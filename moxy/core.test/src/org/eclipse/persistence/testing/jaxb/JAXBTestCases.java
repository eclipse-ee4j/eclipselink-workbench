/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb;

import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.jaxb.*;

public abstract class JAXBTestCases extends XMLMappingTestCases {
    Class[] classes;

    JAXBContext jaxbContext;
    Marshaller jaxbMarshaller;
    Unmarshaller jaxbUnmarshaller;
    Generator generator;
    
    public JAXBTestCases(String name) throws Exception {
        super(name);
    }

    public XMLContext getXMLContext(Project project) {
        return new XMLContext(project);
    }

    protected void setProject(Project project) {
        xmlContext = getXMLContext(project);
        xmlMarshaller = xmlContext.createMarshaller();
        xmlMarshaller.setFormattedOutput(false);
		xmlUnmarshaller = xmlContext.createUnmarshaller();
        try {
            jaxbContext = new org.eclipse.persistence.jaxb.JAXBContext(xmlContext, generator);
            jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            
        }catch (Exception ex) {}
    }

    public void setClasses(Class[] newClasses) throws Exception {
        this.classes = newClasses;
        generator = new Generator(new JavaModelInputImpl(classes, new JavaModelImpl()));
        Project proj = generator.generateProject();
        // need to make sure that the java class is set properly on each 
        // descriptor when using java classname - req'd for JOT api implementation 
        for (Iterator<ClassDescriptor> descriptorIt = proj.getOrderedDescriptors().iterator(); descriptorIt.hasNext(); ) {
            ClassDescriptor descriptor = descriptorIt.next();
            if (descriptor.getJavaClass() == null) {
                descriptor.setJavaClass(ConversionManager.getDefaultManager().convertClassNameToClass(descriptor.getJavaClassName()));
            }
        }
        setProject(proj);
    }

    public Class[] getClasses() {
        return classes;
    }
    
    public JAXBContext getJAXBContext() {
        return jaxbContext;
    }
    
    public Marshaller getJAXBMarshaller() {
        return jaxbMarshaller;
    }
    
    public Unmarshaller getJAXBUnmarshaller() {
        return jaxbUnmarshaller;
    }
    
}