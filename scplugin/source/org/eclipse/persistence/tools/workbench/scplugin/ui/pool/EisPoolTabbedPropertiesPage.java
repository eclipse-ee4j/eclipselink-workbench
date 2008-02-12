/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.ui.pool;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.scplugin.ui.pool.basic.EisPoolLoginPropertiesPage;


public class EisPoolTabbedPropertiesPage extends PoolTabbedPropertiesPage {

	public EisPoolTabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected Component buildLoginPropertiesPage() {
		return new EisPoolLoginPropertiesPage( this.getNodeHolder(), getWorkbenchContextHolder());
	}
}