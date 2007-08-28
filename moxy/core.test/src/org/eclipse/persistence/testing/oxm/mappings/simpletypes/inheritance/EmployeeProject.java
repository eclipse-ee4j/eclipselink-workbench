/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.inheritance;

// TopLink imports
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public class EmployeeProject extends Project {

  private NamespaceResolver namespaceResolver;

	public EmployeeProject() {
		super();
		
		namespaceResolver = new NamespaceResolver();
    namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		
		this.addDescriptor(getPersonDescriptor());
		this.addDescriptor(getEmployeeDescriptor());
	}

	XMLDescriptor getEmployeeDescriptor() {
		XMLDescriptor xmlDescriptor = new XMLDescriptor();
		xmlDescriptor.setJavaClass(Employee.class);
		xmlDescriptor.setDefaultRootElement("employee");
		xmlDescriptor.setNamespaceResolver(namespaceResolver);
		xmlDescriptor.getInheritancePolicy().setParentClass(Person.class);

		XMLDirectMapping mapping = new XMLDirectMapping();
		mapping.setAttributeName("jobTitle");
		mapping.setXPath("text()");
		xmlDescriptor.addMapping(mapping);
		
		return xmlDescriptor;
	}

	XMLDescriptor getPersonDescriptor() {
		XMLDescriptor xmlDescriptor = new XMLDescriptor();
		xmlDescriptor.setJavaClass(Person.class);
		xmlDescriptor.setDefaultRootElement("person");
		xmlDescriptor.setNamespaceResolver(namespaceResolver);		
		XMLField classIndicatorField = new XMLField("@xsi:type");		
    xmlDescriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);
		xmlDescriptor.getInheritancePolicy().addClassIndicator(Employee.class, "employee");

		XMLDirectMapping mapping = new XMLDirectMapping();
		mapping.setAttributeName("firstname");
		mapping.setXPath("firstName/text()");
		xmlDescriptor.addMapping(mapping);
		
		XMLDirectMapping mapping2 = new XMLDirectMapping();
		mapping2.setAttributeName("lastname");
		mapping2.setXPath("lastName/text()");
		xmlDescriptor.addMapping(mapping2);
		
		return xmlDescriptor;
	}
}