/* Copyright (c) 2004, 2006, Oracle. All rights reserved.  */
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Tests fine-grained / descriptor level pessimistic locking.
 * <p>
 * Tests that executing a pessimistically locking query outside a UnitOfWork
 * does not issue any locks, and can get cache hits.
 * @author  smcritch
 */
public class PessimisticLockOutsideUnitOfWorkTest extends PessimisticLockFineGrainedTest {

    public PessimisticLockOutsideUnitOfWorkTest(short lockMode) {
        super(lockMode);
        setDescription("This test verifies the pessimistic locking feature works " + 
                       "properly when set on the descriptor.  And especially only for queries " + 
                       " executed inside a UnitOfWork, not outside.  Outside the query should " + 
                       " be a regular NO_LOCK query.");
    }

    public void test() throws Exception {
        if (getSession().getPlatform().isDB2() || getSession().getPlatform().isAccess() || 
            getSession().getPlatform().isSybase() /*|| getSession().getPlatform().isSQLServer()*/) {
            throw new TestWarningException("This database does not support for update");
        }

        if ((getSession().getPlatform().isMySQL() || getSession().getPlatform().isTimesTen()) && 
            lockMode == org.eclipse.persistence.queries.ObjectLevelReadQuery.LOCK_NOWAIT) {
            throw new TestWarningException("This database does not support NOWAIT");
        }

        // If this did not work, would have had thrown a fetch out of sequence exception.
        ReadObjectQuery query = new ReadObjectQuery(Address.class);

        Address address = (Address)getSession().executeQuery(query);

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        query.setSelectionObject(address);

        address = (Address)getSession().executeQuery(query);

        // Now make sure that on the UnitOfWork the query is prepared for the
        // first time as it is a lock query.
        // The following will make sure the query was a lock query.
        uow = getSession().acquireUnitOfWork();
        address = (Address)uow.executeQuery(query);

        // Test the lock.
        DatabaseSession session2 = null;
        UnitOfWork uow2 = null;
        try {
            if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
                session2 = 
                        org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getProject().createDatabaseSession();
            } else {
                session2 = getSession().getProject().createDatabaseSession();
            }
            session2.setLog(getSession().getLog());
            session2.setLogLevel(getSession().getLogLevel());
            session2.login();
            uow2 = session2.acquireUnitOfWork();
            boolean isLocked = false;

            try {
                Address lockedAddress = (Address)uow2.executeQuery(query);
            } catch (EclipseLinkException exeception) {
                session2.logMessage(exeception.toString());
                isLocked = true;
            }
            if (!isLocked) {
                throw new TestErrorException("Select for update does not acquire a lock");
            }

            // if this attempts a lock should get an exception right away.
            session2.executeQuery(query);

        } catch (RuntimeException e) {
            throw e;
        } finally {
            if (uow2 != null) {
                uow2.release();
            }
            if (session2 != null) {
                session2.logout();
            }
        }

        // Now test that we get a cache hit on the session
        query.checkCacheOnly();

        address = (Address)getSession().executeQuery(query);

        strongAssert(address != null, "Did not get a cache hit when executing lock query outside a UOW");
    }
}