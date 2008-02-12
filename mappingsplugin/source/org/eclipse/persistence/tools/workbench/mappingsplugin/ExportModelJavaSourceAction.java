/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.sourcegen.*;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


public class ExportModelJavaSourceAction extends AbstractFrameworkAction {

	ExportModelJavaSourceAction(WorkbenchContext context) {
		super(context);
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("EXPORT_MODEL_JAVA_SOURCE_PROJECT");
		this.initializeAccelerator("EXPORT_MODEL_JAVA_SOURCE_PROJECT.accelerator");
		this.initializeToolTipText("EXPORT_MODEL_JAVA_SOURCE_PROJECT.toolTipText");
		this.initializeIcon("GENERATE_JAVA");
	}
	
	protected void execute() {
		ApplicationNode[] projectNodes = this.selectedProjectNodes();
		for (int i = 0; i < projectNodes.length; i++) {
			ModelSourceGenerationCoordinator coordinator = new ModelSourceGenerationCoordinator(this.getWorkbenchContext());
			MWProject project = (MWProject) projectNodes[i].getValue();
			coordinator.exportModelJavaSource(project, CollectionTools.collection(project.descriptors()));
		}
	}	

}