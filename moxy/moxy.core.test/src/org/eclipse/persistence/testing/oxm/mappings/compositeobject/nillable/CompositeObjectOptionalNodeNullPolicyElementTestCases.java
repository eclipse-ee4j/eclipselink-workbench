/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/* $Header: CompositeObjectOptionalNodeNullPolicyElementTestCases.java 20-jul-2007.12:53:59 bdoughan Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    bdoughan    07/20/07 - 
    gyorke      11/02/06 - 
    bdoughan    11/14/06 - 
    mfobrien    10/26/06 - Creation
 */

/**
 *  @version $Header: CompositeObjectOptionalNodeNullPolicyElementTestCases.java 20-jul-2007.12:53:59 bdoughan Exp $
 *  @author  mfobrien
 *  @since   11.1
 */

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;


import org.eclipse.persistence.oxm.mappings.NodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.OptionalNodeNullPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeObjectOptionalNodeNullPolicyElementTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = //
    	"org/eclipse/persistence/testing/oxm/mappings/compositeobject/nillable/CompositeObjectOptionalNodeNullPolicyElement.xml";

    public CompositeObjectOptionalNodeNullPolicyElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);

        NodeNullPolicy aNodeNullPolicy = OptionalNodeNullPolicy.getInstance();
        Project aProject = new CompositeObjectNodeNullPolicyProject(true);
        XMLCompositeObjectMapping aMapping = (XMLCompositeObjectMapping)aProject.getDescriptor(Team.class)//
        .getMappingForAttributeName("manager");
        aMapping.setNodeNullPolicy(aNodeNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
    	Team aTeam = new Team();
    	aTeam.setId(123);
    	aTeam.setName("Eng");
    	aTeam.setManager(null);
        return aTeam;
    }
}