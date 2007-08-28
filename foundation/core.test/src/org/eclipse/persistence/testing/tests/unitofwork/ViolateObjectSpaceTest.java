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

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class ViolateObjectSpaceTest extends TransactionalTestCase {
    protected Object objectToBeWritten;

    public ViolateObjectSpaceTest() {
        setDescription("Test using no validation.");
    }

    public void test() {
        testPartial();
        testFull();
        testNone();
    }

    public void testFull() {
        Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.performFullValidation();
        Employee newEmployee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        newEmployee.setId(employeeFromCache.getId());
        ValidationException exception = null;
        try {
            uow.registerObject(newEmployee);
        } catch (ValidationException caught) {
            exception = caught;
        }
        if ((exception == null) || (exception.getErrorCode() != ValidationException.WRONG_OBJECT_REGISTERED)) {
            throw new TestErrorException("incorrect exception thrown.");
        }
    }

    public void testNone() {
        Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newObject = (Employee)uow.newInstance(Employee.class);
        this.objectToBeWritten = newObject;
        uow.dontPerformValidation();
        newObject.setManager(employeeFromCache);
        uow.commit();
    }

    public void testPartial() {
        Employee employeeFromCache = (Employee)getSession().readObject(Employee.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee newObject = (Employee)uow.newInstance(Employee.class);
        uow.performPartialValidation();
        newObject.setManager(employeeFromCache);
        QueryException exception = null;
        try {
            uow.commit();
        } catch (QueryException caught) {
            exception = caught;
        }
        if ((exception == null) || 
            (exception.getErrorCode() != QueryException.BACKUP_CLONE_IS_ORIGINAL_FROM_PARENT)) {
            throw new TestErrorException("incorrect exception thrown.");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Object objectFromDatabase = getSession().readObject(objectToBeWritten);

        if (!(compareObjects(this.objectToBeWritten, objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '" + objectFromDatabase + 
                                         "' does not match the original, '" + this.objectToBeWritten + ".");
        }
    }
}