/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;

public class LoadAndSaveOpenContentTestCases extends LoadAndSaveTestCases {
    public LoadAndSaveOpenContentTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.LoadAndSaveOpenContentTestCases" };
        TestRunner.main(arguments);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/CustomerOpenContent.xsd";
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElementOpenContent.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElementOpenContentNoSchema.xml");
    }

    protected String getControlRootURI() {
        return "http://www.example.org";
    }

    protected String getControlRootName() {
        return "customer";
    }
    
     protected String getRootInterfaceName()   {
       return "CustomerType";
     }

    public void registerTypes() {        
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type addressType = registerAddressType();
        Type phoneType = registerPhoneType();
        Type orderType = registerOrderType();

        // create a new Type for Customers
        DataObject customerType = dataFactory.create("commonj.sdo", "Type");
        customerType.set("uri", getControlRootURI());
        customerType.set("name", "customer-type");
        customerType.set("open", true);

        // create a first name property
        addProperty(customerType, "firstName", stringType, false, false, true);
        addProperty(customerType, "lastName", stringType, false, false, true);
        addProperty(customerType, "address", addressType, true, false, true);
        addProperty(customerType, "customerID", SDOConstants.SDO_INT);
        addProperty(customerType, "sin", stringType);

        // now define the Customer type so that customers can be made
        Type customerSDOType = typeHelper.define(customerType);

        DataObject agePropDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        agePropDO.set("name", "age");
        agePropDO.set("type", stringType);
        agePropDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        typeHelper.defineOpenContentProperty(getControlRootURI(), agePropDO);

        DataObject pricePropDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        pricePropDO.set("name", "price");
        pricePropDO.set("type", SDOConstants.SDO_FLOAT);
        pricePropDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        typeHelper.defineOpenContentProperty(getControlRootURI(), pricePropDO);

        DataObject extraNamePropDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        extraNamePropDO.set("name", "extraName");
        extraNamePropDO.set("type", stringType);
        extraNamePropDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        typeHelper.defineOpenContentProperty(getControlRootURI(), extraNamePropDO);

        DataObject bdPropDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        bdPropDO.set("name", "birthDate");
        bdPropDO.set("type", SDOConstants.SDO_DATE);
        bdPropDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        typeHelper.defineOpenContentProperty(getControlRootURI(), bdPropDO);

        DataObject orderPropDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        orderPropDO.set("name", "order");
        orderPropDO.set("type", orderType);
        orderPropDO.set("containment", true);
        orderPropDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        typeHelper.defineOpenContentProperty(getControlRootURI(), orderPropDO);

        DataObject customerPropDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        customerPropDO.set("name", "customer");
        customerPropDO.set("type", customerSDOType);
        customerPropDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        typeHelper.defineOpenContentProperty(getControlRootURI(), customerPropDO);

        DataObject phonePropDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        phonePropDO.set("name", "phone");
        phonePropDO.set("type", phoneType);
        phonePropDO.set("containment", true);
        phonePropDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        typeHelper.defineOpenContentProperty(getControlRootURI(), phonePropDO);

        DataObject blahPhonePropDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        blahPhonePropDO.set("name", "blahphone");
        blahPhonePropDO.set("type", phoneType);
        blahPhonePropDO.set("many", true);
        blahPhonePropDO.set("containment", true);
        blahPhonePropDO.set(SDOConstants.XMLELEMENT_PROPERTY, true);
        typeHelper.defineOpenContentProperty(getControlRootURI(), blahPhonePropDO);

    }

    private Type registerPhoneType() {
        // create a new Type for Phone
        DataObject phoneTypeDO = dataFactory.create("commonj.sdo", "Type");
        phoneTypeDO.set("uri", getControlRootURI());
        phoneTypeDO.set("name", "phoneType");

        addProperty(phoneTypeDO, "areaCode", SDOConstants.SDO_STRING, false, false, true);
        addProperty(phoneTypeDO, "number", SDOConstants.SDO_STRING, false, false, true);

        return typeHelper.define(phoneTypeDO);
    }

    private Type registerOrderType() {
        // create a new Type for Phone
        DataObject phoneTypeDO = dataFactory.create("commonj.sdo", "Type");
        phoneTypeDO.set("uri", getControlRootURI());
        phoneTypeDO.set("name", "orderType");

        addProperty(phoneTypeDO, "orderName", SDOConstants.SDO_STRING, false, false, true);
        addProperty(phoneTypeDO, "orderQuantity", SDOConstants.SDO_INT, false, false, true);

        return typeHelper.define(phoneTypeDO);
    }

    private Type registerAddressType() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        DataObject addressType = dataFactory.create("commonj.sdo", "Type");
        SDOProperty prop = (SDOProperty)addressType.getType().getProperty("uri");
        addressType.set(prop, getControlRootURI());
        prop = (SDOProperty)addressType.getType().getProperty("name");
        addressType.set(prop, "address-type");
        addProperty(addressType, "zipcode", stringType, false, false, true);
        addProperty(addressType, "permanent", SDOConstants.SDO_BOOLEAN, false, false, true);
        addProperty(addressType, "street", stringType);
        addProperty(addressType, "city", stringType);
        return typeHelper.define(addressType);
    }
}