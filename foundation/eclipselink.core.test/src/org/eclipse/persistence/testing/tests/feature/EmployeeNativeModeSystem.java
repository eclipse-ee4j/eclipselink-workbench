/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

/**
 * <b>Purpose</b>: To define system behavoir.
 * <p><b>Responsibilities</b>:    <ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class EmployeeNativeModeSystem extends EmployeeSystem {

    /**
     * Also creates the procs.
     */
    public void createTables(DatabaseSession session) {
        super.createTables(session);

        if (session.getLogin().getPlatform().isSybase() || session.getLogin().getPlatform().isSQLServer()) {
            session.executeNonSelectingCall(new SQLCall("ALTER      TABLE EMPLOYEE DROP CONSTRAINT FK_EMPLOYEE_ADDR_ID"));
            session.executeNonSelectingCall(new SQLCall("ALTER      TABLE PROJECT DROP CONSTRAINT FK_PROJECT_LEADER_ID"));// Make sure no stmt hit, JConnect has nullpointer bug.
            session.executeNonSelectingCall(new SQLCall("ALTER      TABLE EMPLOYEE DROP CONSTRAINT FK_EMPLOYEE_MANAGER_ID"));// Make sure no stmt hit, JConnect has nullpointer bug.
        }
    }
}