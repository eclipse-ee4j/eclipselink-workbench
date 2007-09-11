/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

/**
 * <p>
 * <b>Purpose</b>: Hold the Records of change for a particular instance of an object.
 * <p>
 * <b>Description</b>: This class uses the Primary Keys of the Object it represents,
 * and the class.
 * <p>
 */
public class ObjectChangeSet implements Serializable, org.eclipse.persistence.sessions.changesets.ObjectChangeSet {

    /** This is the collection of changes */
    protected Vector changes;
    protected Hashtable attributesToChanges;
    protected boolean shouldBeDeleted;
    protected CacheKey cacheKey;
    protected transient Class classType;
    protected String className;
    protected boolean isNew;
    protected boolean isAggregate;
    protected Object oldKey;
    protected Object newKey;

    /** This member variable holds the reference to the parent UnitOfWork Change Set **/
    protected UnitOfWorkChangeSet unitOfWorkChangeSet;
    /** Used in mergeObjectChanges method for writeLock and initialWriteLock comparison of the merged change sets **/
    protected transient OptimisticLockingPolicy optimisticLockingPolicy;
    protected Object initialWriteLockValue;
    protected Object writeLockValue;
    /** Invalid change set shouldn't be merged into object in cache, rather the object should be invalidated **/
    protected boolean isInvalid;
    protected transient Object cloneObject;
    protected boolean hasVersionChange;
    /** Contains optimisticReadLockObject corresponding to the clone, non-null indicates forced changes **/
    protected Boolean shouldModifyVersionField;
    /** For CMP only: indicates that the object should be force updated (whether it has OptimisticLocking or not): getCmpPolicy().getForcedUpdate()==true**/
    protected boolean hasCmpPolicyForcedUpdate;
    protected boolean hasChangesFromCascadeLocking;
    
    /**
     * This is used during attribute level change tracking when a particular
     * change was detected but that change can not be tracked (ie customer set
     * entire collection in object).
     */
    protected transient HashSet deferredSet;

    /**
     * Used to store the type of cache synchronization used for this object
     * This variable is set just before the change set is serialized.
     */
    protected int cacheSynchronizationType = ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR;

    // uses to map SDK project
    protected int id = -1;
    
    /** PERF: Cache the session cacheKey during thr merge to avoid duplicate lookups. */
    protected transient CacheKey activeCacheKey;

    /**
     * The default constructor is used only by SDK XML project for mapping ObjectChangeSet
     */
    public ObjectChangeSet() {
        super();
    }

    /**
     * This constructor is used to create an ObjectChangeSet that represents an aggregate object.
     */
    public ObjectChangeSet(Object cloneObject, UnitOfWorkChangeSet parent, boolean isNew) {
        this.cloneObject = cloneObject;
        this.shouldBeDeleted = false;
        this.classType = cloneObject.getClass();
        this.className = this.classType.getName();
        this.unitOfWorkChangeSet = parent;
        this.isNew = isNew;
    }

    /**
     * This constructor is used to create an ObjectChangeSet that represents a regular object.
     */
    public ObjectChangeSet(Vector primaryKey, Class classType, Object cloneObject, UnitOfWorkChangeSet parent, boolean isNew) {
        super();
        this.cloneObject = cloneObject;
        this.isNew = isNew;
        this.shouldBeDeleted = false;
        if ((primaryKey != null) && !org.eclipse.persistence.internal.helper.Helper.containsNull(primaryKey, 0)) {
            this.cacheKey = new CacheKey(primaryKey);
        }
        this.classType = classType;
        this.className = this.classType.getName();
        this.unitOfWorkChangeSet = parent;
        this.isAggregate = false;
    }

    /**
     * INTERNAL:
     * This method will clear the changerecords from a changeSet
     */
    public void clear() {
        this.shouldBeDeleted = false;
        this.setOldKey(null);
        this.setNewKey(null);
        this.changes = null;
        this.attributesToChanges = null;
    }

    /**
     * Add the attribute change record.
     */
    public void addChange(ChangeRecord changeRecord) {
        if (changeRecord == null) {
            return;
        }
        String attributeName = changeRecord.getAttribute();
        Map attributeToChanges = getAttributesToChanges();
        List changes = getChanges();
        ChangeRecord existingChangeRecord = (ChangeRecord)attributeToChanges.get(attributeName);
        // change tracking may add a change to an existing attribute fix that here.
        if (existingChangeRecord != null) {
            changes.remove(existingChangeRecord);
        }
        changes.add(changeRecord);
        attributeToChanges.put(attributeName, changeRecord);
        dirtyUOWChangeSet();
    }

    /**
     * INTERNAL:
     * This method is used during attribute level change tracking when a particular
     * change was detected but that change can not be tracked (ie customer set
     * entire collection in object).  In this case flag this attribute for 
     * deferred change detection at commit time.
     */
    public void deferredDetectionRequiredOn(String attributeName){
        getDeferredSet().add(attributeName);
    }
    
    /**
     * INTERNAL:
     * Convenience method used to query this change set after it has been sent by
     * cache synchronization.
     * @return true if this change set should contain all change information, false if only
     * the identity information should be available.
     */
    public boolean containsChangesFromSynchronization() {
        return ((cacheSynchronizationType == ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES) || (cacheSynchronizationType == ClassDescriptor.SEND_OBJECT_CHANGES));
    }

    /**
     * Ensure change sets with the same primary key are equal.
     */
    public boolean equals(Object object) {
        if (object instanceof ObjectChangeSet) {
            return equals((ObjectChangeSet)object);
        }
        return false;
    }

    /**
     * Ensure change sets with the same primary key are equal.
     */
    public boolean equals(org.eclipse.persistence.sessions.changesets.ObjectChangeSet objectChange) {
        if (this == objectChange) {
            return true;
        } else if (getCacheKey() == null) {
            //new objects are compared based on identity
            return false;
        }

        return (getCacheKey().equals(((ObjectChangeSet)objectChange).getCacheKey()));
    }

    /**
     * INTERNAL:
     * stores the change records indexed by the attribute names
     */
    public Hashtable getAttributesToChanges() {
        if (this.attributesToChanges == null) {
            this.attributesToChanges = new Hashtable(10);
        }
        return this.attributesToChanges;
    }

    /**
     * INTERNAL:
     * returns the change record for the specified attribute name
     */
    public org.eclipse.persistence.sessions.changesets.ChangeRecord getChangesForAttributeNamed(String attributeName) {
        return (ChangeRecord)this.getAttributesToChanges().get(attributeName);
    }

    /**
     * Return the CacheKey for the object that this is a change set for.
     */
    public CacheKey getCacheKey() {
        // this must not be lazy initialized as newness of the ObjectChangeSet and
        //equality are determined by the existence of a cachekey - GY
        return cacheKey;
    }

    /**
     * ADVANCED:
     * This method will return a collection of the attributes changed in the object.
     */
    public Vector getChangedAttributeNames() {
        Vector names = new Vector();
        Enumeration attributes = getChanges().elements();
        while (attributes.hasMoreElements()) {
            names.addElement(((ChangeRecord)attributes.nextElement()).getAttribute());
        }
        return names;
    }

    /**
     * INTERNAL:
     * This method returns a reference to the collection of changes within this changeSet.
     */
    public Vector getChanges() {
        if (this.changes == null) {
            this.changes = new Vector();
        }
        return changes;
    }

    /**
     * INTERNAL:
     * This method returns the class type that this changeSet represents.
     * The class type must be initialized, before this method is called.
     * @return java.lang.Class or null if the class type isn't initialized.
     */
    public Class getClassType() {
        return classType;
    }

    /**
     * ADVANCE:
     * This method returns the class type that this changeSet Represents.
     * This requires the session to reload the class on serialization.
     */
    public Class getClassType(org.eclipse.persistence.sessions.Session session) {
        if (classType == null) {
            classType = (Class)((AbstractSession)session).getDatasourcePlatform().getConversionManager().convertObject(getClassName(), ClassConstants.CLASS);
        }
        return classType;
    }

    /**
     * ADVANCE:
     * This method returns the class type that this changeSet Represents.
     * The class type should be used if the class is desired.
     */
    public String getClassName() {
        return className;
    }

    /**
     * INTERNAL:
     * This method is used to return the initial lock value of the object this changeSet represents.
     */
    public Object getInitialWriteLockValue() {
        return initialWriteLockValue;
    }

    /**
     * This method returns the key value that this object was stored under in it's
     * Respective hashmap.
     */
    public Object getOldKey() {
        return this.oldKey;
    }

    /**
     * This method returns the key value that this object will be stored under in it's
     * Respective hashmap.
     */
    public Object getNewKey() {
        return this.newKey;
    }

    /**
     * ADVANCED:
     * This method returns the primary keys for the object that this change set represents.
     */
    public Vector getPrimaryKeys() {
        if (getCacheKey() == null) {
            return null;
        }
        return getCacheKey().getKey();
    }

    public int getSynchronizationType() {
        return cacheSynchronizationType;
    }

    /**
     * INTERNAL:
     * This method is used to return the complex object specified within the change record.
     * The object is collected from the session which, in this case, is the unit of work.
     * The object's changed attributes will be merged and added to the identity map.
     */
    public Object getTargetVersionOfSourceObject(AbstractSession session) {
        return getTargetVersionOfSourceObject(session, false);
    }

    /**
     * INTERNAL:
     * This method is used to return the complex object specified within the change record.
     * The object is collected from the session which, in this case, is the unit of work.
     * The object's changed attributes will be merged and added to the identity map
     * @param shouldRead boolean if the object can not be found should it be read in from the database.
     */
    public Object getTargetVersionOfSourceObject(AbstractSession session, boolean shouldRead) {
        Object attributeValue = null;
        ClassDescriptor descriptor = session.getDescriptor(getClassType(session));

        if (descriptor != null) {
            // Rebuild PK to expected type for identity lookup only if chang set was converted to user format such as XML  
            // Note that other conversions of changed attributes will be handled by each mapping during merging.
            // Obsolete SDK cache synch code.
            if ((session.getCommandManager() != null) && (session.getCommandManager().getCommandConverter() != null)) {
                rebuildPKFromUserFormat(descriptor, session);
            }

            if (session.isUnitOfWork()) {
                // The unit of works will have a copy or a new instance must be made
                if (((UnitOfWorkImpl)session).getLifecycle() == UnitOfWorkImpl.MergePending) {
                    // We are merging the unit of work into the original.
                    attributeValue = ((UnitOfWorkImpl)session).getOriginalVersionOfObjectOrNull(getUnitOfWorkClone(), this, descriptor);
                } else {
                    // We are merging something else within the unit of work.
                    // this is most likely because we are updating a backup clone and can retrieve
                    // the working clone as the result.
                    attributeValue = getUnitOfWorkClone();
                }
            } else {
                // It is not a unitOfWork so we must be merging into a distributed cache.
                attributeValue = session.getIdentityMapAccessorInstance().getIdentityMapManager().getFromIdentityMap(getPrimaryKeys(), getClassType(session), descriptor);
            }
        
            if ((attributeValue == null) && (shouldRead)) {
                // If the cache does not have a copy and I should read it from the database
                // Then load the object if possible
                ReadObjectQuery query = new ReadObjectQuery();
                query.setShouldUseWrapperPolicy(false);
                query.setReferenceClass(getClassType(session));
                query.setSelectionKey(getPrimaryKeys());
                attributeValue = session.executeQuery(query);
            }
        }
        
        return attributeValue;
    }

    /**
     * INTERNAL:
     * Returns the UnitOfWork Clone that this ChangeSet was built for.
     */
    public Object getUnitOfWorkClone() {
        return this.cloneObject;
    }

    /**
     * ADVANCED:
     * This method is used to return the parent UnitOfWorkChangeSet.
     */
    public org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet getUOWChangeSet() {
        return unitOfWorkChangeSet;
    }

    /**
     * INTERNAL:
     * This method is used to return the lock value of the object this changeSet represents.
     */
    public Object getWriteLockValue() {
        return writeLockValue;
    }

    /**
     * ADVANCED:
     * This method will return true if the specified attributue has been changed.
     * @param attributeName the name of the attribute to search for.
     */
    public boolean hasChangeFor(String attributeName) {
        Enumeration attributes = getChanges().elements();
        while (attributes.hasMoreElements()) {
            if (((ChangeRecord)attributes.nextElement()).getAttribute().equals(attributeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ADVANCED:
     * Returns true if this particular changeSet has changes.
     */
    public boolean hasChanges() {
        // a change set must also be considered dirty if only the version number has been updated
        // and the version is not a mapped field.  This is required to propagate the change
        // set via cache sync. to avoid opt. lock exceptions on the remote servers.
        return this.hasVersionChange || ((this.changes != null) && (!this.changes.isEmpty()));
    }

    /**
     * INTERNAL:
     * Returns true if this particular changeSet has forced SQL changes.  This is true whenever
     * CMPPolicy.getForceUpdate() == true or if the object has been marked for opt. read
     * lock (uow.forceUpdateToVersionField).  Kept separate from 'hasChanges' because we don't
     * want to merge or cache sync. a change set that has no 'real' changes.
     */
    public boolean hasForcedChanges() {
        return this.shouldModifyVersionField != null || this.hasCmpPolicyForcedUpdate;
    }
    
    /**
     * INTERNAL:
     * Holds a Boolean indicating whether version field should be modified.
     * This Boolean is set by forcedUpdate into uow.getOptimisticReadLockObjects() 
     * for the clone object and copied here (so don't need to search for it again
     * in uow.getOptimisticReadLockObjects()).
     */
    public void setShouldModifyVersionField(Boolean shouldModifyVersionField) {
        this.shouldModifyVersionField = shouldModifyVersionField;
        if(shouldModifyVersionField != null && shouldModifyVersionField.booleanValue()) {
            // mark the version number as 'dirty'
            // Note that at this point there is no newWriteLockValue - it will be set later.
            // This flag is set to indicate that the change set WILL have changes.
            this.hasVersionChange = true;
        }
    }
    
    /**
     * INTERNAL:
     * Holds a Boolean indicating whether version field should be modified.
     */
    public Boolean shouldModifyVersionField() {
        return this.shouldModifyVersionField;
    }
    
    /**
     * INTERNAL:
     */
    public void setHasCmpPolicyForcedUpdate(boolean hasCmpPolicyForcedUpdate) {
        this.hasCmpPolicyForcedUpdate = hasCmpPolicyForcedUpdate;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasCmpPolicyForcedUpdate() {
        return this.hasCmpPolicyForcedUpdate;
    }
    
    /**
     * INTERNAL:
     * Returns true if this particular changeSet has forced SQL changes because 
     * of a cascade optimistic locking policy.
     */
    public boolean hasForcedChangesFromCascadeLocking() {
        return this.hasChangesFromCascadeLocking;
    }

    /**
     * INTERNAL:
     * Used by calculateChanges to mark this ObjectChangeSet as having to be 
     * flushed to the db steming from a cascade optimistic locking policy.
     */
    public void setHasForcedChangesFromCascadeLocking(boolean newValue) {
        this.setShouldModifyVersionField(Boolean.TRUE);
        this.hasChangesFromCascadeLocking = newValue;
    }

    /**
     * This method overrides the hashcode method.  If this set has a cacheKey then return the hashcode of the
     * cache key, otherwise return the identity hashcode of this object.
     */
    public int hashCode() {
        if (getCacheKey() == null) {
            //new objects are compared based on identity
            return System.identityHashCode(this);
        }
        return getCacheKey().hashCode();
    }

    /**
     * INTERNAL:
     * Returns true if this particular changeSet has a Key.
     */
    public boolean hasKeys() {
        return (this.newKey != null) || (this.oldKey != null);
    }

    /**
     * INTERNAL:
     * Used to determine if the object change set represents an aggregate object.
     */
    public boolean isAggregate() {
        return isAggregate;
    }

    /**
     * ADVANCED:
     * Returns true if this ObjectChangeSet represents a new object.
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * INTERNAL:
     * Indicates whether the change set is invalid.
     */
    public boolean isInvalid() {
        return isInvalid;
    }

    /**
     * INTERNAL:
     * This method will be used to merge changes from a supplied ObjectChangeSet
     * into this changeSet.
     */
    public void mergeObjectChanges(ObjectChangeSet changeSetToMergeFrom, UnitOfWorkChangeSet mergeToChangeSet, UnitOfWorkChangeSet mergeFromChangeSet) {
        if (this == changeSetToMergeFrom || this.isInvalid()) {
            return;
        }
        if(changeSetToMergeFrom.optimisticLockingPolicy != null) {
            // optimisticLockingPolicy != null guarantees initialWriteLockValue != null
            if(this.optimisticLockingPolicy == null) {
                this.optimisticLockingPolicy = changeSetToMergeFrom.optimisticLockingPolicy;
                this.initialWriteLockValue = changeSetToMergeFrom.initialWriteLockValue;
                this.writeLockValue = changeSetToMergeFrom.writeLockValue;
            } else {
                // optimisticLockingPolicy != null guarantees initialWriteLockValue != null
                Object writeLockValueToCompare = this.writeLockValue;
                if(writeLockValueToCompare == null) {
                    writeLockValueToCompare = this.initialWriteLockValue;
                }
                // In this merge initialWriteLockValue of this changeSet differes from 
                // writeLockValue of the changeSetToMergeFrom into which the merge was performed.
                // Example:
                // Original registered with version 1, the clone changed to version 2, uow.writeChanges is called:
                // the corresponding "this" changeSet has initialWriteLockValue = 1 and writeLockValue = 2;
                // custom update performed next changing the version of the object in the db to 3;
                // the clone is refreshed in the uow - not it's version is 3;
                // the cloned is changed to version 4, uow.commit is called:
                // the corresponding changeSetToMergeFrom has initialWriteLockValue = 3 and writeLockValue = 4.
                // This change set should be invalidated - the custom update would not be reflected after merge,
                // therefore nor merge into cache should be performed but rather the object in the cache should be invalidated.
                if(this.optimisticLockingPolicy.compareWriteLockValues(writeLockValueToCompare, changeSetToMergeFrom.initialWriteLockValue) != 0) {
                    this.isInvalid = true;
                    return;
                }
                this.writeLockValue = changeSetToMergeFrom.writeLockValue;
            }
        }
        List changesToMerge = changeSetToMergeFrom.getChanges();
        int size = changesToMerge.size();
        for (int index = 0; index < size; ++index) {
            ChangeRecord record = (ChangeRecord)changesToMerge.get(index);
            ChangeRecord thisRecord = (ChangeRecord) getChangesForAttributeNamed(record.getAttribute());
            if (thisRecord == null) {
                record.updateReferences(mergeToChangeSet, mergeFromChangeSet);
                record.setOwner(this);
                this.addChange(record);
            } else {
                thisRecord.mergeRecord(record, mergeToChangeSet, mergeFromChangeSet);
            }
        }
        this.shouldBeDeleted = changeSetToMergeFrom.shouldBeDeleted;
        this.setOldKey(changeSetToMergeFrom.oldKey);
        this.setNewKey(changeSetToMergeFrom.newKey);
        this.hasVersionChange = changeSetToMergeFrom.hasVersionChange;
        this.shouldModifyVersionField = changeSetToMergeFrom.shouldModifyVersionField;
        this.hasCmpPolicyForcedUpdate = changeSetToMergeFrom.hasCmpPolicyForcedUpdate;
        this.hasChangesFromCascadeLocking = changeSetToMergeFrom.hasChangesFromCascadeLocking;
        this.id = changeSetToMergeFrom.id;
    }

    /**
     * INTERNAL:
     * Iterate through the change records and ensure the cache synchronization types
     * are set on the change sets associated with those records.
     */
    public void prepareChangeRecordsForSynchronization(AbstractSession session) {
        Enumeration records = getChanges().elements();
        while (records.hasMoreElements()) {
            ((ChangeRecord)records.nextElement()).prepareForSynchronization(session);
        }
    }

    /**
     * INTERNAL:
     * Helper method used by readObject to read a completely serialized change set from
     * the stream.
     */
    public void readCompleteChangeSet(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        readIdentityInformation(stream);
        // bug 3526981 - avoid side effects of setter methods by directly assigning variables
        // still calling setOldKey to avoid duplicating the code in that method
        changes = (Vector)stream.readObject();
        setOldKey(stream.readObject());
        newKey = stream.readObject();
    }

    /**
     * INTERNAL:
     * Helper method used by readObject to read just the information about object identity
     * from a serialized stream.
     */
    public void readIdentityInformation(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        // bug 3526981 - avoid side effects of setter methods by directly assigning variables
        cacheKey = (CacheKey)stream.readObject();
        className = (String)stream.readObject();
        writeLockValue = stream.readObject();
    }

    /**
     * INTERNAL:
     * Override the default serialization.  Object Change Sets will be serialized differently
     * depending on the type of cache synchronization they use.
     */
    private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        int cacheSyncType = stream.read();
        setSynchronizationType(cacheSyncType);
        // The boolean variables have been assembled into a byte.
        // Extract them here
        setShouldBeDeleted(stream.readBoolean());
        this.isInvalid = stream.readBoolean();
        setIsNew(stream.readBoolean());
        setIsAggregate(stream.readBoolean());
        setId(stream.readInt());

        // Only the identity information is sent with a number of cache synchronization types
        // Here we decide what to read.
        if (shouldBeDeleted || (cacheSynchronizationType == ClassDescriptor.DO_NOT_SEND_CHANGES) || (cacheSynchronizationType == ClassDescriptor.INVALIDATE_CHANGED_OBJECTS)) {
            readIdentityInformation(stream);
        } else {
            readCompleteChangeSet(stream);
        }

        // bug 3526981 - ensure the UOWChangeSet is updated.  Required since we removed calls to methods that
        // did this in readIdentityInformation and readCompleteChangeSet.
        updateUOWChangeSet();
    }

    /**
     * Set the cache key.
     */
    public void setCacheKey(CacheKey cacheKey) {
        this.cacheKey = cacheKey;
    }

    /**
     * Set the changes.
     */
    public void setChanges(Vector changesList) {
        this.changes = changesList;
        updateUOWChangeSet();
    }

    /**
     * Set the class type.
     */
    public void setClassType(Class newValue) {
        this.classType = newValue;
    }

    /**
     * INTERNAL:
     * Set the class name.  The name is used for serialization with cache coordination.
     */
    public void setClassName(String newValue) {
        this.className = newValue;
    }

    /**
     * INTERNAL:
     * Set if this object change Set represents an aggregate
     * @param isAggregate boolean true if the ChangeSet represents an aggregate
     */
    public void setIsAggregate(boolean isAggregate) {
        this.isAggregate = isAggregate;
    }

    /**
     * INTERNAL:
     * Set whether this ObjectChanges represents a new Object
     * @param newIsNew boolean true if this ChangeSet represents a new object
     */
    protected void setIsNew(boolean newIsNew) {
        isNew = newIsNew;
    }

    /**
     * This method is used to set the value that this object was stored under in its respected
     * map collection
     */
    public void setOldKey(Object key) {
        //may be merging changeSets lets make sure that we can remove based on the
        //old key when we finally merge.
        if ((key == null) || (this.oldKey == null)) {
            this.oldKey = key;
        }
    }

    /**
     * This method is used to set the value that this object will be stored under in its respected
     * map collection
     */
    public void setNewKey(Object key) {
        this.newKey = key;
    }

    /**
     * This method was created in VisualAge.
     * @param newValue boolean
     */
    public void setShouldBeDeleted(boolean newValue) {
        this.shouldBeDeleted = newValue;
    }

    public void setSynchronizationType(int type) {
        cacheSynchronizationType = type;
    }

    /**
     * INTERNAL:
     * Used to set the parent change Set
     * @param newUnitOfWorkChangeSet prototype.changeset.UnitOfWorkChangeSet
     */
    public void setUOWChangeSet(UnitOfWorkChangeSet newUnitOfWorkChangeSet) {
        unitOfWorkChangeSet = newUnitOfWorkChangeSet;
    }

    /**
     * INTERNAL:
     * This method should ONLY be used to set the initial writeLock value for
     * an ObjectChangeSet when it is first built.
     */
    public void setOptimisticLockingPolicyAndInitialWriteLockValue(OptimisticLockingPolicy optimisticLockingPolicy, AbstractSession session) {
        // ignore optimistic locking policy if it can't compare lock values (like FieldsLockingPolicy).
        if(optimisticLockingPolicy.supportsWriteLockValuesComparison()) {
            this.optimisticLockingPolicy = optimisticLockingPolicy;
            this.initialWriteLockValue = optimisticLockingPolicy.getWriteLockValue(cloneObject, getPrimaryKeys(), session);
        }
    }

    /**
     * ADVANCED:
     * This method is used to set the writeLock value for an ObjectChangeSet
     * Any changes to the write lock value
     * should to through setWriteLockValue(Object obj) so that th change set is
     * marked as being dirty.
     * @param newWriteLockValue java.lang.Object
     */
    public void setWriteLockValue(java.lang.Object newWriteLockValue) {
        this.writeLockValue = newWriteLockValue;

        // mark the version number as 'dirty'
        this.hasVersionChange = true;
        updateUOWChangeSet();
    }

    /**
     * ADVANCED:
     * This method is used to set the initial writeLock value for an ObjectChangeSet.
     * The initial value will only be set once, and can not be overwritten.
     * @param initialWriteLockValue java.lang.Object
     */
    public void setInitialWriteLockValue(java.lang.Object initialWriteLockValue) {
        if (this.initialWriteLockValue == null) {
            this.initialWriteLockValue = initialWriteLockValue;
        }
    }

    /**
     * This method was created in VisualAge.
     * @return boolean
     */
    public boolean shouldBeDeleted() {
        return shouldBeDeleted;
    }

    public String toString() {
        return this.getClass().getName() + "(" + this.getClassName() + ")" + getChanges().toString();
    }

    /**
     * INTERNAL:
     * Used to update a changeRecord that is stored in the CHangeSet with a new value.
     */
    public void updateChangeRecordForAttribute(String attributeName, Object value) {
        ChangeRecord changeRecord = (ChangeRecord)getChangesForAttributeNamed(attributeName);
        if (changeRecord != null) {
            changeRecord.updateChangeRecordWithNewValue(value);
        }
    }

    /**
     * ADVANCED:
     * Used to update a changeRecord that is stored in the CHangeSet with a new value.
     * Used when the new value is a mapped object.
     */
    public void updateChangeRecordForAttributeWithMappedObject(String attributeName, Object value, AbstractSession session) {
        ObjectChangeSet referenceChangeSet = (ObjectChangeSet)this.getUOWChangeSet().getObjectChangeSetForClone(value);
        if (referenceChangeSet == null) {
            ClassDescriptor descriptor = session.getDescriptor(value.getClass());
            if (descriptor != null) {
                referenceChangeSet = descriptor.getObjectBuilder().createObjectChangeSet(value, (UnitOfWorkChangeSet)this.getUOWChangeSet(), false, session);
            }
        }
        updateChangeRecordForAttribute(attributeName, referenceChangeSet);
    }

    /**
     * INTERNAL:
     * Used to update a changeRecord that is stored in the CHangeSet with a new value.
     */
    public void updateChangeRecordForAttribute(DatabaseMapping mapping, Object value, AbstractSession session) {
        String attributeName = mapping.getAttributeName();
        ChangeRecord changeRecord = (ChangeRecord)getChangesForAttributeNamed(attributeName);

        // bug 2641228 always ensure that we convert the value to the correct type
        if (mapping.isDirectToFieldMapping()) {
            value = ((AbstractDirectMapping)mapping).getAttributeValue(value, session);
        }
        if (changeRecord != null) {
            changeRecord.updateChangeRecordWithNewValue(value);
        } else if (mapping.isDirectToFieldMapping()) {
            // If it is direct to field then this is most likely the result of a forced update and
            // we will need to merge this object.
            changeRecord = new DirectToFieldChangeRecord(this);
            changeRecord.setAttribute(attributeName);
            changeRecord.setMapping(mapping);
            ((DirectToFieldChangeRecord)changeRecord).setNewValue(value);
            this.addChange(changeRecord);
        }
    }

    /**
     * INTERNAL:
     * This method will be used when merging changesets into other changesets.
     * It will fix references within a changeSet so that it's records point to
     * changesets within this UOWChangeSet.
     */
    public void updateReferences(UnitOfWorkChangeSet localChangeSet, UnitOfWorkChangeSet mergingChangeSet) {
        for (int index = 0; index < this.getChanges().size(); ++index) {
            ChangeRecord record = (ChangeRecord)this.getChanges().get(index);
            record.updateReferences(localChangeSet, mergingChangeSet);
            record.setOwner(this);
        }
    }

    /**
     * INTERNAL:
     * Override the default serialization since different parts of an ObjectChangeSet will
     * be serialized depending on the type of CacheSynchronizationType
     */
    private void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
        stream.write(cacheSynchronizationType);
        stream.writeBoolean(shouldBeDeleted);
        stream.writeBoolean(isInvalid);
        stream.writeBoolean(isNew);
        stream.writeBoolean(isAggregate);
        stream.writeInt(id);
        if (shouldBeDeleted || (cacheSynchronizationType == ClassDescriptor.DO_NOT_SEND_CHANGES) || (cacheSynchronizationType == ClassDescriptor.INVALIDATE_CHANGED_OBJECTS)) {
            writeIdentityInformation(stream);
        } else {
            writeCompleteChangeSet(stream);
        }
    }

    /**
     * INTERNAL:
     * Helper method to writeObject.  Write only the information necessary to identify this
     * ObjectChangeSet to the stream
     */
    public void writeIdentityInformation(java.io.ObjectOutputStream stream) throws java.io.IOException {
        stream.writeObject(cacheKey);
        stream.writeObject(className);
        stream.writeObject(writeLockValue);
    }

    /**
     * INTERNAL:
     * Helper method to readObject.  Completely write this ObjectChangeSet to the stream
     */
    public void writeCompleteChangeSet(java.io.ObjectOutputStream stream) throws java.io.IOException {
        writeIdentityInformation(stream);
        stream.writeObject(changes);
        stream.writeObject(oldKey);
        stream.writeObject(newKey);
    }

    /**
     * INTERNAL:
     */
    public void setPrimaryKeys(Vector key) {
        if (key == null) {
            return;
        }
        if (getCacheKey() == null) {
            setCacheKey(new CacheKey(key));
        } else {
            getCacheKey().setKey(key);
        }
    }

    /**
     * This set contains the list of attributes that must be calculated at commit time.
     */
    public HashSet getDeferredSet() {
        if (deferredSet == null){
            this.deferredSet = new HashSet();
        }
        return deferredSet;
    }

    /**
     * Check to see if there are any attributes that must be calculated at commit time.
     */
    public boolean hasDeferredAttributes() {
        return ! (deferredSet == null  || this.deferredSet.isEmpty());
    }

    /**
     * INTERNAL:
     * Used by SDK for XML project to map ObjectChangeSet
     */
    public int getId() {
        return id;
    }

    /**
     * INTERNAL:
     * Used by SDK for XML project to map ObjectChangeSet
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * INTERNAL:
     * Used by SDK for XML project to map ObjectChangeSet
     */
    public Vector getSDKChanges() {
        return getChanges();
    }

    /**
     * INTERNAL:
     * Used by SDK for XML project to map ObjectChangeSet
     */
    public void setSDKChanges(Vector changeRecords) {
        if (changeRecords != null) {
            // rebuild owner
            for (Enumeration enumtr = changeRecords.elements(); enumtr.hasMoreElements();) {
                ((ChangeRecord)enumtr.nextElement()).setOwner(this);
            }
            setChanges(changeRecords);
        }
    }

    protected void dirtyUOWChangeSet() {
        // PERF: Set the unit of work change set to dirty avoid unnecessary message sends.
        UnitOfWorkChangeSet unitOfWorkChangeSet = (UnitOfWorkChangeSet)getUOWChangeSet();
        if (unitOfWorkChangeSet != null) {
            unitOfWorkChangeSet.setHasChanges(true);
        }
    }
    
    protected void updateUOWChangeSet() {
        // needed to explicitly mark parent uow as having changes.  This is needed in the
        // case of Optimistic read locking and ForceUpdate.  In these scenarios, the object
        // change set can be modified to contain 'real' changes after the uow change set has
        // computed its 'hasChanges' flag.  If not done, the change set will not be merged.
        if (this.getUOWChangeSet() != null) {
            ((org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet)this.getUOWChangeSet()).setHasChanges(this.hasChanges());
        }
    }

    /**
     * Rebuild primary key to the expected type from user format i.e XML change set has all values as String.
     * Do nothing if the change set was not converted to user format
     */
    public void rebuildPKFromUserFormat(ClassDescriptor descriptor, AbstractSession session) {
        Vector newPks = new Vector();
        Vector pks = getPrimaryKeys();
        Vector keyClassifications = descriptor.getObjectBuilder().getPrimaryKeyClassifications();

        for (int i = 0; i < pks.size(); i++) {
            Object key = session.getPlatform(descriptor.getJavaClass()).getConversionManager().convertObject(pks.elementAt(i), (Class)keyClassifications.elementAt(i));
            newPks.add(i, key);
        }
        if (newPks.size() > 0) {
            this.setPrimaryKeys(newPks);
        }
    }

    /**
     * Rebuild writeLockValue to the expected type from user format i.e XML change set has all values as String.
     */
    protected void rebuildWriteLockValueFromUserFormat(ClassDescriptor descriptor, AbstractSession session) {
        if (descriptor.getOptimisticLockingPolicy() instanceof TimestampLockingPolicy) {
            writeLockValue = session.getPlatform(descriptor.getJavaClass()).getConversionManager().convertObject(writeLockValue, ClassConstants.JavaSqlTimestamp_Class);
        } else if (descriptor.getOptimisticLockingPolicy() instanceof VersionLockingPolicy) {
            writeLockValue = session.getPlatform(descriptor.getJavaClass()).getConversionManager().convertObject(writeLockValue, ClassConstants.BIGDECIMAL);
        }
    }

    /**
     * INTERNAL:
     * Remove the change.
     * Used by the event mechanism to reset changes after client has updated the object within an event.
     */
    public void removeChange(String attributeName){
        Object record = getChangesForAttributeNamed(attributeName);
        if (record != null) {
            getChanges().remove(record);
            this.attributesToChanges.remove(attributeName);
        }
    }

    /**
     * Remove object represent this change set from identity map.  If change set is in XML format, rebuild pk to the correct class type from String
     */
    protected void removeFromIdentityMap(AbstractSession session) {
        // Rebuild PK to the expected type if change set was converted to user format such as XML
        if ((session.getCommandManager() != null) && (session.getCommandManager().getCommandConverter() != null)) {
            rebuildPKFromUserFormat(session.getDescriptor(getClassType(session)), session);
        }
        session.getIdentityMapAccessor().removeFromIdentityMap(getPrimaryKeys(), getClassType(session));
    }

    /**
     * INTERNAL:
     * Indicates whether the object in session cache should be invalidated.
     * @param original Object is from session's cache into which the changes are about to be merged, non null.
     * @param session AbstractSession into which the changes are about to be merged;
     */
    public boolean shouldInvalidateObject(Object original, AbstractSession session) {
        // Either no optimistic locking or no version change.
        if (optimisticLockingPolicy == null) {
            return false;
        }
        if (session.isRemoteSession()){
            //remote unit of work not supported as version values in UOW will be updated 
            //when the committed UOW is received on the client.  That updated value will be
            //set in the UOW cache when the changeset is calculated giving the changeset the
            //incorrect initialWriteLockValue value
            //version number comparison will still be completed later.
            return false;
        }
        
        if(isInvalid()) {
            return true;
        }
        
        Object originalWriteLockValue = optimisticLockingPolicy.getWriteLockValue(original, getPrimaryKeys(), session);
        // initialWriteLockValue and originalWriteLockValue are not equal.
        // Example: 
        // original registered in uow with version 1 (originalWriteLockValue);
        // uow.beginEarlyTransaction();
        // custom update run through the uow changes the version on the object in the db to 2;
        // the clone is refreshed - now it has version 2;
        // on uow.commit or uow.writeChanges changeSet is created with initialWriteLockValue = 2;
        // The original in the cache should be invalidated - the custom update would not be reflected after merge.
        if (this.initialWriteLockValue == null){
            if (this.hasChanges()){
                return true; // no initial version was available but we will be merging changes with unknown version force invalidation
            }else{
                return false;  // don't invalidate as we are not merging anything anyway
            }
        }
        if (optimisticLockingPolicy.compareWriteLockValues(initialWriteLockValue, originalWriteLockValue) != 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * INTERNAL:
     * PERF: Return the session cache-key, cached during thr merge.
     */
    public CacheKey getActiveCacheKey()  {
        return activeCacheKey;
    }
    
    /**
     * INTERNAL:
     * PERF: Set the session cache-key, cached during thr merge.
     */
    public void setActiveCacheKey(CacheKey activeCacheKey)  {
        this.activeCacheKey = activeCacheKey;
    }
}