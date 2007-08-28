/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xsdhelper.define;

import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.helper.ClassConstants;

public class DefineWithIncludesTestCases extends XSDHelperDefineTestCases {
    public DefineWithIncludesTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(DefineWithIncludesTestCases.class);
    }

    public String getSchemaToDefine() {
        return "org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/IncludesWithNamespaces.xsd";
    }

    protected String getSchemaLocation() {
        return "file:./org/eclipse/persistence/testing/sdo/helper/xsdhelper/generate/";
    }

    public List getControlTypes() {
        List types = new ArrayList();
        String uri = "my.uri";

        String javaPackage = "defaultPackage";

        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type intType = typeHelper.getType("commonj.sdo", "Int");

        /****ADDRESS TYPE*****/

        //ADDRESS TYPE
        SDOType USaddrType = new SDOType(uri, "USAddress");
        USaddrType.setDataType(false);
        USaddrType.setInstanceClassName(javaPackage + "." + "USAddress");

        SDOProperty streetProp = new SDOProperty(aHelperContext);
        streetProp.setName("street");
        streetProp.setType(stringType);
        //streetProp.setAttribute(true);
        streetProp.setXsd(true);
        streetProp.setXsdLocalName("street");
        streetProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(streetProp);

        SDOProperty cityProp = new SDOProperty(aHelperContext);
        cityProp.setName("city");
        cityProp.setType(stringType);
        //cityProp.setAttribute(true);
        cityProp.setXsd(true);
        cityProp.setXsdLocalName("city");
        cityProp.setContainingType(USaddrType);
        USaddrType.getDeclaredProperties().add(cityProp);

        /****QUANTITY TYPE*****/
        SDOType quantityType = new SDOType(uri, "quantityType");
        quantityType.setDataType(true);
        quantityType.getBaseTypes().add(intType);
        //quantityType.setInstanceClassName("java.lang.Integer");
        quantityType.setInstanceClassName(ClassConstants.PINT.getName());

        /****SKU TYPE*****/
        SDOType SKUType = new SDOType(uri, "SKU");
        SKUType.setDataType(true);
        SKUType.getBaseTypes().add(stringType);
        SKUType.setInstanceClassName("java.lang.String");

        /****PURCHASEORDER TYPE*****/
        SDOType POtype = new SDOType(uri, "PurchaseOrder");
        POtype.setInstanceClassName(javaPackage + "." + "PurchaseOrder");
        POtype.setDataType(false);

        SDOProperty shipToProp = new SDOProperty(aHelperContext);
        shipToProp.setName("shipTo");
        shipToProp.setContainment(true);
        //shipToProp.setElement(true);
        shipToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        shipToProp.setType(USaddrType);
        shipToProp.setXsd(true);
        shipToProp.setXsdLocalName("shipTo");
        shipToProp.setContainingType(POtype);

        SDOProperty billToProp = new SDOProperty(aHelperContext);
        billToProp.setName("billTo");
        billToProp.setContainment(true);
        //billToProp.setElement(true);
        billToProp.setInstanceProperty(SDOConstants.XMLELEMENT_PROPERTY, Boolean.TRUE);
        billToProp.setType(USaddrType);
        billToProp.setXsd(true);
        billToProp.setXsdLocalName("billTo");
        billToProp.setContainingType(POtype);

        SDOProperty quantityProp = new SDOProperty(aHelperContext);
        quantityProp.setName("quantity");
        //quantityProp.setAttribute(true);
        quantityProp.setType(quantityType);
        quantityProp.setXsd(true);
        quantityProp.setXsdLocalName("quantity");
        quantityProp.setContainingType(POtype);

        SDOProperty partNumProp = new SDOProperty(aHelperContext);
        partNumProp.setName("partNum");
        //partNumProp.setAttribute(true);
        partNumProp.setType(SKUType);
        partNumProp.setXsd(true);
        partNumProp.setXsdLocalName("partNum");
        partNumProp.setContainingType(POtype);

        POtype.getDeclaredProperties().add(shipToProp);
        POtype.getDeclaredProperties().add(billToProp);
        POtype.getDeclaredProperties().add(quantityProp);
        POtype.getDeclaredProperties().add(partNumProp);

        types.add(POtype);
        types.add(quantityType);
        types.add(USaddrType);
        types.add(SKUType);

        return types;

    }
}