package org.eclipse.persistence.testing.tests.clientserver;

import java.util.Vector;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class CommunicationFailureTest extends AutoVerifyTestCase {



    protected Accessor non_txn_read;
    protected org.eclipse.persistence.sessions.server.Server serverSession;
    
    public CommunicationFailureTest() {
        this.setDescription("Tests that TopLink is correctly handling Accessors that experience exceptions.");
    }

    public void setup() {
        DatabaseLogin login = (DatabaseLogin)getSession().getLogin().clone();
        this.serverSession = getSession().getProject().createServerSession();
        this.serverSession.setSessionLog(getSession().getSessionLog());
        this.serverSession.login();
    }

    public void test() {
        UnitOfWork uow = this.serverSession.acquireUnitOfWork();
        QueryExecuteListener listener = new QueryExecuteListener(this);
        getSession().getEventManager().addListener(listener);
        try{
            uow.readObject(Employee.class);
        }catch (Exception ex){
            throw new TestErrorException("outside of the transaction TopLink failed to retry the read.");
        }
        if (((ClientSession)uow.getParent()).getParent().getReadConnectionPool().getConnectionsAvailable().contains(this.non_txn_read)){
            throw new TestErrorException("Failed to remove accessor from pool on exception");


        }
    }

    public void verify() {
    }

    public void reset() {
        this.serverSession.logout();
    }
    
    public class QueryExecuteListener extends SessionEventAdapter{
        protected CommunicationFailureTest test;
        public QueryExecuteListener(CommunicationFailureTest test){
            this.test = test;
        }
        
        /**
         * PUBLIC:
         * This event is raised before the execution of every query against the session.
         * The event contains the query to be executed.
         */
        public void preExecuteQuery(SessionEvent event){
            test.non_txn_read = event.getQuery().getAccessor();
            event.getQuery().getAccessor().closeConnection();
        }

        
        
    }
}