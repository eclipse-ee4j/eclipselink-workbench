/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyposition;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyposition.xmlelement.DirectToXMLElementIdentifiedByPositionTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyposition.xmlelement.DirectToXMLElementIdentifiedByPosition2TestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyposition.xmlelement.DirectToXMLElementIdentifiedByPositionNullElementTestCases;

public class IdentifiedByPositionTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("Identified By Position Test Cases");
        suite.addTestSuite(DirectToXMLElementIdentifiedByPositionTestCases.class);
        suite.addTestSuite(DirectToXMLElementIdentifiedByPosition2TestCases.class);
        suite.addTestSuite(DirectToXMLElementIdentifiedByPositionNullElementTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyposition.IdentifiedByPositionTestCases" };
        junit.swingui.TestRunner.main(arguments);
    }
}