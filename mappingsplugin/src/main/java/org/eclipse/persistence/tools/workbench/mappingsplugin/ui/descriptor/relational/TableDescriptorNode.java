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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorPackageNode;


public final class TableDescriptorNode extends RelationalClassDescriptorNode {

    // ********** constructors/initialization **********

    public TableDescriptorNode(MWTableDescriptor descriptor, DescriptorPackageNode parentNode) {
        super(descriptor, parentNode);
    }



    // ********** ApplicationNode implementation **********

    @Override
    public String helpTopicID() {
        return "descriptor.class";
    }

    @Override
    public String buildIconKey() {
        return    "descriptor.class";
    }

    MWTableDescriptor getTableDescriptor() {
        return (MWTableDescriptor) getMappingDescriptor();
    }

    // ********** DescriptorNode implementation **********

    @Override
    protected String accessibleNameKey() {
        return "ACCESSIBLE_TABLE_DESCRIPTOR_NODE";
    }

    // ********** DescriptorNode overrides *************

    @Override
    public boolean supportsInterfaceAliasPolicy() {
        return true;
    }

    // ********** MWApplicationNode overrides **********

    @Override
    protected Class propertiesPageClass() {
        return TableDescriptorTabbedPropertiesPage.class;
    }


    // ********** MWRelationalClassDescriptorNode overrides **********

    @Override
    public boolean isTableDescriptor() {
        return true;
    }

    @Override
    public boolean supportsTransactionalDescriptorProperties() {
        return true;
    }

    @Override
    public void selectQueryKey(MWQueryKey queryKey, WorkbenchContext context) {
        ((TableDescriptorTabbedPropertiesPage) context.getPropertiesPage()).selectQueryKey(queryKey);
}
}
