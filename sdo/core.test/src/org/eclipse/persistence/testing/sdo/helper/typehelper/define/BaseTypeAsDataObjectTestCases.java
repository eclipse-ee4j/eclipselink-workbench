/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class BaseTypeAsDataObjectTestCases extends SDOTestCase {
    public BaseTypeAsDataObjectTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.BaseTypeAsDataObjectTestCases" };
        TestRunner.main(arguments);
    }

    public void testDefine() throws Exception {
        DataObject personTypeDO = dataFactory.create(SDOConstants.SDO_TYPE);
        personTypeDO.set("uri", "theUri");
        personTypeDO.set("name", "person");
        DataObject parentTypeDO = dataFactory.create(SDOConstants.SDO_TYPE);
        parentTypeDO.set("uri", "theUri");
        parentTypeDO.set("name", "parent");
        List baseTypes = new ArrayList();
        baseTypes.add(personTypeDO);
        parentTypeDO.set("baseType", baseTypes);
        addProperty(personTypeDO, "name", SDOConstants.SDO_STRING, false, false, true);
        addProperty(personTypeDO, "parent", parentTypeDO, true, false, true);
        Type personType = typeHelper.define(personTypeDO);
        Type parentType = typeHelper.define(parentTypeDO);

        assertNotNull(parentType.getBaseTypes());
        assertEquals(1, parentType.getBaseTypes().size());
        Type theBaseType = (Type)parentType.getBaseTypes().get(0);
        assertTrue(personType == theBaseType);
    }
}