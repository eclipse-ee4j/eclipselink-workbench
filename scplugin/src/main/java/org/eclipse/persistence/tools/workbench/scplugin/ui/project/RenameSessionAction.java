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
package org.eclipse.persistence.tools.workbench.scplugin.ui.project;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.AbstractApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.SessionAdapter;
import org.eclipse.persistence.tools.workbench.scplugin.model.adapter.TopLinkSessionsAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;


public class RenameSessionAction extends AbstractFrameworkAction {

    public RenameSessionAction( WorkbenchContext context) {
        super( context);
    }

    @Override
    protected void initialize() {
        this.initializeText( "RENAME_SESSION");
        this.initializeMnemonic( "RENAME_SESSION");
        // no accelerator
        this.initializeIcon( "RENAME");
        this.initializeToolTipText( "RENAME_SESSION.TOOL_TIP");
    }

    @Override
    protected void execute( ApplicationNode selectedNode) {

        SessionAdapter session = ( SessionAdapter)selectedNode.getValue();
        TopLinkSessionsAdapter sessions = ( TopLinkSessionsAdapter)session.getParent();
        SimplePropertyValueModel stringHolder = new SimplePropertyValueModel();
        stringHolder.setValue( session.getName());

        RenameDialog dialog = new RenameDialog( getWorkbenchContext(), stringHolder, sessions.getAllSessionsNames());
        dialog.show();
        if( dialog.wasConfirmed()) {

            navigatorSelectionModel().pushExpansionState();
            session.setName(( String)stringHolder.getValue());
            navigatorSelectionModel().popAndRestoreExpansionState();

            ((AbstractApplicationNode) selectedNode.getProjectRoot()).selectDescendantNodeForValue( session, navigatorSelectionModel());
        }
    }
}