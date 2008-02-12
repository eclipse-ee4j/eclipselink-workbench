/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;


/**
 * close the selected nodes;
 * enabled when nodes are selected;
 * this action is part of the FileMenu and MainToolBar, while SaveAction is
 * part of the SelectionMenu
 */
final class WorkbenchCloseAction
	extends AbstractFrameworkAction
{
	/** we need access to the node manager's internal api */
	private FrameworkNodeManager nodeManager;
	private TreeSelectionListener treeSelectionListener;
	private WindowListener windowListener;


	WorkbenchCloseAction(WorkbenchContext context, FrameworkNodeManager nodeManager) {
		super(context);
		this.nodeManager = nodeManager;
	}

	protected void initialize() {
		super.initialize();
		this.initializeTextAndMnemonic("file.close");
		this.initializeIcon("file.close");
		this.initializeToolTipText("file.close.toolTipText");
		this.initializeAccelerator("file.close.ACCELERATOR");

		this.treeSelectionListener = buildTreeSelectionListener();
		this.windowListener = buildWorkbenchWindowListener();
		
		this.currentWindow().addWindowListener(this.windowListener);
		this.navigatorSelectionModel().addTreeSelectionListener(this.treeSelectionListener);
		
		this.updateEnabledState();
	}

	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				synchronized (WorkbenchCloseAction.this) {
					WorkbenchCloseAction.this.update();
				}
			}
		};
	}

	private WindowListener buildWorkbenchWindowListener() {
		return new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				WorkbenchCloseAction.this.workbenchWindowClosed();
			}
		};
	}

	void update() {
		this.updateEnabledState();
	}
	
	private void updateEnabledState() {
		this.setEnabled(this.selectedProjectNodes().length > 0);
	}
	
	protected void execute() {
		this.nodeManager.close(this.selectedProjectNodes(), this.getWorkbenchContext());
	}
	
	void workbenchWindowClosed() {
		this.navigatorSelectionModel().removeTreeSelectionListener(this.treeSelectionListener);
		// stop listening to the window, or, for some odd reason,
		// we will receive the WINDOW_CLOSED event twice...
		this.currentWindow().removeWindowListener(this.windowListener);
	}

}