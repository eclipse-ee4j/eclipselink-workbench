/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.events;

import java.util.ArrayList;
import java.util.Vector;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.*;

public class EmployeeProject extends Project 
{
    public EmployeeProject() 
    {
        super();
        addEmployeeDescriptor();
        addAddressDescriptor();
        addPhoneNumberDescriptor();
    }
    
    public void addEmployeeDescriptor() 
    {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");
        
        XMLCompositeObjectMapping addr = new XMLCompositeObjectMapping();
        addr.setAttributeName("address");
        addr.setXPath("address");
        addr.setReferenceClass(Address.class);
        descriptor.addMapping(addr);
        
        XMLCompositeCollectionMapping phone = new XMLCompositeCollectionMapping();
        phone.setAttributeName("phoneNumbers");
        phone.setXPath("phone-number");
        phone.setReferenceClass(PhoneNumber.class);
        phone.useCollectionClass(ArrayList.class);
        descriptor.addMapping(phone);
        
        XMLAnyObjectMapping object = new XMLAnyObjectMapping();
        object.setAttributeName("anyObject");
        object.setXPath("any-object");
        descriptor.addMapping(object);
        
        XMLAnyCollectionMapping anyCollection = new XMLAnyCollectionMapping();
        anyCollection.setAttributeName("anyCollection");
        anyCollection.setXPath("any-collection");
        anyCollection.useCollectionClass(ArrayList.class);
        descriptor.addMapping(anyCollection);

        this.addDescriptor(descriptor);
    }
    public void addAddressDescriptor() 
    {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.setDefaultRootElement("address");
        
        XMLDirectMapping street = new XMLDirectMapping();
        street.setAttributeName("street");
        street.setXPath("street/text()");
        descriptor.addMapping(street);
        
        this.addDescriptor(descriptor);
    }
    
    public void addPhoneNumberDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PhoneNumber.class);
        descriptor.setDefaultRootElement("phone-number");
        
        XMLDirectMapping num = new XMLDirectMapping();
        num.setAttributeName("number");
        num.setXPath("text()");
        descriptor.addMapping(num);
        
        this.addDescriptor(descriptor);
    }
}