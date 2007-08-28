/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model;

import junit.framework.*;
import org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectTestSuite;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.testing.sdo.helper.typehelper.SDOTypeHelperTestSuite;
import org.eclipse.persistence.testing.sdo.model.type.SDOTypeTestSuite;

/**
 *  The general location where we perform all corresponding testing based on
 *  different SDO classes such as Type, TypeHelper, and SDOType etc
 */
public class SDOModelTestSuite extends TestCase {
    public SDOModelTestSuite() {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     *  Inherited suite mthod for generating all test cases.
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All Helper Tests");
        suite.addTest(new SDODataObjectTestSuite().suite());
        suite.addTest(new SDOTypeTestSuite().suite());
        return suite;
    }
}