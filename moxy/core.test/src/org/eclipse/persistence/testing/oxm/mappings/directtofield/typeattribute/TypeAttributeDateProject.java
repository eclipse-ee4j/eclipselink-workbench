/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directtofield.typeattribute;

import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import java.util.*;
import javax.xml.namespace.QName;

public class TypeAttributeDateProject extends Project {
    public TypeAttributeDateProject() {
        addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");
        NamespaceResolver resolver = new NamespaceResolver();
        resolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, XMLConstants.SCHEMA_INSTANCE_URL);
        resolver.put(XMLConstants.SCHEMA_PREFIX, XMLConstants.SCHEMA_URL);
        descriptor.setNamespaceResolver(resolver);

        XMLDirectMapping identifierMapping = new XMLDirectMapping();
        identifierMapping.setAttributeName("identifier");
        XMLField field = new XMLField();
        field.setIsTypedTextField(true);
        field.setXPath("identifier/text()");        
				field.addConversion(XMLConstants.DATE_QNAME, GregorianCalendar.class);        
        identifierMapping.setField(field);
        descriptor.addMapping(identifierMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("first-name/text()");
        descriptor.addMapping(firstNameMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath("last-name/text()");
        descriptor.addMapping(lastNameMapping);

        return descriptor;
    }
}