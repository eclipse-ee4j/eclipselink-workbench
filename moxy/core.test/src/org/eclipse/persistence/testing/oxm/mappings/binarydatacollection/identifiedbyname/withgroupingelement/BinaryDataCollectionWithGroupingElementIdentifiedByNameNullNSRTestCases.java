/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

/* $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases.java 19-oct-2006.17:10:30 mfobrien Exp $ */

/**
 *  @version $Header: BinaryDataCollectionWithGroupingElementIdentifiedByNameXOPonNSRTestCases.java 19-oct-2006.17:10:30 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.identifiedbyname.withgroupingelement;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentMarshaller;
import org.eclipse.persistence.testing.oxm.mappings.binarydatacollection.MyAttachmentUnmarshaller;

/**
 * Not supported
 * base64Binary as attribute - no singlenode\xmlattribute
 *
 * @author mfobrien
 *
 */
public class BinaryDataCollectionWithGroupingElementIdentifiedByNameNullNSRTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydatacollection/identifiedbyname/withgroupingelement/BinaryDataCollectionWithGroupElemIdentifiedByNameNullNSR.xml";
    private final static int CONTROL_ID = 123;

    public BinaryDataCollectionWithGroupingElementIdentifiedByNameNullNSRTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        // NSR must be null
        NamespaceResolver namespaceResolver = null;//new NamespaceResolver();

        setProject(new BinaryDataCollectionWithGroupingElementIdentifiedByNameProject(namespaceResolver));
    }

    protected Object getControlObject() {
        return Employee.example1();
    }

    protected void setSession(String sessionName) {
        super.setSession(sessionName);
        // setup attachment Marshaller/Unmarshaller	  
        xmlMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
    }

    protected void setProject(Project project) {
        super.setProject(project);
        // setup attachment Marshaller/Unmarshaller
        xmlMarshaller.setAttachmentMarshaller(new MyAttachmentMarshaller());
        xmlUnmarshaller.setAttachmentUnmarshaller(new MyAttachmentUnmarshaller());
    }
}