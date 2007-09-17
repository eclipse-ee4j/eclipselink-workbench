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

import java.util.Iterator;

import javax.xml.bind.Binder;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.compiler.MarshalCallback;
import org.eclipse.persistence.jaxb.compiler.UnmarshalCallback;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide a TopLink implementation of the JAXBContext interface.
 * <p><b>Responsibilities:</b><ul>
 * <li>Create Marshaller instances</li>
 * <li>Create Unmarshaller instances</li>
 * <li>Create Binder instances</li>
 * <li>Create Introspector instances</li>
 * <li>Create Validator instances</li>
 * <li>Generate Schema Files</li>
 * <ul>
 * <p>This is the TopLink JAXB 2.0 implementation of javax.xml.bind.JAXBContext. This class
 * is created by the JAXBContextFactory and is used to create Marshallers, Unmarshallers, Validators,
 * Binders and Introspectors. A JAXBContext can also be used to create Schema Files.
 * 
 * @see javax.xml.bind.JAXBContext
 * @see org.eclipse.persistence.jaxb.JAXBMarshaller
 * @see org.eclipse.persistence.jaxb.JAXBUnmarshaller
 * @see org.eclipse.persistence.jaxb.JAXBBinder
 * @see org.eclipse.persistence.jaxb.JAXBIntrospector
 * 
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 */

public class JAXBContext extends javax.xml.bind.JAXBContext {
    private XMLContext xmlContext;
    private org.eclipse.persistence.jaxb.compiler.Generator generator;
    
    public JAXBContext(XMLContext context) {
        super();
        xmlContext = context;
    }
    
    public JAXBContext(XMLContext context, Generator generator) {
        super();
        this.xmlContext = context;
        this.generator = generator;
    }
    
    public void generateSchema(SchemaOutputResolver outputResolver) {
        if(generator == null) {
            return;
        }
        generator.generateSchemaFiles(outputResolver, null);
    }
    
    public Marshaller createMarshaller() {
    	// create a JAXBIntrospector and set it on the marshaller
        JAXBMarshaller marshaller = new JAXBMarshaller(xmlContext.createMarshaller(), new JAXBIntrospector(xmlContext));
        if (generator != null && generator.hasMarshalCallbacks()) {
            // initialize each callback in the map
            for (Iterator callIt = generator.getMarshalCallbacks().keySet().iterator(); callIt.hasNext(); ) {
                MarshalCallback cb = (MarshalCallback) generator.getMarshalCallbacks().get(callIt.next());
                // TODO:  what classloader do we want to use here?
                cb.initialize(generator.getClass().getClassLoader());
            }
            marshaller.setMarshalCallbacks(generator.getMarshalCallbacks());
        }
        return marshaller;
    }

    public Unmarshaller createUnmarshaller() {
        JAXBUnmarshaller unmarshaller = new JAXBUnmarshaller(xmlContext.createUnmarshaller());
        if (generator != null && generator.hasUnmarshalCallbacks()) {
            // initialize each callback in the map
            for (Iterator callIt = generator.getUnmarshalCallbacks().keySet().iterator(); callIt.hasNext(); ) {
                UnmarshalCallback cb = (UnmarshalCallback) generator.getUnmarshalCallbacks().get(callIt.next());
                // TODO:  what classloader do we want to use here?
                cb.initialize(generator.getClass().getClassLoader());
            }
            unmarshaller.setUnmarshalCallbacks(generator.getUnmarshalCallbacks());
        }
        return unmarshaller;
    }

    public Validator createValidator() {
        return new JAXBValidator(xmlContext.createValidator());
    }
    
    public Binder createBinder() {
        return new JAXBBinder(this.xmlContext);
    }
    
    public JAXBIntrospector createJAXBIntrospector() {
        return new JAXBIntrospector(xmlContext);
    }
}