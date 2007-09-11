/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDODataObjectJIRA81TestCases extends SDOTestCase {
    private DataObject empDataObject;

    public SDODataObjectJIRA81TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectJIRA81TestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        Type intType = SDOConstants.SDO_INT;
        Type stringType = SDOConstants.SDO_STRING;

        DataObject empType = defineType("my.uri", "employee");
        addProperty(empType, "name", stringType);
        DataObject testProp = addProperty(empType, "myList", intType);
        testProp.set("many", true);

        Type empSDOType = typeHelper.define(empType);
        empDataObject = dataFactory.create(empSDOType);
        empDataObject.set("name", "Bob Smith");
    }

    public void testSetAtEndOfListBySquareBracket() {
        List value = empDataObject.getList("myList");
        assertNotNull(value);

        value.add(1);
        value.add(2);
        empDataObject.set("myList[2]", 3);
        assertEquals(1, empDataObject.get("myList[1]"));
        assertEquals(3, empDataObject.get("myList[2]"));
        try {
            empDataObject.get("myList[3]");
            //returns null should throw exception per 81          
            fail("Expected exception");
        } catch (IndexOutOfBoundsException e) {
            //pass
        }
        try {
            empDataObject.getList("myList").get(3);
        } catch (IndexOutOfBoundsException e) {
            //pass
        }
    }

    public void testSetAtEndOfList() {
        List value = empDataObject.getList("myList");
        assertNotNull(value);

        value.add(1);
        value.add(2);
        empDataObject.set("myList[2]", 3);
        assertEquals(1, empDataObject.get("myList[1]"));
        assertEquals(3, empDataObject.get("myList[2]"));

        try {
            empDataObject.getList("myList").get(3);
            fail("Expected exception");
        } catch (IndexOutOfBoundsException e) {
            //pass
        }
    }
}