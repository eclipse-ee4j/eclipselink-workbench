/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JAXBDOMTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("JAXB Test Suite");

        String contextPath = "org.eclipse.persistence.testing.oxm.jaxb.dom";
        System.setProperty("jaxb.test.contextpath", contextPath);
				String platform = System.getProperty("eclipselink.xml.platform");
        boolean jaxpPlatform = platform.equalsIgnoreCase("org.eclipse.persistence.platform.xml.jaxp.JAXPPlatform");
				
        suite.addTestSuite(MarshallerTestCases.class);
        suite.addTestSuite(UnmarshallerTestCases.class);
				if(!jaxpPlatform){
					suite.addTestSuite(ValidatorTestCases.class);
				}
        suite.addTestSuite(UnmarshallValidationTestCases.class);
        suite.addTestSuite(MarshallerPropertiesTestCases.class);
        suite.addTestSuite(MarshallerFormattingTestCases.class);
        suite.addTest(MarshallerEncodingTestCases.suite());
        suite.addTestSuite(MarshallerFragmentTestCases.class);

        return suite;
    }
}