/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.distributedcache;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.sessions.server.Server;

public abstract class DistributedCacheMergeTest extends TestCase {
    private OptimisticLockingPolicy policy1 = null;
    private OptimisticLockingPolicy policy2 = null;
    protected Server cluster1Session = null;
    protected Server cluster2Session = null;
    protected Session originalSession = null;
    public static ConcurrencyManager semaphore = new ConcurrencyManager();
    Object originalObject = null;

    public DistributedCacheMergeTest() {
        setDescription("Testing");
    }

    protected void setup() throws Exception {
        originalSession = (org.eclipse.persistence.sessions.Session)getExecutor().getSession();
        createObject();

        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (Exception e) {
            //hopefully this exception is just because the registry already exists            
        }

        cluster1Session = (Server)buildSession("cluster1");
        cluster1Session.login();

        cluster2Session = (Server)buildSession("cluster2");
        cluster2Session.login();
        Thread.sleep(5000);// Let the Cache-sync get configured.  

        policy1 = disableOptimisticLocking(cluster1Session);
        policy2 = disableOptimisticLocking(cluster2Session);
    }

    protected void createObject() {
        originalObject = getOriginalObject();
        UnitOfWork uow = originalSession.acquireUnitOfWork();
        Object newObj = uow.registerNewObject(originalObject);
        uow.commit();
    }

    protected Server buildSession(String sessionName) throws Exception {
        Server session = null;
        Project p = getNewProject();

        DatabaseLogin theLogin = originalSession.getLogin();
	p.setLogin(originalSession.getLogin());
        session = p.createServerSession();
        session.setSessionLog(getSession().getSessionLog());

        org.eclipse.persistence.sessions.remote.CacheSynchronizationManager manager = new org.eclipse.persistence.sessions.remote.CacheSynchronizationManager();
        manager.setClusteringServiceClassTypeName("org.eclipse.persistence.sessions.remote.rmi.RMIClusteringService");
        org.eclipse.persistence.sessions.remote.rmi.RMIClusteringService clusteringService = new org.eclipse.persistence.sessions.remote.rmi.RMIClusteringService(session);
        clusteringService.setLocalHostURL("localhost:1099");
        manager.setClusteringService(clusteringService);
        session.setCacheSynchronizationManager(manager);

        ((Server)session).getReadConnectionPool().setMinNumberOfConnections(1);
        ((Server)session).getDefaultConnectionPool().setMinNumberOfConnections(1);

        return session;
    }

    public void reset() throws Exception {
        int depth = semaphore.getDepth();

        while (depth != 0) {
            semaphore.release();
            depth = semaphore.getDepth();
        }
        UnitOfWork uow = cluster1Session.acquireClientSession().acquireUnitOfWork();
        uow.deleteObject(originalObject);
        uow.commit();

        enableOptimisticLocking(cluster1Session, policy1);
        if (cluster1Session != null) {
            cluster1Session = null;
        }

        if (cluster2Session != null) {
            cluster2Session = null;
        }
    }

    private OptimisticLockingPolicy disableOptimisticLocking(Server server) {
        ClassDescriptor descriptor = server.getDescriptor(getRootClass());
        OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();
        descriptor.setOptimisticLockingPolicy(null);
        return policy;
    }

    private void enableOptimisticLocking(Server server, OptimisticLockingPolicy policy) {
        ClassDescriptor descriptor = server.getDescriptor(getRootClass());
        descriptor.setOptimisticLockingPolicy(policy);
    }

    protected void test() {
        Session clientSession1 = cluster1Session.acquireClientSession();
        Session clientSession2 = cluster2Session.acquireClientSession();

        cluster2Session.getIdentityMapAccessor().initializeAllIdentityMaps();
        cluster2Session.getEventManager().addListener(buildCacheMergeBlockingListener());

        Object object1 = findOriginalObject(clientSession1);

        if (object1 == null) {
            throw new TestErrorException("Employee on Server 1 exists");
        }
        int initialNumProjs = getCollectionSize(object1);

        UnitOfWork uow = clientSession1.acquireUnitOfWork();
        Object newEmpWC = uow.registerObject(object1);
        modifyCollection(uow, newEmpWC);

        semaphore.acquire();

        uow.commit();

        if (getCollectionSize(object1) != (initialNumProjs + 1)) {
            throw new TestErrorException("Employee has the wrong number of items in the collection" + " expected:" + (initialNumProjs + 1) + " was:" + getCollectionSize(object1));
        }

        try {
            while (semaphore.getNumberOfWritersWaiting() == 0) {
                Thread.sleep(10);
            }
        } catch (Exception e) {
            throw new TestErrorException("Error while getting Thread to sleep", e);
        }

        // Make sure its not in server 2's cache
        Object object2 = clientSession2.getIdentityMapAccessor().getFromIdentityMap(object1);

        if (object2 != null) {
            throw new TestErrorException("Employee already exists in Server 2's cache");
        }

        object2 = findOriginalObject(clientSession2);

        if (object2 == null) {
            throw new TestErrorException("Employee does not exist in Server 2's cache");
        }

        if (getCollectionSize(object2) != (initialNumProjs + 1)) {
            throw new TestErrorException("Employee has the wrong number of items in the collection");
        }

        semaphore.release();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new TestErrorException("Error while getting Thread to sleep", e);
        }

        semaphore.acquire();

        if (getCollectionSize(object2) != (initialNumProjs + 1)) {
            if (getCollectionSize(object2) == (initialNumProjs + 2)) {
                throw new TestErrorException("Employee has one too many items in the collection after merge");
            }
            throw new TestErrorException("Employee has wrong number of items in the collection after merge");
        }
        semaphore.release();

    }

    protected Class getRootClass() {
        return Employee.class;
    }

    protected Project getNewProject() {
        return new EmployeeProject();
    }

    protected abstract void modifyCollection(UnitOfWork uow, Object objectToModify);

    protected abstract int getCollectionSize(Object rootObject);

    protected abstract Object buildOriginalObject();

    public Object findOriginalObject(Session session) {
        ReadObjectQuery roq = new ReadObjectQuery(originalObject);
        return session.executeQuery(roq);
    }

    private SessionEventListener buildCacheMergeBlockingListener() {
        return new SessionEventAdapter() {
                public void preDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
                    try {
                        DistributedCacheMergeTest.semaphore.acquire();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                public void postDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
                    DistributedCacheMergeTest.semaphore.release();
                }
            };
    }

    public Object getOriginalObject() {
        if (originalObject == null) {
            originalObject = buildOriginalObject();
        }
        return originalObject;
    }
}