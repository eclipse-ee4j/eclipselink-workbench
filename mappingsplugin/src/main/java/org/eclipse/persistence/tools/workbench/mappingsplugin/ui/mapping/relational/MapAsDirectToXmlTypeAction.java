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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWDirectToXmlTypeMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.ChangeMappingTypeAction;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


final class MapAsDirectToXmlTypeAction extends ChangeMappingTypeAction {

    MapAsDirectToXmlTypeAction(WorkbenchContext context) {
        super(context);
    }

    @Override
    protected void initialize() {
        super.initialize();
        this.initializeIcon("mapping.directToXmlType");
        this.initializeText("MAP_AS_DIRECT_TO_XML_TYPE_ACTION");
        this.initializeMnemonic("MAP_AS_DIRECT_TO_XML_TYPE_ACTION");
        this.initializeToolTipText("MAP_AS_DIRECT_TO_XML_TYPE_ACTION.toolTipText");
    }

    @Override
    protected void engageValueEnabled(AbstractApplicationNode node) {
        super.engageValueEnabled(node);
        ((MappingNode) node).database().addPropertyChangeListener(MWDatabase.DATABASE_PLATFORM_PROPERTY, getEnabledStateListener());
    }

    @Override
    protected void disengageValueEnabled(AbstractApplicationNode node) {
        super.disengageValueEnabled(node);
        ((MappingNode) node).database().removePropertyChangeListener(MWDatabase.DATABASE_PLATFORM_PROPERTY, getEnabledStateListener());
    }


    // ************ ChangeMappingTypeAction implementation ***********

    @Override
    protected MWMapping morphMapping(MWMapping mapping) {
        return mapping.asMWDirectToXmlTypeMapping();
    }

    @Override
    protected MWMapping addMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute) {
        return ((MWRelationalClassDescriptor) descriptor).addDirectToXmlTypeMapping(attribute);
    }

    @Override
    protected Class mappingClass() {
        return MWDirectToXmlTypeMapping.class;
    }

    @Override
    protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
        return super.shouldBeEnabled(selectedNode) &&
                ((MappingNode) selectedNode).database().getDatabasePlatform().containsDatabaseTypeNamed("XMLTYPE");
    }



}
