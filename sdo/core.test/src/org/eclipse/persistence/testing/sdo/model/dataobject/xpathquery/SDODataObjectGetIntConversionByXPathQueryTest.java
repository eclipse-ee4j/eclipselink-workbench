/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.dataobject.xpathquery;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;

public class SDODataObjectGetIntConversionByXPathQueryTest extends SDODataObjectGetByXPathQueryTestCases {
    public SDODataObjectGetIntConversionByXPathQueryTest(String name) {
        super(name);
    }

    public void testGetBooleanConversionWithPathFromDefinedBooleanPropertyEqualSignBracketInPathDotSet() {
        SDOProperty prop = (SDOProperty)dataObject_c0.getType().getProperty("test");
        prop.setType(SDOConstants.SDO_INT);
        Integer bb = new Integer(12);

        //List b = new ArrayList();
        //dataObject_c.set(property_c, b);// c dataobject's a property has value boolean 'true'
        dataObject_a.setInt(propertyTest + "test", bb.intValue());

        this.assertEquals(bb.intValue(), dataObject_a.getInt(propertyTest + "test"));
    }

    // purpose: opencontent properties
    public void testGetIntConversionFromDefinedPropertyWithPath() {
        SDOProperty property_c1_object = ((SDOProperty)dataObject_c1.getInstanceProperty("PName-c1"));
        property_c1_object.setType(SDOConstants.SDO_INT);

        //type_c0.addDeclaredProperty(property_c1_object);
        List objects = new ArrayList();
        Integer b = new Integer(12);
        Integer bb = new Integer(2);
        objects.add(b);
        objects.add(bb);

        dataObject_c1.set(property_c1_object, objects);// add it to instance list

        this.assertEquals(bb.intValue(), dataObject_a.getInt("PName-a0/PName-b0[number='1']/PName-c1.1"));
    }

    //2. purpose: getDataObject with property value is not dataobject
    public void testGetDataObjectConversionFromUndefinedProperty() {
        property_c = new SDOProperty(aHelperContext);
        property_c.setName(PROPERTY_NAME_C);
        property_c.setType(SDOConstants.SDO_DATAOBJECT);
        type_c.addDeclaredProperty(property_c);
        dataObject_c._setType(type_c);

        SDODataObject C = new SDODataObject();

        dataObject_c.set(property_c, C);

        try {
            dataObject_a.getInt(property1);
            fail("ClassCastException should be thrown.");
        } catch (ClassCastException e) {
        }
    }

    //3. purpose: getDataObject with property set to boolean value
    public void testGetDataObjectConversionFromProperty() {
        //try {
        this.assertNull(dataObject_a.getDataObject("PName-a/notExistedTest"));

        //fail("IllegalArgumentException should be thrown.");
        //} catch (IllegalArgumentException e) {
        //}
    }

    //purpose: getDataObject with nul value
    public void testGetDataObjectConversionWithNullArgument() {
        String p = null;
        this.assertNull(dataObject_a.getDataObject(p));
    }

    public void testSetGetDataObjectWithQueryPath() {
        SDOProperty property_c1_object = new SDOProperty(aHelperContext);
        property_c1_object.setName("PName-c1");
        property_c1_object.setContainment(true);
        property_c1_object.setMany(true);
        property_c1_object.setType(SDOConstants.SDO_INT);

        type_c0.addDeclaredProperty(property_c1_object);

        Integer b = new Integer(12);

        dataObject_a.setInt("PName-a0/PName-b0[number='1']/PName-c1.0", b.intValue());

        this.assertEquals(b.intValue(), dataObject_a.getInt("PName-a0/PName-b0[number='1']/PName-c1.0"));
    }
}