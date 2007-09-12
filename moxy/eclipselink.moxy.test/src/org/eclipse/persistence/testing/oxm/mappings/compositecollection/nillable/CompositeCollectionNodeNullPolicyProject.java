/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

/* $Header: CompositeCollectionNodeNullPolicyProject.java 02-nov-2006.10:57:13 gyorke Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    gyorke      11/02/06 - 
    mfobrien    10/26/06 - Creation
 */

/**
 *  @version $Header: CompositeCollectionNodeNullPolicyProject.java 02-nov-2006.10:57:13 gyorke Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.nillable;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class CompositeCollectionNodeNullPolicyProject extends Project {

    /**
     * Construct a project with a descriptor setup for all fields that do not use a default NodeNullPolicy
     * @param aMapping
     * @param fieldsAsElements
     */
    public CompositeCollectionNodeNullPolicyProject(boolean fieldsAsElements) {
        XMLDescriptor aTeamDescriptor = getTeamDescriptor(fieldsAsElements);
        XMLDescriptor anEmployeeDescriptor = getEmployeeDescriptor(fieldsAsElements);
        addDescriptor(aTeamDescriptor);
        addDescriptor(anEmployeeDescriptor);
    }

    private XMLDescriptor getTeamDescriptor(boolean fieldsAsElements) {
        // if all fields are attributes the use XPath format @id otherwise use id/text()
        String xPathPrepend;

        // if all fields are attributes the use XPath format @id otherwise use id/text()
        String xPathAppend;
        if (fieldsAsElements) {
            xPathPrepend = "";
            xPathAppend = "/text()";
        } else {
            xPathPrepend = "@";
            xPathAppend = "";
        }

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Team.class);
        descriptor.setDefaultRootElement("team");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath(xPathPrepend + "id" + xPathAppend);
        descriptor.addMapping(idMapping);

        XMLCompositeCollectionMapping developersMapping = new XMLCompositeCollectionMapping();
        developersMapping.setAttributeName("developers");
        developersMapping.setXPath("developer");
        developersMapping.setGetMethodName("getDevelopers");
        developersMapping.setSetMethodName("setDevelopers");
        developersMapping.setReferenceClass(Employee.class);
        descriptor.addMapping(developersMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("name");
        lastNameMapping.setXPath(xPathPrepend + "name" + xPathAppend);
        descriptor.addMapping(lastNameMapping);

        return descriptor;
    }

    /**
     * @return
     */
    private XMLDescriptor getEmployeeDescriptor(boolean fieldsAsElements) {
        // if all fields are attributes the use XPath format @id otherwise use id/text()
        String xPathPrepend;

        // if all fields are attributes the use XPath format @id otherwise use id/text()
        String xPathAppend;
        if (fieldsAsElements) {
            xPathPrepend = "";
            xPathAppend = "/text()";
        } else {
            xPathPrepend = "@";
            xPathAppend = "";
        }

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("manager");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath(xPathPrepend + "id" + xPathAppend);
        descriptor.addMapping(idMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath(xPathPrepend + "first-name" + xPathAppend);
        descriptor.addMapping(firstNameMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath(xPathPrepend + "last-name" + xPathAppend);
        descriptor.addMapping(lastNameMapping);

        return descriptor;
    }
}