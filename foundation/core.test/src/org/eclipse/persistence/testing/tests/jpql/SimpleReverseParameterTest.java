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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class SimpleReverseParameterTest extends JPQLParameterTestCase {
    public void setup() {
        // Get the baseline employees for the verify
        Employee emp = (Employee)getSomeEmployees().firstElement();

        String parameterName = "firstName";
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression whereClause = builder.get("firstName").equal(builder.getParameter(parameterName));

        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(Employee.class);
        raq.setSelectionCriteria(whereClause);
        raq.addArgument(parameterName);

        Vector parameters = new Vector();
        parameters.add(emp.getFirstName());

        Vector employees = (Vector)getSession().executeQuery(raq, parameters);

        emp = (Employee)employees.firstElement();

        // Set up the EJBQL using the retrieved employees
        String ejbqlString = "SELECT OBJECT(emp) FROM Employee emp WHERE ";
        ejbqlString = ejbqlString + "?1 = emp.firstName ";

        setEjbqlString(ejbqlString);
        setOriginalOject(employees);

        setArguments(parameters);

        Vector myArgumentNames = new Vector();
        myArgumentNames.add("1");
        setArgumentNames(myArgumentNames);

        // Finish the setup
        super.setup();
    }
}