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
import org.eclipse.persistence.testing.models.employee.domain.*;

public class ComplexReverseAbsTest extends org.eclipse.persistence.testing.tests.jpql.JPQLTestCase {
    public void setup() {
        Vector employees = getSomeEmployees();

        Employee emp1 = (Employee)employees.firstElement();
        Employee emp2 = (Employee)employees.lastElement();

        String ejbqlString;

        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + emp1.getSalary();
        ejbqlString = ejbqlString + " = ABS(emp.salary)";
        ejbqlString = ejbqlString + " OR ";
        ejbqlString = ejbqlString + emp2.getSalary();
        ejbqlString = ejbqlString + " = ABS(emp.salary)";
        setEjbqlString(ejbqlString);
        Vector employeesUsed = new Vector();
        employeesUsed.add(emp1);
        employeesUsed.add(emp2);
        setOriginalOject(employeesUsed);
        super.setup();
    }
}