/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.OraclePlatform;
import org.eclipse.persistence.testing.framework.*;

public class XDBTestModel extends TestModel {
    DatabasePlatform oldPlatform = null;

    public XDBTestModel() {
        setDescription("Tests Oracle XDB Specific features");
    }

    public void addTests() {
        addTest(getXDBTestSuite());
    }

    public void reset() {
        this.getSession().getLogin().setPlatform(oldPlatform);
        getDatabaseSession().logout();
        getDatabaseSession().login();
    }

    public static TestSuite getXDBTestSuite() {
        TestSuite testSuite = new TestSuite();
        testSuite.setName("XML Database Test Suite");

        testSuite.addTest(new ExistsNodeWithJoinTest());
        testSuite.addTest(new ExtractValueTest());
        testSuite.addTest(new GetNumberValTest());
        testSuite.addTest(new UpdateDocumentTest());
        testSuite.addTest(new ReportQueryTest());
        testSuite.addTest(new SessionBrokerTestCase());
        testSuite.addTest(new RemoteSessionTest());
        testSuite.addTest(new InsertWithNullTest());
        return testSuite;
    }

    public void addForcedRequiredSystems() {
        addForcedRequiredSystem(new XMLTypeEmployeeSystem());
        oldPlatform = getSession().getPlatform();
        if ((!(oldPlatform instanceof OraclePlatform)) || (getSession().getLogin().getConnectionString().indexOf("oci") == -1)) {
            throw new TestProblemException("This model is intended for Oracle databases through OCI");
        }
        this.getSession().getLogin().setPlatform(new org.eclipse.persistence.platform.database.oracle.Oracle9Platform());
        getDatabaseSession().logout();
        getDatabaseSession().login();
        try {
            super.addForcedRequiredSystems();
        } catch (Exception ex) {
            String message = ex.getMessage();
            if ((message != null) && (message.indexOf("invalid datatype") > 0)) {
                throw new TestProblemException("This model will only work on Oracle 9i and up");
            } else {
                throw new TestProblemException("Error occured", ex);
            }
        }
    }
}