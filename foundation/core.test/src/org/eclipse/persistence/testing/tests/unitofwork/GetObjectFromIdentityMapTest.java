/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Bug 3287196
 * Ensure calling getFromIdentityMap(Object) on a UnitOfWork will go to the parent
 * session if the object is not found in the UnitOfWorkCache
 */
public class GetObjectFromIdentityMapTest extends TestCase {
    protected Expression expression = null;
    protected Employee employee = null;

    public GetObjectFromIdentityMapTest() {
        setDescription("Test UnitOfWorkIdentityMapAccessor to ensure it goes to the parent session to get objects not in the UOW cache..");
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        ExpressionBuilder employees = new ExpressionBuilder();
        expression = employees.get("firstName").equal("Sarah");
        expression = expression.and(employees.get("lastName").equal("Way"));
        employee = (Employee)getSession().readObject(Employee.class, expression);
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        employee = (Employee)uow.getIdentityMapAccessor().getFromIdentityMap(employee);
    }

    public void verify() {
        if (employee == null) {
            throw new TestErrorException("UnitOfWork did not look in its parents cache for an object when calling getFromIdentityMap(Object)");
        }
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}