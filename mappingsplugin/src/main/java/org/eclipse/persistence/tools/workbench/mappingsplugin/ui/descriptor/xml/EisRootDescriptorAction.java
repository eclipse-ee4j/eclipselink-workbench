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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.ChangeDescriptorTypeAction;



final class EisRootDescriptorAction extends ChangeDescriptorTypeAction {

    EisRootDescriptorAction(WorkbenchContext context) {
        super(context);
    }

    @Override
    protected void initialize() {
        super.initialize();
        initializeTextAndMnemonic("EIS_ROOT_DESCRIPTOR_ACTION");
        initializeIcon("descriptor.eis.root");
        initializeToolTipText("EIS_ROOT_DESCRIPTOR_ACTION.toolTipText");
    }

    @Override
    protected MWDescriptor morphDescriptor(MWDescriptor descriptor) {
        return  ((MWEisDescriptor) descriptor).asRootEisDescriptor();
    }

    @Override
    protected boolean shouldBeSelected(ApplicationNode selectedNode) {
        return ((EisDescriptorNode) selectedNode).isRootDescriptor();
    }
}
