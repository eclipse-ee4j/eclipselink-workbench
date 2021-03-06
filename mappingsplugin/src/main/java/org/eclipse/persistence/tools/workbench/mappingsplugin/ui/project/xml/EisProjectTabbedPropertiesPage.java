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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.UiCommonBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectTabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.TransactionalProjectDefaultsPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.UiProjectBundle;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.xml.login.EisLoginTabbedPropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.UiXmlBundle;


final class EisProjectTabbedPropertiesPage extends ProjectTabbedPropertiesPage {

    // this value is queried reflectively during plug-in initialization
    private static final Class[] REQUIRED_RESOURCE_BUNDLES = new Class[] {
        UiCommonBundle.class,
        UiXmlBundle.class,
        UiProjectBundle.class
    };


    EisProjectTabbedPropertiesPage(WorkbenchContext context) {
        super(context);
    }

    @Override
    protected void initializeTabs() {
        addTab(buildProjectGeneralPropertiesPage(),    "GENERAL_TAB_TITLE");
        addTab(buildProjectConnectionPropertiesPage(), "CONNECTION_TAB_TITLE");
        addTab(buildProjectDefaultsPropertiesPage(),   "DEFAULTS_TAB_TITLE");
        addTab(buildProjectOptionsPropertiesPage(),    "OPTIONS_TAB_TITLE");
    }

    protected Component buildProjectOptionsPropertiesPage() {
        return new XmlProjectOptionsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }

    @Override
    protected Component buildProjectDefaultsPropertiesPage()
    {
        return new TransactionalProjectDefaultsPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }

    protected Component buildProjectConnectionPropertiesPage()
    {
        return new EisLoginTabbedPropertiesPage(getNodeHolder(), getWorkbenchContextHolder());
    }
}
