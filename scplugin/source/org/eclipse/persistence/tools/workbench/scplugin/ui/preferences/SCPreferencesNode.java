/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
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

	protected String buildDisplayString()
	{
		return resourceRepository().getString("PREFERENCES.SC");
	}

	protected Component buildPropertiesPage()
	{
		return new SCPreferencesPage(getPreferencesContext());
	}

	public String helpTopicId()
	{
		return "preferences.sessions.general";
	}

	protected void initialize()
	{
		super.initialize();

		insert(new NewNamesPreferencesNode(getPreferencesContext()), 0);
		insert(new PlatformPreferencesNode(getPreferencesContext()), 1);
	}
}