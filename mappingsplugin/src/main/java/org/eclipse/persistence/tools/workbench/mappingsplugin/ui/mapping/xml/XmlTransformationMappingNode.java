/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.xml;

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml.XmlDescriptorNode;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


public final class XmlTransformationMappingNode
    extends MappingNode
{
    // **************** Constructors ******************************************

    public XmlTransformationMappingNode(MWXmlTransformationMapping mapping, SelectionActionsPolicy mappingNodeTypePolicy, XmlDescriptorNode parent) {
        super(mapping, mappingNodeTypePolicy, parent);
    }


    // ************** AbstractApplicationNode overrides *************

    @Override
    protected String accessibleNameKey() {
        return "ACCESSIBLE_XML_TRANSFORMATION_MAPPING_NODE";
    }


    // **************** MappingNode contract **********************************

    @Override
    protected String buildIconKey() {
        return "mapping.transformation";
    }


    // **************** ApplicationNode contract ******************************

    @Override
    public String helpTopicID() {
//        return this.getDescriptorNode().mappingHelpTopicPrefix() + ".transformation";
        return "mapping.xmlTransformation"; // For 10.1.3
    }


    // ********** MWApplicationNode overrides **********

    @Override
    protected Class propertiesPageClass() {
        XmlDescriptorNode parentNode = (XmlDescriptorNode) getDescriptorNode();
        return parentNode.propertiesPageClassForTransformationMapping();
    }

}
