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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;


final class MapInheritedAttributesToSuperclassAction extends AbstractMapInheritedAttributesAction
{
    MapInheritedAttributesToSuperclassAction(WorkbenchContext context)
    {
        super(context);
    }

    @Override
    protected void execute(MWMappingDescriptor descriptor) throws ClassNotFoundException
    {
        descriptor.mapInheritedAttributesToSuperclass();
    }

    @Override
    protected void initialize()
    {
        super.initialize();
        initializeTextAndMnemonic("MAP_INHERITED_ATTRIBUTES_TO_SUPERCLASS_MENU_ITEM");
    }

}
