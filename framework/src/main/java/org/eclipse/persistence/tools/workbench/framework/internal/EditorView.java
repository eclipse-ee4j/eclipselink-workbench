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
package org.eclipse.persistence.tools.workbench.framework.internal;

import javax.swing.JLabel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * This subclass builds a label that simply says "Editor".
 */
final class EditorView
    extends AbstractEditorView
{

    /**
     * The resource repository will provide the label's icon,
     * text, and mnemonic.
     */
    EditorView(PropertyValueModel nodeHolder, WorkbenchContext context) {
        super(nodeHolder, context);
    }

    /**
     * The label is fixed for this view.
     * @see AbstractEditorView#buildLabel()
     */
    @Override
    JLabel buildLabel() {
        JLabel label = new JLabel(this.resourceRepository().getString("EDITOR_LABEL"));
        label.setDisplayedMnemonic(this.resourceRepository().getMnemonic("EDITOR_LABEL"));
        label.setIcon(this.resourceRepository().getIcon("editor"));
        return label;
    }

}
