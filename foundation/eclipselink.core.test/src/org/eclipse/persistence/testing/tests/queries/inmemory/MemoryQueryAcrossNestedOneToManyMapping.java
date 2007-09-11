/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.Vector;
import java.util.Enumeration;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

public class MemoryQueryAcrossNestedOneToManyMapping extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected Employee employee;
    protected ReadAllQuery queryAll;
    protected ReadObjectQuery queryObject;
    protected java.util.Vector allEmployees;
    protected java.util.Vector employees;
    protected java.util.Vector phones;
    protected java.util.Vector verifyEmp;
    protected java.util.Vector verifyPhone;

    public MemoryQueryAcrossNestedOneToManyMapping() {
        super();
    }

    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * This is  still in the works.
     */
    public void setup() {
        allEmployees = new Vector();
        employees = new Vector();
        phones = new Vector();
        Vector anotherVector = new Vector();
        Vector phone = new Vector();
        Vector managedEmp = new Vector();
        Vector managedEmpVector = new Vector();
        Vector allPhones = new Vector();
        Vector allEmps = new Vector();
        queryAll = new ReadAllQuery();
        queryAll.setReferenceClass(Employee.class);
        queryAll.setSelectionCriteria(new ExpressionBuilder().anyOf("managedEmployees").anyOf("phoneNumbers").get("areaCode").equal("905"));
        getSession().removeQuery("getAllEmployees");
        getSession().addQuery("getAllEmployees", queryAll);
        allEmployees = (Vector)getSession().executeQuery("getAllEmployees");
        for (Enumeration enumtr = (Enumeration)allEmployees.elements(); enumtr.hasMoreElements();) {
            employees = (Vector)((Employee)enumtr.nextElement()).getManagedEmployees();
            allEmps.addAll(employees);
            //trigger all the value holders of employees who have managedEmployees
        }
        verifyEmp = allEmps;

        for (Enumeration enum2 = (Enumeration)allEmployees.elements(); enum2.hasMoreElements();) {
            anotherVector = (Vector)((Employee)enum2.nextElement()).getPhoneNumbers();
            phones.addAll(anotherVector);
            //trigger all the value holders of managers' phonenumbers
        }

        for (Enumeration enum3 = (Enumeration)allEmps.elements(); enum3.hasMoreElements();) {
            managedEmpVector = (Vector)((Employee)enum3.nextElement()).getManagedEmployees();
            managedEmp.addAll(managedEmpVector);

            //trigger all the value holders of managed employees' managed employees
        }

        for (Enumeration enum4 = (Enumeration)allEmps.elements(); enum4.hasMoreElements();) {
            phone = (Vector)((Employee)enum4.nextElement()).getPhoneNumbers();
            allPhones.addAll(phone);

            //trigger all the value holders of managed employees' phonenumbers
        }
        verifyPhone = allPhones;

    }

    public void test() {
        //all the employees with area code 905 phonenumbers should be
        //in the cache right now.
        queryObject = new ReadObjectQuery();
        queryObject.setReferenceClass(Employee.class);
        queryObject.checkCacheOnly();//read from cache only

        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.anyOf("managedEmployees").anyOf("phoneNumbers").get("areaCode").equal("905");
        queryObject.setSelectionCriteria(exp);
        employee = (Employee)getSession().executeQuery(queryObject);

    }

}