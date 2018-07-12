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

import org.eclipse.persistence.tools.workbench.framework.action.AbstractToggleFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;


final class ActivateDescriptorAction extends AbstractToggleFrameworkAction {

    ActivateDescriptorAction(WorkbenchContext context)
    {
        super(context);
    }

    @Override
    protected void initialize()
    {
        super.initialize();
        this.setIcon(EMPTY_ICON);
        initializeTextAndMnemonic("ACTIVATE_DESCRIPTOR_ACTION");
        initializeToolTipText("ACTIVATE_DESCRIPTOR_ACTION");
    }

    @Override
    protected void execute() {
        boolean active = !isSelected();
        ApplicationNode[] nodes = this.selectedNodes();
        for (int i = nodes.length; i-- > 0; ) {
            ((MWDescriptor) nodes[i].getValue()).setActive(active);
        }
    }

    @Override
    protected boolean shouldBeEnabled(ApplicationNode selectedNode) {
        return true;
    }

    @Override
    protected boolean shouldBeSelected(ApplicationNode selectedNode) {
        return ((DescriptorNode) selectedNode).getDescriptor().isActive();
    }

    @Override
    protected String[] selectedPropertyNames() {
        return new String[] {MWDescriptor.ACTIVE_PROPERTY};
    }

    protected ApplicationNode[] buildSelectedNodes() {
        return selectedProjectNodes();
    }
}
