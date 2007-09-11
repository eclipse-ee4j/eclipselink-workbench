/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class ComplexLengthTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        if ((getSession().getLogin().getPlatform().isSQLServer())) {
            throw new TestWarningException("This test is not supported on SQL Server. Because 'LENGTH' is not a recognized function name on SQL Server.");
        }

        Vector employees = getSomeEmployees();
        Employee emp = (Employee)employees.firstElement();

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "(LENGTH(emp.firstName) = ";
        ejbqlString = ejbqlString + emp.getFirstName().length() + ")";
        ejbqlString = ejbqlString + " AND ";
        ejbqlString = ejbqlString + "(LENGTH(emp.lastName) = ";
        ejbqlString = ejbqlString + emp.getLastName().length() + ")";

        setEjbqlString(ejbqlString);
        setOriginalOject(emp);

        super.setup();
    }
}