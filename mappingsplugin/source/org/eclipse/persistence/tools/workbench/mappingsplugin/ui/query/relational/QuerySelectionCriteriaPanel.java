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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAutoGeneratedQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWEJBQLQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpressionQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWSQLQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStoredProcedureQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWStringQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsplugin.MappingsPlugin;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;




/**
 * QueryFormatPanel is one of the nested tabs found on the NamedQueries tab of a descriptor.
 * The user chooses a query format(expression, SQL, or EJBQL) and the appropriate panel
 * is shown and populated.
 */
final public class QuerySelectionCriteriaPanel extends AbstractPanel
{
    private AutoGeneratedQueryFormatPanel autoGeneratedQueryFormatPanel;
    private NonAutoGeneratedQueryFormatPanel nonAutoGeneratedQueryFormatPanel;
    private Map<Class<? extends MWQueryFormat>, JComponent> queryFormatPanelMap;
    private PropertyValueModel queryHolder;

    public QuerySelectionCriteriaPanel(PropertyValueModel queryHolder,
                                WorkbenchContextHolder workbenchContextHolder)
    {
        super(workbenchContextHolder);
        addHelpTopicId(this, helpTopicId());
        this.queryHolder = queryHolder;
        initializeLayout();
    }

    private Transformer buildPaneTransformer()
    {
        return new Transformer()
        {
            public JComponent transform(Object queryFormat)
            {
                if (queryFormat == null)
                {
                    return null;
                }

                return queryFormatPanelMap.get(queryFormat.getClass());
            }
        };
    }

    private PropertyValueModel buildQueryFormatHolder()
    {
        return new PropertyAspectAdapter(buildRelationalOptionsHolder(), MWRelationalQuery.QUERY_FORMAT_TYPE_PROPERTY)
        {
            @Override
            protected Object getValueFromSubject()
            {
                return ((MWRelationalQuery)subject).getQueryFormat();
            }
        };
    }

    private ValueModel buildRelationalOptionsHolder()
    {
        return new TransformationValueModel(buildRelationalQueryHolder())
        {
            @Override
            protected Object transformNonNull(Object value)
            {
                return ((MWRelationalQuery)value).getRelationalOptions();
            };
        };
    }

    private ValueModel buildRelationalQueryHolder()
    {
        return new TransformationValueModel(this.queryHolder)
        {
            @Override
            protected Object transformNonNull(Object value)
            {
                return value;
            }
        };
    }

    private String helpTopicId()
    {
        return "descriptor.queries.format";
    }

    protected void initializeLayout()
    {
        GridBagConstraints constraints = new GridBagConstraints();
        initializeMaps();

        SwitcherPanel switcherPane = new SwitcherPanel
        (
            buildQueryFormatHolder(),
            buildPaneTransformer()
        );

        constraints.gridx      = 0;
        constraints.gridy      = 0;
        constraints.gridwidth  = 1;
        constraints.gridheight = 1;
        constraints.weightx    = 1;
        constraints.weighty    = 1;
        constraints.fill       = GridBagConstraints.BOTH;
        constraints.anchor     = GridBagConstraints.PAGE_START;
        constraints.insets     = new Insets(5, 5, 5, 5);

        add(switcherPane, constraints);
    }

    private PropertyValueModel buildAutoGeneratorQueryFormatHolder()
    {
        return new TransformationPropertyValueModel(buildQueryFormatHolder())
        {
            @Override
            protected MWAutoGeneratedQueryFormat transform(Object value)
            {
                return (value instanceof MWAutoGeneratedQueryFormat) ? (MWAutoGeneratedQueryFormat) value : null;
            }
        };
    }

    private void initializeMaps()
    {
        autoGeneratedQueryFormatPanel = new AutoGeneratedQueryFormatPanel
        (
            this.queryHolder,
            buildAutoGeneratorQueryFormatHolder(),
            getWorkbenchContextHolder()
        );

        nonAutoGeneratedQueryFormatPanel = new NonAutoGeneratedQueryFormatPanel
        (
            this.queryHolder,
            buildQueryFormatHolder(),
            getWorkbenchContextHolder()
        );

        queryFormatPanelMap = new Hashtable<Class<? extends MWQueryFormat>, JComponent>();
        queryFormatPanelMap.put(MWAutoGeneratedQueryFormat.class,     autoGeneratedQueryFormatPanel);
        queryFormatPanelMap.put(MWExpressionQueryFormat.class,        nonAutoGeneratedQueryFormatPanel);
        queryFormatPanelMap.put(MWStoredProcedureQueryFormat.class, nonAutoGeneratedQueryFormatPanel);
        queryFormatPanelMap.put(MWSQLQueryFormat.class,               nonAutoGeneratedQueryFormatPanel);
        queryFormatPanelMap.put(MWEJBQLQueryFormat.class,             nonAutoGeneratedQueryFormatPanel);
    }

    private boolean promptToChangeSelectionCriteriaType()
    {
        if (preferences().getBoolean(MappingsPlugin.CHANGE_QUERY_FORMAT_DO_NOT_SHOW_THIS_AGAIN_PREFERENCE, false))
        {
            return true;
        }

        // build dialog panel
        String title = this.resourceRepository().getString("QUERY_QUERY_FORMAT_TITLE");
        String message = this.resourceRepository().getString("QUERY_QUERY_FORMAT_MESSAGE");
        PropertyValueModel dontAskAgainHolder = new SimplePropertyValueModel(new Boolean(false));
        JComponent dontAskAgainPanel =
            SwingComponentFactory.buildDoNotAskAgainPanel(message, dontAskAgainHolder, this.resourceRepository());

        // prompt user for response
        int response =
            JOptionPane.showConfirmDialog(
                this.getWorkbenchContext().getCurrentWindow(),
                dontAskAgainPanel,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

        if (dontAskAgainHolder.getValue().equals(Boolean.TRUE)) {
            if (response == JOptionPane.YES_OPTION) {
                this.preferences().putBoolean(MappingsPlugin.CHANGE_QUERY_FORMAT_DO_NOT_SHOW_THIS_AGAIN_PREFERENCE, true);
            }
            else if (response == JOptionPane.NO_OPTION) {
                this.preferences().putBoolean(MappingsPlugin.CHANGE_QUERY_FORMAT_DO_NOT_SHOW_THIS_AGAIN_PREFERENCE, false);
            }
        }

        return (response == JOptionPane.OK_OPTION);
    }

    private boolean queryFormatCanChange()
    {
        String promptValue = TriStateBoolean.UNDEFINED.toString();
        String value = preferences().get(MappingsPlugin.CHANGE_QUERY_FORMAT_DO_NOT_SHOW_THIS_AGAIN_PREFERENCE, "undefined");
        boolean changeQueryType;

        if (value.equals(promptValue)) {
            changeQueryType = promptToChangeSelectionCriteriaType();
        }
        else {
            changeQueryType = TriStateBoolean.TRUE.toString().equals(value);
            if (!changeQueryType) {
                JOptionPane.showMessageDialog(
                        getRootPane(),
                        resourceRepository().getString("QUERY_FORMAT_CHANGE_DISSALLOWED"),
                        application().getShortProductName(),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return changeQueryType;
    }

    private abstract class AbstractQueryFormatPanel extends AbstractPanel
    {
        protected PropertyValueModel queryHolder;
        protected PropertyValueModel queryFormatHolder;

        protected AbstractQueryFormatPanel(LayoutManager layoutManager,
                                            PropertyValueModel queryHolder,
                                            PropertyValueModel queryFormatHolder,
                                            WorkbenchContextHolder workbenchContextHolder)
        {
            super(layoutManager, workbenchContextHolder);

            this.queryFormatHolder = queryFormatHolder;
            this.queryHolder = queryHolder;
            initializeLayout();
        }

        protected AbstractQueryFormatPanel(PropertyValueModel queryHolder,
                                           PropertyValueModel queryFormatHolder,
                                           WorkbenchContextHolder workbenchContextHolder)
        {
            super(workbenchContextHolder);

            this.queryFormatHolder = queryFormatHolder;
            this.queryHolder = queryHolder;
            initializeLayout();
        }

        protected abstract void initializeLayout();
    }


    private final class AutoGeneratedQueryFormatPanel extends AbstractQueryFormatPanel
    {
        public AutoGeneratedQueryFormatPanel(PropertyValueModel queryHolder,
                                                PropertyValueModel queryFormatHolder,
                                                WorkbenchContextHolder workbenchContextHolder)
        {
            super(new BorderLayout(), queryHolder, queryFormatHolder, workbenchContextHolder);
        }

        @Override
        protected void initializeLayout()
        {
            LabelArea autoGenCommentArea = new LabelArea();
            SwingComponentFactory.setTextFor(autoGenCommentArea, "AUTO_GENERATED_QUERY_COMMENT", resourceRepository());
            autoGenCommentArea.setScrollable(true);
            autoGenCommentArea.setFocusable(true);
            autoGenCommentArea.setRequestFocusEnabled(true);
            add(autoGenCommentArea, BorderLayout.PAGE_START);
        }
    }

    private final class NonAutoGeneratedQueryFormatPanel extends AbstractQueryFormatPanel
    {
        private ExpressionQueryFormatSubPanel expressionPanel;
        private StoredProcedureQueryFormatSubPanel storedProcedurePanel;
        private StringQueryFormatSubPanel stringQueryFormatPanel;
        private Map<Class<? extends MWQueryFormat>, JComponent> subQueryFormatPanelMap;

        public NonAutoGeneratedQueryFormatPanel(PropertyValueModel queryHolder,
                                                 PropertyValueModel queryFormatHolder,
                                                 WorkbenchContextHolder workbenchContextHolder)
        {
            super(queryHolder, queryFormatHolder, workbenchContextHolder);
        }

        private ValueModel buildExpressionQueryFormatHolder()
        {
            return new TransformationPropertyValueModel(this.queryFormatHolder)
            {
                @Override
                protected Object transform(Object value)
                {
                    return (value instanceof MWExpressionQueryFormat) ? (MWExpressionQueryFormat) value : null;
                }
            };
        }

        private ValueModel buildStoredProcedureQueryFormatHolder()
        {
            return new TransformationValueModel(this.queryFormatHolder)
            {
                @Override
                protected Object transform(Object value)
                {
                    return (value instanceof MWStoredProcedureQueryFormat) ? (MWStoredProcedureQueryFormat) value : null;
                }
            };
        }

        private Transformer buildPaneTransformer()
        {
            return new Transformer()
            {
                public JComponent transform(Object queryFormat)
                {
                    if (queryFormat == null)
                    {
                        return null;
                    }

                    return subQueryFormatPanelMap.get(queryFormat.getClass());
                }
            };
        }

        private ValueModel buildQueryComponentEnableBooleanHolder()
        {
            return new TransformationValueModel(this.queryFormatHolder)
            {
                @Override
                protected Object transform(Object value)
                {
                    return (value != null);
                }
            };
        }

        private PropertyValueModel buildQueryFormatTypeHolder()
        {
            return new PropertyAspectAdapter(buildRelationalOptionsHolder(), MWRelationalQuery.QUERY_FORMAT_TYPE_PROPERTY)
            {
                @Override
                protected Object getValueFromSubject()
                {
                    return ((MWRelationalQuery)subject).getQueryFormatType();
                }

                @Override
                protected void setValueOnSubject(Object value)
                {
                    if (QuerySelectionCriteriaPanel.this.queryFormatCanChange())
                    {
                        ((MWRelationalQuery)subject).setQueryFormatType((String)value);
                    }
                }
            };
        }

        private ListCellRenderer buildSelectionCriteraCellRenderer()
        {
            return new SimpleListCellRenderer()
            {
                @Override
                public String buildText(Object value)
                {
                    if (value == MWRelationalQuery.EXPRESSION_FORMAT)
                    {
                        return resourceRepository().getString("EXPRESSION_OPTION");
                    }

                    if (value == MWRelationalQuery.STORED_PROCEDURE_FORMAT)
                    {
                        return resourceRepository().getString("STORED_PROCEDURE_OPTION");
                    }

                    if (value == MWRelationalQuery.SQL_FORMAT)
                    {
                        return resourceRepository().getString("SQL_OPTION");
                    }

                    return resourceRepository().getString("EJBQL_OPTION");
                }
            };
        }

        private ComboBoxModel buildSelectionCriteriaComboModel()
        {
            return new ComboBoxModelAdapter
            (
                selectionCriteriaOptionsModel(),
                buildQueryFormatTypeHolder()
            );
        }

        private ValueModel buildStringQueryFormatHolder()
        {
            return new TransformationValueModel(this.queryFormatHolder)
            {
                @Override
                protected Object transform(Object value)
                {
                    return (value instanceof MWStringQueryFormat) ? (MWStringQueryFormat) value : null;
                }
            };
        }

        @Override
        protected void initializeLayout()
        {
            GridBagConstraints constraints = new GridBagConstraints();
            initializeMaps();

            // Query Format Type widgets
            JComponent queryFormatTypeWidgets = buildLabeledComboBox
            (
                "SELECTION_CRITERIA_TYPE_LABEL",
                buildSelectionCriteriaComboModel(),
                buildSelectionCriteraCellRenderer()
            );

            constraints.gridx      = 0;
            constraints.gridy      = 0;
            constraints.gridwidth  = 1;
            constraints.gridheight = 1;
            constraints.weightx    = 1;
            constraints.weighty    = 0;
            constraints.fill       = GridBagConstraints.HORIZONTAL;
            constraints.anchor     = GridBagConstraints.PAGE_START;
            constraints.insets     = new Insets(0, 0, 0, 0);

            add(queryFormatTypeWidgets, constraints);
            new ComponentEnabler(buildQueryComponentEnableBooleanHolder(), queryFormatTypeWidgets);

            // Pane switcher
            SwitcherPanel switcherPane = new SwitcherPanel
            (
                this.queryFormatHolder,
                buildPaneTransformer()
            );

            constraints.gridx      = 0;
            constraints.gridy      = 1;
            constraints.gridwidth  = 1;
            constraints.gridheight = 1;
            constraints.weightx    = 1;
            constraints.weighty    = 1;
            constraints.fill       = GridBagConstraints.BOTH;
            constraints.anchor     = GridBagConstraints.LINE_START;
            constraints.insets     = new Insets(5, 0, 0, 0);

            add(switcherPane, constraints);
        }

        private void initializeMaps()
        {
            stringQueryFormatPanel = new StringQueryFormatSubPanel
            (
                buildStringQueryFormatHolder(),
                getWorkbenchContextHolder()
            );

            expressionPanel = new ExpressionQueryFormatSubPanel
            (
                buildExpressionQueryFormatHolder(),
                getWorkbenchContextHolder()
            );

            storedProcedurePanel = new StoredProcedureQueryFormatSubPanel
            (
                buildStoredProcedureQueryFormatHolder(),
                getWorkbenchContextHolder()
            );

            subQueryFormatPanelMap = new Hashtable<Class<? extends MWQueryFormat>, JComponent>();
            subQueryFormatPanelMap.put(MWExpressionQueryFormat.class,      expressionPanel);
            subQueryFormatPanelMap.put(MWStoredProcedureQueryFormat.class, storedProcedurePanel);
            subQueryFormatPanelMap.put(MWSQLQueryFormat.class,             stringQueryFormatPanel);
            subQueryFormatPanelMap.put(MWEJBQLQueryFormat.class,           stringQueryFormatPanel);
        }

        private CollectionValueModel selectionCriteriaOptionsModel()
        {
            Collection<String> options = new ArrayList<String>();
            options.add(MWRelationalQuery.EXPRESSION_FORMAT);
            options.add(MWRelationalQuery.STORED_PROCEDURE_FORMAT);
            options.add(MWRelationalQuery.SQL_FORMAT);
            options.add(MWRelationalQuery.EJBQL_FORMAT);
            return new ReadOnlyCollectionValueModel(options);
        }
    }
}
