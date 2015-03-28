/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.SchemaContextChooser;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


final class EisCompositeDescriptorInfoPropertiesPage extends ScrollablePropertiesPage {

    EisCompositeDescriptorInfoPropertiesPage(PropertyValueModel eisDescriptorNodeHolder, WorkbenchContextHolder contextHolder) {
        super(eisDescriptorNodeHolder, contextHolder);
    }

    protected Component buildPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints constraints = new GridBagConstraints();

        // schema context chooser label
        JLabel schemaContextLabel = XmlDescriptorComponentFactory.buildSchemaContextLabel(resourceRepository());
        constraints.gridx      = 0;
        constraints.gridy      = 0;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 0;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.NONE;
        constraints.anchor     = GridBagConstraints.LINE_START;
        constraints.insets     = new Insets(0, 0, 0, 0);
        panel.add(schemaContextLabel, constraints);
        addAlignLeft(schemaContextLabel);

        // schema context chooser
        SchemaContextChooser schemaContextChooser = XmlDescriptorComponentFactory.buildSchemaContextChooser(getSelectionHolder(), getWorkbenchContextHolder(), schemaContextLabel);
        constraints.gridx      = 1;
        constraints.gridy      = 0;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 0;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.CENTER;
        constraints.insets     = new Insets(0, 5, 0, 0);
        panel.add(schemaContextChooser, constraints);

        // comment
        JComponent commentPanel = SwingComponentFactory.buildCommentPanel(getSelectionHolder(), resourceRepository());
        constraints.gridx      = 0;
        constraints.gridy      = 1;
        constraints.gridwidth  = 2;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.HORIZONTAL;
        constraints.anchor     = GridBagConstraints.FIRST_LINE_START;
        constraints.insets     = new Insets(5, 0, 0, 0);
        panel.add(commentPanel, constraints);
        this.addHelpTopicId(commentPanel, "descriptor.general.comment");

        addHelpTopicId(panel, helpTopicId());
        return panel;
    }


    private String helpTopicId() {
        return "eisCompDescriptor.descriptorInfo";
    }

}
