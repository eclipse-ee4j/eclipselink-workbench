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
package org.eclipse.persistence.tools.workbench.scplugin.ui.preferences;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;


/**
 * Preferences node for general settings used by the sessions configuration
 * plug-in.
 */
public class SCPreferencesNode extends AbstractPreferencesNode
{
    public SCPreferencesNode(PreferencesContext context)
    {
        super(context);
    }

    @Override
    protected String buildDisplayString()
    {
        return resourceRepository().getString("PREFERENCES.SC");
    }

    @Override
    protected Component buildPropertiesPage()
    {
        return new SCPreferencesPage(getPreferencesContext());
    }

    @Override
    public String helpTopicId()
    {
        return "preferences.sessions.general";
    }

    @Override
    protected void initialize()
    {
        super.initialize();

        insert(new NewNamesPreferencesNode(getPreferencesContext()), 0);
        insert(new PlatformPreferencesNode(getPreferencesContext()), 1);
    }
}
