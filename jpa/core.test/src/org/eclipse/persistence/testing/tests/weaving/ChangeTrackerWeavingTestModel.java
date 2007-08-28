/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.weaving;

import java.util.Enumeration;

import org.eclipse.persistence.testing.framework.JUnitTestCase;
import org.eclipse.persistence.testing.framework.TestModel;

public class ChangeTrackerWeavingTestModel extends TestModel {
    
    public ChangeTrackerWeavingTestModel () {
        setDescription("Tests to ensure weaving properly inserts constructs for change tracking");
    }
    
    public void addTests() {
        junit.framework.TestSuite testsuite = (junit.framework.TestSuite)ChangeTrackerWeavingTestSuite.suite();
        for (Enumeration e = testsuite.tests(); e.hasMoreElements();) {
            junit.framework.TestCase testcase = (junit.framework.TestCase)e.nextElement();
            addTest(new JUnitTestCase(testcase));
        }
    }
}