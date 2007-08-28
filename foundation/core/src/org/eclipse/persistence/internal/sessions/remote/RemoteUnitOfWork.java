/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.remote;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Counter part of the unit of work which exists on the client side.
 */
public class RemoteUnitOfWork extends UnitOfWorkImpl {
    protected Vector newObjectsCache;
    protected Vector unregisteredNewObjectsCache;
    protected boolean isOnClient;
    protected transient RemoteSessionController parentSessionController;

    public RemoteUnitOfWork(RemoteUnitOfWork parent) {
        super(parent);
        this.isOnClient = true;
    }

    public RemoteUnitOfWork(RemoteSession parent) {
        super(parent);
        this.isOnClient = true;
    }

    /**
     * The nested unit of work must also be remote.
     */
    public UnitOfWorkImpl acquireUnitOfWork() {
        log(SessionLog.FINER, SessionLog.TRANSACTION, "acquire_unit_of_work");
        setNumberOfActiveUnitsOfWork(getNumberOfActiveUnitsOfWork() + 1);
        RemoteUnitOfWork ruow = new RemoteUnitOfWork(this);
        ruow.discoverAllUnregisteredNewObjectsInParent();
        return ruow;
    }

    /**
     * This is done to maintain correspondence between local new objects and returned new objects from serialization.
     * Object correspondence is maintained by comparing primary keys but for new objects it is possible that primary
     * key value is null as it is still not inserted. The returned new objects from serialization will have primary
     * key value which will be inserted into corresponding local new objects.
     */
    protected Vector collectNewObjects() {
        return Helper.buildVectorFromHashtableElements(getNewObjectsCloneToOriginal());
    }

    /**
     * This is done to maintain correspondence between local unregistered new objects and returned unregistered new
     * objects from serialization. Object correspondence is maintained by comparing primary keys but for unregistered
     * new objects it is possible that primary key value is null as it is still not inserted. The returned unregistered
     * new objects from serialization will have primary key value which will be inserted into corresponding local new
     * objects.
     */
    protected Vector collectUnregisteredNewObjects() {
        discoverAllUnregisteredNewObjects();
        return Helper.buildVectorFromHashtableElements(getUnregisteredNewObjects());
    }
    
    /**
     * INTERNAL:
     * This overloading method is required, it forces non-callingSession specified commitTransaction method 
     * being invoked from its parent class, this guarantees the method being invoked from correct parent 
     * class - RemoteSession, which has only one non-callingSession specified commitTrasaction method defined.
     */
    public void commitTransaction() throws DatabaseException {
        getParent().commitTransaction();
    }


    /**
     * The remote unit of work returned after its commit on the server is merged with remote unit of work
     * on the remote side.
     */
    protected void commitIntoRemoteUnitOfWork() {
        // Must merge the transaction flag.
        ((UnitOfWorkImpl)getParent()).setWasTransactionBegunPrematurely(wasTransactionBegunPrematurely());

        MergeManager manager = new MergeManager(this);
        manager.mergeWorkingCopyIntoRemote();

        // Must clone the clone mapping because entries can be added to it during the merging,
        // and that can lead to concurrency problems.
        Enumeration clones = ((IdentityHashtable)getCloneMapping().clone()).keys();

        // Iterate over each clone and let the object build merge to clones into the originals.
        while (clones.hasMoreElements()) {
            manager.mergeChanges(clones.nextElement(), null);
        }
    }

    /**
     * Starts committing the remote unit of work.
     * This must serialize the unit of work across to the server,
     * commit the unit of work on the server,
     * serialize it back and merge any server-side changes (such as sequence numbers) it into itself,
     * then merge into the parent remote session.
     */
    public void commitRootUnitOfWork() {
        if (!isOnClient()) {
            if (isSynchronized()) {
                // If we started the JTS transaction then we have to commit it as well.
                if (getParent().wasJTSTransactionInternallyStarted()) {
                    commitInternallyStartedExternalTransaction();
                }

                // Do not commit until the JTS wants to.
                return;
            }
            getEventManager().preCommitUnitOfWork();
            super.commitRootUnitOfWork();// On the server the normal commit is done.
            getEventManager().postCommitUnitOfWork();
            return;
        }

        // New objects cache is created to maintain the correspondence when they are returned back as differnt copy
        setNewObjectsCache(collectNewObjects());
        // Unregistered new objects cache is created to maintain the correspondence when they are returned back as differnt copy
        setUnregisteredNewObjectsCache(collectUnregisteredNewObjects());

        // Commit on the server
        RemoteUnitOfWork remoteUnitOfWork;
        try {
            remoteUnitOfWork = ((RemoteSession)getParent()).getRemoteConnection().commitRootUnitOfWork(this);
        } catch (RuntimeException exception) {
            // Must ensure remote session transaction mutex is correct.
            if (wasTransactionBegunPrematurely()) {
                getParent().getTransactionMutex().release();
            }
            // If an exception occured, the unit of work will have rolledback the early transaction
            // so must record this incase the unit of work re-commits.
            setWasTransactionBegunPrematurely(false);
            throw exception;
        }

        // Must ensure remote session transaction mutex is correct.
        if (wasTransactionBegunPrematurely()) {
            getParent().getTransactionMutex().release();
        }
        // Make the returned remote unit of work a nested unit of work and merge it with the local remote unit of work
        remoteUnitOfWork.setParent(this);
        remoteUnitOfWork.setProject(getProject());
        remoteUnitOfWork.prepareForMergeIntoRemoteUnitOfWork();
        remoteUnitOfWork.commitIntoRemoteUnitOfWork();
        // Now commit this unit of work to the parent remote session
        commitRootUnitOfWorkOnClient();
    }

    /**
     * Merges remote unit of work to parent remote session.
     */
    protected void commitRootUnitOfWorkOnClient() {
        IdentityHashtable allObjects = collectAndPrepareObjectsForNestedMerge();

        // I must clone because the commitManager will remove the objects from the collection
        // as the objects are written to the database.
        setAllClonesCollection(allObjects);

        //calculate the change set here as we have special behaviour for remote
        // in that the new changesets must be updated within the UOWChangeSet as the
        // primary keys have already been assigned.  This was modified for updating
        // new object change set behaviour.
        UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)getUnitOfWorkChangeSet();
        if (uowChangeSet == null) {
            //may be using the old commit prosess usesOldCommit()
            setUnitOfWorkChangeSet(new UnitOfWorkChangeSet());
            uowChangeSet = (UnitOfWorkChangeSet)getUnitOfWorkChangeSet();
            calculateChanges(getAllClones(), (UnitOfWorkChangeSet)getUnitOfWorkChangeSet(), false);
        }
        Enumeration classes = uowChangeSet.getNewObjectChangeSets().elements();
        while (classes.hasMoreElements()) {
            IdentityHashtable newList = (IdentityHashtable)classes.nextElement();
            Enumeration newChangeSets = newList.keys();
            while (newChangeSets.hasMoreElements()) {
                uowChangeSet.putNewObjectInChangesList((ObjectChangeSet)newChangeSets.nextElement(), this);
            }
        }
        
        //add the deleted objects
        for (Enumeration iterator = getObjectsDeletedDuringCommit().keys(); iterator.hasMoreElements(); ){
            ((UnitOfWorkChangeSet)getUnitOfWorkChangeSet()).addDeletedObject(iterator.nextElement(), this);
        }

        mergeChangesIntoParent();
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The named query can be defined on the remote session or the server-side session.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName) throws DatabaseException {
        return executeQuery(queryName, new Vector(1));
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     * The query is executed on the server-side session.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Class domainClass) throws DatabaseException {
        return executeQuery(queryName, domainClass, new Vector(1));
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Class domainClass, Vector argumentValues) throws DatabaseException {
        RemoteSession remoteSession = null;
        if (getParent().isRemoteSession()) {
            remoteSession = (RemoteSession)getParent();
        } else {//must be remote unit of work
            RemoteUnitOfWork uow = (RemoteUnitOfWork)getParent();
            while (uow.getParent().isRemoteUnitOfWork()) {
                uow = (RemoteUnitOfWork)uow.getParent();
            }
            remoteSession = (RemoteSession)uow.getParent();
        }

        Transporter transporter = remoteSession.getRemoteConnection().remoteExecuteNamedQuery(queryName, domainClass, argumentValues);
        transporter.getQuery().setSession(this);
        return transporter.getQuery().extractRemoteResult(transporter);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Vector argumentValues) throws DatabaseException {
        if (containsQuery(queryName)) {
            return super.executeQuery(queryName, argumentValues);
        }
        return executeQuery(queryName, null, argumentValues);
    }

    /**
     * Return the table descriptor specified for the class.
     */
    public ClassDescriptor getDescriptor(Class domainClass) {
        return getParent().getDescriptor(domainClass);
    }

    /**
     * Returns a new object cache
     */
    public Vector getNewObjectsCache() {
        return newObjectsCache;
    }

    /**
     * INTERNAL:
     * Method returns the parent RemoteSessionController for this Remote UnitOfWork
     * Used to retrieve Valueholders that were used on the client
     */
    public RemoteSessionController getParentSessionController() {
        return this.parentSessionController;
    }

    /**
     * INTERNAL:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavoir.
     */
    public DatabasePlatform getPlatform() {
        return getParent().getPlatform();
    }

    /**
     * INTERNAL:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavoir.
     */
    public Platform getDatasourcePlatform() {
        return getParent().getDatasourcePlatform();
    }

    /**
     * Returns an unregistered new object cache
     */
    public Vector getUnregisteredNewObjectsCache() {
        return unregisteredNewObjectsCache;
    }

    /**
     * INTERNAL:
     * Return the results from exeucting the database query.
     * the arguments should be a database row with raw data values.
     */
    public Object internalExecuteQuery(DatabaseQuery query, AbstractRecord Record) throws DatabaseException, QueryException {
        if (!isActive()) {
            throw QueryException.querySentToInactiveUnitOfWork(query);
        }
        if (isOnClient()) {
            //assert !getCommitManager().isActive();
            // This will either throw an exception or do a logic only operation
            // (i.e. mark for later deletion if a deleteObjet query).
            boolean objectLevelRead = (query.isObjectLevelReadQuery() && !query.isReportQuery() && query.shouldMaintainCache());
            if (objectLevelRead) {
                ObjectLevelReadQuery readQuery = (ObjectLevelReadQuery)query;
                if (isAfterWriteChangesButBeforeCommit()) {
                    throw ValidationException.illegalOperationForUnitOfWorkLifecycle(getLifecycle(), "executeQuery(ObjectLevelReadQuery)");
                }
                Object result = readQuery.checkEarlyReturn(this, Record);

                if (result != null) {
                    if (result == InvalidObject.instance) {
                        return null;
                    }
                    return result;
                }

                // Must use the uow connection in these cases.
                // can be certain that commit manager not active as on client.
                if (readQuery.isLockQuery(this) && !wasTransactionBegunPrematurely()) {
                    beginEarlyTransaction();
                }
            } else if (query.isObjectLevelModifyQuery()) {
                return query.executeInUnitOfWork(this, Record);
            }

            // Starting a transaction early when on a remote UnitOfWork starts
            // a transaction on the server side client session, and all queries
            // when they arrive they will go down the write connection.

            /* Fix to allow executing non-selecting SQL in a UnitOfWork. - RB */
            if ((!getCommitManager().isActive()) && query.isDataModifyQuery()) {
                if (!wasTransactionBegunPrematurely()) {
                    beginEarlyTransaction();
                }
            }
            Object result = getParent().executeQuery(query, Record);

            if (objectLevelRead) {
                result = ((ObjectLevelReadQuery)query).registerResultInUnitOfWork(result, this, Record, false);
            }
            return result;
        }
        return query.executeInUnitOfWork(this, Record);
    }

    protected boolean isOnClient() {
        return isOnClient;
    }

    /**
     * Return if this session is a unit of work.
     */
    public boolean isRemoteUnitOfWork() {
        return true;
    }

    /**
     * The returned remote unit of work from the server is prepared to merge with local remote unit of work.
     */
    protected void prepareForMergeIntoRemoteUnitOfWork() {
        IdentityHashtable originalToClone = new IdentityHashtable();
        IdentityHashtable cloneToOriginal = new IdentityHashtable();

        // For new and unregistered objects the clone from the parent remote unit of work is picked and store as original
        // in the remote unit of work. This is done so that changes are merged into the clone of the parent.
        Enumeration returnedNewObjects = getNewObjectsCache().elements();

        // For new and unregistered objects the clone from the parent remote unit of work is picked and store as original
        // in the remote unit of work. This is done so that changes are merged into the clone of the parent.
        Enumeration newObjects = ((RemoteUnitOfWork)getParent()).getNewObjectsCache().elements();

        for (; returnedNewObjects.hasMoreElements();) {
            Object cloneFromParent = ((RemoteUnitOfWork)getParent()).getNewObjectsOriginalToClone().get(newObjects.nextElement());
            Object cloneFromSelf = getNewObjectsOriginalToClone().get(returnedNewObjects.nextElement());
            if (cloneFromSelf != null) {
                originalToClone.put(cloneFromParent, cloneFromSelf);
                cloneToOriginal.put(cloneFromSelf, cloneFromParent);
            }
        }

        Enumeration returnedUnregisteredNewObjects = getUnregisteredNewObjectsCache().elements();
        Enumeration unregisteredNewObjects = ((RemoteUnitOfWork)getParent()).getUnregisteredNewObjectsCache().elements();

        for (; returnedUnregisteredNewObjects.hasMoreElements();) {
            Object cloneFromParent = ((RemoteUnitOfWork)getParent()).getUnregisteredNewObjects().get(unregisteredNewObjects.nextElement());
            Object cloneFromSelf = getUnregisteredNewObjects().get(returnedUnregisteredNewObjects.nextElement());
            originalToClone.put(cloneFromParent, cloneFromSelf);
            cloneToOriginal.put(cloneFromSelf, cloneFromParent);
        }

        setNewObjectsOriginalToClone(originalToClone);
        setNewObjectsCloneToOriginal(cloneToOriginal);

        // Get the corresponding deleted objects from the original remote unit of work,
        // and set them in the remote unit of work, this is the parent.
        IdentityHashtable objectsDeletedDuringCommit = new IdentityHashtable();
        for (Enumeration deletedObjects = getObjectsDeletedDuringCommit().keys();
                 deletedObjects.hasMoreElements();) {
            Object deletedObject = deletedObjects.nextElement();
            Vector primaryKey = keyFromObject(deletedObject);
            Object cloneFromParent = getParent().getIdentityMapAccessor().getFromIdentityMap(primaryKey, deletedObject.getClass());

            // The original may be a new object, or read on the server.
            if (cloneFromParent == null) {
                cloneFromParent = cloneToOriginal.get(deletedObject);
                // This means read on the server, so not on client, so use the same one.
                if (cloneFromParent == null) {
                    cloneFromParent = deletedObject;
                }
            }

            objectsDeletedDuringCommit.put(cloneFromParent, keyFromObject(cloneFromParent));
            ((UnitOfWorkImpl)getParent()).getIdentityMapAccessor().removeFromIdentityMap(cloneFromParent);
        }
        ((UnitOfWorkImpl)getParent()).setObjectsDeletedDuringCommit(objectsDeletedDuringCommit);
    }

    /**
     * INTERNAL:
     * Re-initialize for the server-side session.
     * This is done when the uow is passed back to the server for committing.
     */
    public void reinitializeForSession(AbstractSession session, RemoteSessionController parentSessionController) {
        // If a server, acquire a client to commit into as client store connection for commit.
        if (session.isServerSession()) {
            session = ((org.eclipse.persistence.sessions.server.ServerSession)session).acquireClientSession();
        }
        setIsOnClient(false);
        setParentSessionController(parentSessionController);
        setParent(session);
        setProject(session.getProject());
        setProfiler(getProfiler());
        setEventManager(session.getEventManager().clone(this));
        //	setShouldLogMessages(session.shouldLogMessages());
        setSessionLog(session.getSessionLog());
        setLog(session.getLog());
        // These are transient so must be reset.
        setCommitManager(new CommitManager(this));
        setTransactionMutex(new ConcurrencyManager());
        getCommitManager().setCommitOrder(session.getCommitManager().getCommitOrder());

        if (getParent().hasExternalTransactionController()) {
            getParent().getExternalTransactionController().registerSynchronizationListener(this, getParent());
        }
    }
    
    /**
     * INTERNAL:
     * This overloading method is required, it forces non-callingSession specified rollbackTransaction method 
     * being invoked from its parent class, this guarantees the method being invoked from correct parent 
     * class - RemoteSession, which has only one non-callingSession specified rollbackTransaction method defined.
     */
    public void rollbackTransaction() throws DatabaseException {
        incrementProfile(SessionProfiler.UowRollbacks);
        getParent().rollbackTransaction();
    }

    protected void setIsOnClient(boolean isOnClient) {
        this.isOnClient = isOnClient;
    }

    /**
     * Set a new object cache
     */
    protected void setNewObjectsCache(Vector newObjectsCache) {
        this.newObjectsCache = newObjectsCache;
    }

    /**
     * INTERNAL:
     * Sets the parent RemoteSessionController for this Remote UnitOfWork
     * Used to retrieve Valueholders that were used on the client
     */
    public void setParentSessionController(RemoteSessionController parentSessionController) {
        this.parentSessionController = parentSessionController;
    }

    /**
     * Set unregistered new object cache
     */
    protected void setUnregisteredNewObjectsCache(Vector unregisteredNewObjectsCache) {
        this.unregisteredNewObjectsCache = unregisteredNewObjectsCache;
    }

    /**
     * Avoid the toString printing the accessor and platform.
     */
    public String toString() {
        return Helper.getShortClassName(getClass()) + "()";
    }

    /**
     * TESTING:
     * This is used by testing code to ensure that a deletion was successful.
     */
    public boolean verifyDelete(Object domainObject) {
        return getParent().verifyDelete(domainObject);
    }
}