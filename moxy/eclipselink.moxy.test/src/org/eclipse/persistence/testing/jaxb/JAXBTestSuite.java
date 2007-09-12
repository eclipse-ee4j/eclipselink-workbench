/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb;

import org.eclipse.persistence.testing.jaxb.events.RootWithCompositeObjectTestCases;
import org.eclipse.persistence.testing.jaxb.xmlschematype.XmlSchemaTypeDateTestCases;
import org.eclipse.persistence.testing.jaxb.xmlschematype.XmlSchemaTypeTwoDatesTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueListTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueTestCases;
import org.eclipse.persistence.testing.jaxb.xmlvalue.XmlValueWithAttributesTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.complex.JAXBElementComplexTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.simple.JAXBElementSimpleTestCases;
import org.eclipse.persistence.testing.jaxb.xmlidref.XmlIdRefTestCases;
import org.eclipse.persistence.testing.jaxb.xmladapter.XmlAdapterTestSuite;
import org.eclipse.persistence.testing.jaxb.schemagen.SchemaGenTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBDOMTestSuite;
import org.eclipse.persistence.testing.jaxb.xmlmarshaller.JAXBSAXTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB20 Compiler Test Suite");
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.employee.JAXBEmployeeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNamespaceTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlrootelement.XmlRootElementNoNamespaceTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementNamespaceTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementNoNamespaceTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlelement.XmlElementCollectionTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeNamespaceTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeNoNamespaceTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlattribute.XmlAttributeCollectionTestCases.class);
        suite.addTestSuite(XmlValueTestCases.class);
        suite.addTestSuite(XmlValueWithAttributesTestCases.class);
        suite.addTestSuite(XmlValueListTestCases.class);
	//jaxb-mats bug due to time/timestamp changes to TopLink after 070609
        //suite.addTestSuite(XmlSchemaTypeDateTestCases.class);
        //suite.addTestSuite(XmlSchemaTypeTwoDatesTestCases.class);
        suite.addTestSuite(XmlIdRefTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumElementTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumAttributeTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumElementCollectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.xmlenum.XmlEnumAttributeCollectionTestCases.class);
        suite.addTestSuite(JAXBElementSimpleTestCases.class);
        suite.addTestSuite(JAXBElementComplexTestCases.class);
        suite.addTestSuite(RootWithCompositeObjectTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.jaxb.events.RootWithCompositeCollectionTestCases.class);
        suite.addTest(XmlAdapterTestSuite.suite());
        suite.addTest(SchemaGenTestSuite.suite());
        suite.addTest(JAXBDOMTestSuite.suite());
        suite.addTest(JAXBSAXTestSuite.suite());
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.JAXBTestSuite" };
        //junit.swingui.TestRunner.main(arguments);
        //System.setProperty("useLogging", "true");
        junit.textui.TestRunner.main(arguments);
    }
}