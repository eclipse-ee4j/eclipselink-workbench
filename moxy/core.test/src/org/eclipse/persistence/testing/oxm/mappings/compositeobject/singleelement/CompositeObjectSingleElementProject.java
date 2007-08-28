/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.singleelement;

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

public class CompositeObjectSingleElementProject extends Project {

  public CompositeObjectSingleElementProject() {
    addDescriptor(getEmployeeDescriptor());
    addDescriptor(getEmailAddressDescriptor());
  }

  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(Employee.class);
    descriptor.setDefaultRootElement("employee");
   
    XMLDirectMapping idMapping = new XMLDirectMapping();
    idMapping.setXPath("id/text()");
    idMapping.setAttributeName("id");
    descriptor.addMapping(idMapping);   

    XMLCompositeObjectMapping emailMapping = new XMLCompositeObjectMapping();
    emailMapping.setAttributeName("emailAddress");
    emailMapping.setXPath("info/email-address");
    emailMapping.setGetMethodName("getEmailAddress");
    emailMapping.setSetMethodName("setEmailAddress");
    emailMapping.setReferenceClass(EmailAddress.class);
    descriptor.addMapping(emailMapping);
    
    return descriptor;
  }

  private XMLDescriptor getEmailAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(EmailAddress.class);
    
    XMLDirectMapping userIDMapping = new XMLDirectMapping();
    userIDMapping.setAttributeName("userID");
    userIDMapping.setXPath("user-id/text()");
    descriptor.addMapping(userIDMapping);   

    return descriptor;
  }  
  
}