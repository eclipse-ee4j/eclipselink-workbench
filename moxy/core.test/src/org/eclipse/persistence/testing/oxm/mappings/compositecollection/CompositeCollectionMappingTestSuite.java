/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositecollection;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.errortests.CompositeCollectionErrorTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyname.IdentifiedByNameTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbynamespace.IdentifiedByNamespaceTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.identifiedbyposition.IdentifiedByPositionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.map.CompositeCollectionMapNullChildTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.map.CompositeCollectionMapTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.mappingxpathcollision.MappingXpathCollisionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.nested.CompositeCollectionNestedTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.setmethod.SetMethodTestCases;

public class CompositeCollectionMappingTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Composite Collection Mapping Test Suite");
        suite.addTest(IdentifiedByNameTestCases.suite());
        suite.addTest(IdentifiedByNamespaceTestCases.suite());
        suite.addTest(IdentifiedByPositionTestCases.suite());
        suite.addTestSuite(CompositeCollectionNestedTestCases.class);
        suite.addTestSuite(CompositeCollectionMapTestCases.class);
        suite.addTestSuite(CompositeCollectionMapNullChildTestCases.class);
        suite.addTestSuite(CompositeCollectionErrorTestCases.class);
        suite.addTestSuite(SetMethodTestCases.class);
        suite.addTestSuite(MappingXpathCollisionTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositecollection.CompositeCollectionMappingTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }
}