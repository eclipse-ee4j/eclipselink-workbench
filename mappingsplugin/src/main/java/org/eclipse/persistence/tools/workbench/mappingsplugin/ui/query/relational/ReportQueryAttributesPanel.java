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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.event.MouseEvent;
import java.util.ListIterator;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveTablePanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.DoubleClickMouseListener;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;


final class ReportQueryAttributesPanel
    extends AbstractAttributeItemsPanel
{

    public ReportQueryAttributesPanel(PropertyValueModel queryHolder,
                                      WorkbenchContextHolder contextHolder) {
        super(queryHolder, contextHolder);
    }

    @Override
    protected ListValueModel buildAttributesHolder() {
        return new ListAspectAdapter(getQueryHolder(), MWReportQuery.ATTRIBUTE_ITEMS_LIST) {
            @Override
            protected ListIterator getValueFromSubject() {
                return ((MWReportQuery) this.subject).attributeItems();
            }
            @Override
            protected int sizeFromSubject() {
                return ((MWReportQuery) this.subject).attributeItemsSize();
            }
        };
    }


    @Override
    protected AddRemovePanel.UpDownOptionAdapter buildAttributesPanelAdapter() {
        return new AddRemovePanel.UpDownOptionAdapter() {

            @Override
            public void addNewItem(ObjectListSelectionModel listSelectionModel) {
                addAttribute();
            }

            @Override
            public String optionalButtonKey() {
                return "REPORT_QUERY_ATTRIBUTES_LIST_EDIT_BUTTON";
            }

            @Override
            public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
                editSelectedAttribute((MWReportAttributeItem) listSelectionModel.getSelectedValue());
            }

            @Override
            public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
                return listSelectionModel.getSelectedValuesSize() == 1;
            }

            @Override
            public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
                Object[] selectedValues = listSelectionModel.getSelectedValues();
                for (int i = 0; i < selectedValues.length; i++) {
                    ((MWReportQuery) getQuery()).removeAttributeItem((MWReportAttributeItem) selectedValues[i]);
                }
            }

            @Override
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                   ((MWReportQuery) getQuery()).moveAttributeItemDown((MWReportAttributeItem) items[i]);
                }
            }

            @Override
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    ((MWReportQuery) getQuery()).moveAttributeItemUp((MWReportAttributeItem) items[i]);
                 }
            }
        };
    }

    private void addAttribute() {
        editSelectedAttribute(null);
    }

    @Override
    AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item) {
        return new ReportQueryAttributeDialog((MWReportQuery) getQuery(), (MWReportAttributeItem) item, getWorkbenchContext());
    }

    private ComboBoxModel buildFunctionComboBoxModel() {
        return new DefaultComboBoxModel(MWReportAttributeItem.FUNCTIONS);
    }

    private ComboBoxTableCellRenderer buildFunctionComboBoxRenderer() {
        return new ComboBoxTableCellRenderer(this.buildFunctionComboBoxModel());
    }

    @Override
    protected PropertyValueModel buildQueryHolder(PropertyValueModel queryHolder) {
        return new FilteringPropertyValueModel(queryHolder) {
            @Override
            protected boolean accept(Object value) {
                return value instanceof MWReportQuery;
            }
        };
    }

    @Override
    protected String helpTopicId() {
        return "query.report.attributes";
    }

    @Override
    String listTitleKey() {
         return "REPORT_QUERY_ATTRIBUTES_LIST";
    }

    @Override
    protected boolean panelEnabled(MWQueryFormat queryFormat) {
        return queryFormat.reportAttributesAllowed();
    }

    @Override
    protected AddRemovePanel buildAddRemovePanel() {
        final AddRemoveTablePanel tablePanel =  new AddRemoveTablePanel(
                getApplicationContext(),
                buildAttributesPanelAdapter(),
                buildAttributesHolder(),
                new AttributeItemsColumnAdapter(resourceRepository()),
                AddRemovePanel.RIGHT);

        tablePanel.setBorder(buildTitledBorder(listTitleKey()));
        SwingComponentFactory.addDoubleClickMouseListener(tablePanel.getComponent(),
                new DoubleClickMouseListener() {
            @Override
                    public void mouseDoubleClicked(MouseEvent e) {
                        editSelectedAttribute((MWAttributeItem) tablePanel.getSelectionModel().getSelectedValue());
                    }
                });
        addHelpTopicId(tablePanel, helpTopicId());

        updateTableColumns((JTable) tablePanel.getComponent());
        return tablePanel;
    }
    private void updateTableColumns(JTable table) {
        int rowHeight = 0;

        // function column (combo-box)
        TableColumn column = table.getColumnModel().getColumn(AttributeItemsColumnAdapter.FUNCTION_COLUMN);
        ComboBoxTableCellRenderer functionRenderer = this.buildFunctionComboBoxRenderer();
        column.setCellRenderer(this.buildFunctionComboBoxRenderer());
        column.setCellEditor(new TableCellEditorAdapter(this.buildFunctionComboBoxRenderer()));
        rowHeight = Math.max(rowHeight, functionRenderer.getPreferredHeight());

        column = table.getColumnModel().getColumn(AttributeItemsColumnAdapter.ATTRIBUTE_COLUMN);
        column.setCellRenderer(new SimpleTableCellRenderer() {
            @Override
            protected String buildText(Object value) {
                if (value != null) {
                    return ((MWQueryableArgument) value).displayString();
                }
                return "";
            }
        });

        table.setRowHeight(rowHeight);
    }

    // ********** classes **********

    private static class AttributeItemsColumnAdapter implements ColumnAdapter {

        private ResourceRepository resourceRepository;
        public static final int ITEM_NAME_COLUMN = 0;
        public static final int ATTRIBUTE_COLUMN = 1;
        public static final int FUNCTION_COLUMN = 2;

        public static final int COLUMN_COUNT = 3;

        private static final String[] EMPTY_STRING_ARRAY = new String[0];

        private static final String[] COLUMN_NAME_KEYS = new String[] {
            "ITEM_NAME_COLUMN_HEADER",
            "ATTRIBUTE_COLUMN_HEADER",
            "FUNCTION_COLUMN_HEADER",
        };

        private AttributeItemsColumnAdapter(ResourceRepository repository) {
            super();
            this.resourceRepository = repository;
        }

        private PropertyValueModel buildAttributeAdapter(MWReportAttributeItem item) {
            // TODO we need some change notifications from MWQueryableArgument and MWQueryableArgumentElement
            return new PropertyAspectAdapter(EMPTY_STRING_ARRAY, item) {    // the queryableArgument never changes
                @Override
                protected Object getValueFromSubject() {
                    return ((MWReportAttributeItem) this.subject).getQueryableArgument();
                }
            };
        }

        private PropertyValueModel buildFunctionAdapter(MWReportAttributeItem item) {
            return new PropertyAspectAdapter(MWReportAttributeItem.FUNCTION_PROPERTY, item) {
                @Override
                protected Object getValueFromSubject() {
                    return ((MWReportAttributeItem) this.subject).getFunction();
                }
                @Override
                protected void setValueOnSubject(Object value) {
                    ((MWReportAttributeItem) this.subject).setFunction((String) value);
                }
            };
        }

        private PropertyValueModel buildItemNameAdapter(MWReportAttributeItem item) {
            return new PropertyAspectAdapter(MWReportAttributeItem.NAME_PROPERTY, item) {
                @Override
                protected Object getValueFromSubject() {
                    return ((MWReportAttributeItem) this.subject).getName();
                }
                @Override
                protected void setValueOnSubject(Object value) {
                    ((MWReportAttributeItem) this.subject).setName((String) value);
                }
            };
        }

        @Override
        public PropertyValueModel[] cellModels(Object subject) {
            MWReportAttributeItem attributeItem = (MWReportAttributeItem) subject;
            PropertyValueModel[] result = new PropertyValueModel[COLUMN_COUNT];

            result[ITEM_NAME_COLUMN]            = this.buildItemNameAdapter(attributeItem);
            result[ATTRIBUTE_COLUMN]            = this.buildAttributeAdapter(attributeItem);
            result[FUNCTION_COLUMN]            = this.buildFunctionAdapter(attributeItem);

            return result;
        }

        @Override
        public Class getColumnClass(int index) {
            switch (index) {
                case ITEM_NAME_COLUMN:            return Object.class;
                case ATTRIBUTE_COLUMN:            return Object.class;
                case FUNCTION_COLUMN:            return Object.class;
                default:                     return Object.class;
            }
        }

        @Override
        public int getColumnCount() {
            return COLUMN_COUNT;
        }

        @Override
        public String getColumnName(int index) {
            return this.resourceRepository.getString(COLUMN_NAME_KEYS[index]);
        }

        @Override
        public boolean isColumnEditable(int index) {
            return index == FUNCTION_COLUMN;
        }
    }
}
