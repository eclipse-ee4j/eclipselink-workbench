/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlroot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexDifferentPrefixTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexDifferentPrefixWithDRTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexNoNRWithPrefixTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexNoNamespaceResolverTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexNoPrefixTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexNullUriTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.XMLRootComplexTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootSimpleTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootNoPrefixTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.simple.XMLRootNullUriTestCases;

public class XMLRootTestSuite extends TestCase {
    public XMLRootTestSuite(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlroot.XMLRootTestSuite" };
        //System.setProperty("useDeploymentXML", "true");
        //System.setProperty("useDocPres", "true");
        //System.setProperty("useLogging", "true");
        //System.setProperty("useSAXParsing", "true");
        junit.textui.TestRunner.main(arguments);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("XMLRoot Test Cases");
        suite.addTestSuite(XMLRootComplexTestCases.class);
        suite.addTestSuite(XMLRootComplexDifferentPrefixWithDRTestCases.class);
        suite.addTestSuite(XMLRootComplexNoPrefixTestCases.class);
        suite.addTestSuite(XMLRootComplexNoNamespaceResolverTestCases.class);
        suite.addTestSuite(XMLRootComplexNullUriTestCases.class);
        suite.addTestSuite(XMLRootComplexNoNRWithPrefixTestCases.class);
        suite.addTestSuite(XMLRootComplexDifferentPrefixTestCases.class);
        suite.addTestSuite(XMLRootSimpleTestCases.class);
        suite.addTestSuite(XMLRootNoPrefixTestCases.class);
        suite.addTestSuite(XMLRootNullUriTestCases.class);
        return suite;
    }
}