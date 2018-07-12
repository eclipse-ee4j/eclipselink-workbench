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

import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.ListIterator;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel.UpDownOptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBatchReadItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWRelationalReadAllQuery;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


final class BatchReadAttributesPanel extends AbstractAttributeItemsPanel {

    public BatchReadAttributesPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
        super(queryHolder, contextHolder);
    }

    @Override
    protected PropertyValueModel buildQueryHolder(PropertyValueModel queryHolder) {
        return new FilteringPropertyValueModel(queryHolder) {
            @Override
            protected boolean accept(Object value) {
                return value instanceof MWRelationalReadAllQuery;
            }
        };
    }

    @Override
    protected String helpTopicId() {
        return "query.report.batchReadAttributes";
    }

    @Override
    String listTitleKey() {
        return "BATCH_READ_ATTRIBUTES_LIST";
    }

    @Override
    UpDownOptionAdapter buildAttributesPanelAdapter() {
        return new AddRemoveListPanel.UpDownOptionAdapter() {
            @Override
            public String optionalButtonKey() {
                return "BATCH_READ_ATTRIBUTES_LIST_EDIT_BUTTON";
            }

            @Override
            public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
                editSelectedAttribute((MWAttributeItem) listSelectionModel.getSelectedValue());
            }

            @Override
            public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
                return listSelectionModel.getSelectedValuesSize() == 1;
            }

            @Override
            public void addNewItem(ObjectListSelectionModel listSelectionModel) {
                addBatchReadAttribute();
            }

            @Override
            public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
                Object[] selectedValues = listSelectionModel.getSelectedValues();
                for (int i = 0; i < selectedValues.length; i++) {
                    ((MWRelationalReadAllQuery) getQuery()).removeBatchReadItem((MWBatchReadItem) selectedValues[i]);
                }
            }

            @Override
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                   ((MWRelationalReadAllQuery) getQuery()).moveBatchReadItemDown((MWBatchReadItem) items[i]);
                }
            }

            @Override
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    ((MWRelationalReadAllQuery) getQuery()).moveBatchReadItemUp((MWBatchReadItem) items[i]);
                 }
            }
        };
    }

    @Override
    protected ListValueModel buildAttributesHolder() {
        return new ListAspectAdapter(getQueryHolder(), MWRelationalReadAllQuery.BATCH_READ_ITEMS_LIST) {
            @Override
            protected ListIterator getValueFromSubject() {
                return ((MWRelationalReadAllQuery) this.subject).batchReadItems();
            }
            @Override
            protected int sizeFromSubject() {
                return ((MWRelationalReadAllQuery) this.subject).batchReadItemsSize();
            }
        };
    }

    @Override
    protected boolean panelEnabled(MWQueryFormat queryFormat) {
        return queryFormat.batchReadAttributesAllowed();
    }

    private void addBatchReadAttribute() {
        editSelectedAttribute(null);
    }

    @Override
    AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item) {
        return
            new AttributeItemDialog(getQuery(), item, getWorkbenchContext()) {

            @Override
            protected String titleKey() {
                return "BATCH_READ_ATTRIBUTE_DIALOG_TITLE";
            }

            @Override
            protected String editTitleKey() {
                return "BATCH_READ_ATTRIBUTE_EDIT_DIALOG_TITLE";
            }

            @Override
            protected String helpTopicId() {
                return "dialog.batchReadAttribute";
            }

            @Override
            protected QueryableTree buildQueryableTree() {
                QueryableTree tree = super.buildQueryableTree();
                tree.setCellRenderer(buildQueryableTreeRenderer(tree));
                tree.setCellEditor(null);
                return tree;
            }

            private TreeCellRenderer buildQueryableTreeRenderer(final QueryableTree tree) {
                return new SimpleTreeCellRenderer() {
                    @Override
                    public Color getBackgroundSelectionColor() {
                        if (!tree.hasFocus() && !tree.isEditing()) {
                            return UIManager.getColor("Panel.background");
                        }
                        return super.getBackgroundSelectionColor();
                    }
                    @Override
                    public Color getBorderSelectionColor() {
                        if (!tree.hasFocus() && !tree.isEditing()) {
                            return UIManager.getColor("Panel.background");
                        }
                        return super.getBorderSelectionColor();
                    }
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension size = super.getPreferredSize();
                        size.height += 2;
                        return size;
                    }
                    @Override
                    protected String buildText(Object value) {
                        if (MWTableDescriptor.class.isAssignableFrom(((DefaultMutableTreeNode)value).getUserObject().getClass())) {
                            return "";
                        }
                        return ((QueryableTreeNode) value).getQueryable().getName();
                    }
                    @Override
                    protected Icon buildIcon(Object value) {
                        if (MWTableDescriptor.class.isAssignableFrom(((DefaultMutableTreeNode)value).getUserObject().getClass())) {
                            return null;
                        }
                        return resourceRepository().getIcon(((QueryableTreeNode) value).getQueryable().iconKey());
                    }
                };
            }

            @Override
            protected int attributeItemsSize() {
                return ((MWRelationalReadAllQuery) getQuery()).batchReadItemsSize();
            }

            @Override
            protected int indexOfAttributeItem(MWAttributeItem attributeItem) {
                return ((MWRelationalReadAllQuery) getQuery()).indexOfBatchReadItem((MWBatchReadItem) attributeItem);
            }

            @Override
            protected void removeAttributeItem(int index) {
                ((MWRelationalReadAllQuery) getQuery()).removeBatchReadItem(index);
            }

            @Override
            protected void addAttributeItem(int index, Iterator queryables, Iterator allowsNulls) {
                ((MWRelationalReadAllQuery) getQuery()).addBatchReadItem(index, queryables, allowsNulls);
            }


            @Override
            protected Filter buildTraversableFilter() {
                return new Filter() {
                    @Override
                    public boolean accept(Object o) {
                        return ((MWQueryable) o).isTraversableForBatchReadAttribute();
                    }
                };
            }

            @Override
            protected Filter buildChooseableFilter() {
                return new Filter() {
                    @Override
                    public boolean accept(Object o) {
                        return ((MWQueryable) o).isValidForBatchReadAttribute();
                    }
                };
            }
        };
    }
}
