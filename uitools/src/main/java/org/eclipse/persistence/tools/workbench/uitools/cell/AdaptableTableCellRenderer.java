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
package org.eclipse.persistence.tools.workbench.uitools.cell;

import javax.swing.Icon;

/**
 * Delegate rendering to an adapter.
 */
public class AdaptableTableCellRenderer
    extends SimpleTableCellRenderer
{
    private CellRendererAdapter adapter;

    /**
     * Construct a renderer with the specified adapter.
     */
    public AdaptableTableCellRenderer(CellRendererAdapter adapter) {
        super();
        if (adapter == null) {
            throw new NullPointerException();
        }
        this.adapter = adapter;
    }

    @Override
    protected Icon buildIcon(Object value) {
        return this.adapter.buildIcon(value);
    }

    @Override
    protected String buildText(Object value) {
        return this.adapter.buildText(value);
    }

    @Override
    protected String buildToolTipText(Object value) {
        return this.adapter.buildToolTipText(value);
    }

    @Override
    protected String buildAccessibleName(Object value) {
        return this.adapter.buildAccessibleName(value);
    }

}
