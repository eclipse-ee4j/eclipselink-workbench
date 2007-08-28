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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

//Test id comparison without 1:1, that must force a join
public class SimpleEqualsTestWithManualJoin extends JPQLTestCase {
    public void setup() {
        setReferenceClass(Employee.class);

        //add a mapping for addressId wo we can use it in from "emp"
        //        DirectToFieldMapping addressIdMapping = new DirectToFieldMapping();
        //        addressIdMapping.setAttributeName("addressId");
        //        addressIdMapping.setFieldName("EMPLOYEE.ADDR_ID");
        //        addressIdMapping.setGetMethodName("getAddressId");
        //        addressIdMapping.setSetMethodName("setAddressId");
        //        addressIdMapping.setIsReadOnly(true);
        //        getSession().getDescriptor(Employee.class).addMapping(addressIdMapping);
        //        addressIdMapping.initialize(getSession());
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        ExpressionBuilder addressBuilder = new ExpressionBuilder(Address.class);
        Expression whereClause = builder.get("addressId").equal(addressBuilder.get("id"));
        Vector employees = (Vector)getSession().readAllObjects(Employee.class, whereClause);

        setOriginalOject(employees);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        String ejbqlString;
        ejbqlString = "SELECT OBJECT(emp) FROM Employee emp, Address address " + "WHERE emp.addressId = address.id";

        setEjbqlString(ejbqlString);
        super.setup();
    }

    public void reset() {
        //        getSession().getDescriptor(Employee.class).removeMappingForAttributeName("addressId");
    }
}