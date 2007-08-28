/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Session;

/**
 * INTERNAL
 * <p><b>Purpose:</b>Provide a TopLink implementation of JAXBIntrospector
 * <p><b>Responsibilities:</b><ul>
 * <li>Determine if a an object has an associated Global Element</li>
 * <li>Get an element QName for an object that has an associated global element</li>
 * </ul>
 * <p>This class is the TopLink implementation of JAXBIntrospector. An Introspector is created
 * by a JAXBContext and allows the user to access certain peices of meta-data about an instance
 * of a JAXB bound class.
 * 
 * @see javax.xml.bind.JAXBIntrospector
 * @see org.eclipse.persistence.jaxb.JAXB20Context
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 */

public class JAXBIntrospector extends javax.xml.bind.JAXBIntrospector {
    private XMLContext context;
    public JAXBIntrospector(XMLContext context) {
        this.context = context;
    }
    
    public boolean isElement(Object obj) {
    	if (obj instanceof JAXBElement) {
    		return true;
    	}
    	
        Session session = context.getSession(obj);
        if(session == null) {
            return false;
        }
        XMLDescriptor descriptor = (XMLDescriptor)session.getDescriptor(obj);
        if(descriptor == null) {
            return false;
        }
        
        return descriptor.getDefaultRootElement() != null;
    }
    
    public QName getElementName(Object obj) {
        if(!isElement(obj)) {
            return null;
        }
        XMLDescriptor descriptor = (XMLDescriptor)context.getSession(obj).getDescriptor(obj);
        String rootElem = descriptor.getDefaultRootElement();
        int prefixIndex = rootElem.indexOf(":");
        if(prefixIndex == -1) {
            return new QName(rootElem);
        } else {
            String prefix = rootElem.substring(0, prefixIndex);
            String localPart = rootElem.substring(prefixIndex + 1);
            String URI = descriptor.getNamespaceResolver().resolveNamespacePrefix(prefix);
            return new QName(URI, localPart);
        }
        
    }     
}