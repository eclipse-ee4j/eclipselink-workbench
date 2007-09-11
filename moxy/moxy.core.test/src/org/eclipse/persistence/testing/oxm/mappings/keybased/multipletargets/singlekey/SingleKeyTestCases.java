/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.attributekey.SingleAttributeKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey.SingleElementKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey.SingleElementKeyWithGroupingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.elementkey.maptests.SingleElementKeyUsingMapTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.nestedattributekey.NestedAttributeKeyTestCases;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.nonstringkeytype.NonStringKeyTypeTestCases;

public class SingleKeyTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Multiple targets with single key test suite");
        suite.addTestSuite(SingleAttributeKeyTestCases.class);
        suite.addTestSuite(SingleElementKeyTestCases.class);
        suite.addTestSuite(SingleElementKeyWithGroupingTestCases.class);
        suite.addTestSuite(SingleElementKeyUsingMapTestCases.class);
        suite.addTestSuite(NonStringKeyTypeTestCases.class);
        suite.addTestSuite(NestedAttributeKeyTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.SingleKeyTestCases"};
        junit.textui.TestRunner.main(arguments);
    }
}