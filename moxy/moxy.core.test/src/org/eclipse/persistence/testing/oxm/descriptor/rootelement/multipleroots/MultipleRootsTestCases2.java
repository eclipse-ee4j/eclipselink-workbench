/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots;

import java.io.InputStream;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.MailingAddress;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class MultipleRootsTestCases2 extends XMLMappingTestCases {
    Document writeControlDocument;
    private final static String SOMEADDRESS = "org/eclipse/persistence/testing/oxm/descriptor/rootelement/multipleroots/MultipleRootsSomeAddress.xml";

    public MultipleRootsTestCases2(String name) throws Exception {
        super(name);
        setControlDocument(SOMEADDRESS);
        setProject(new MultipleRootsProject());
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots.MultipleRootsTestCases2" };
        TestRunner.main(arguments);
    }

    protected Object getControlObject() {
        MailingAddress address = new MailingAddress();
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName("someAddress");
        xmlRoot.setObject(address);
        return xmlRoot;
    }
}