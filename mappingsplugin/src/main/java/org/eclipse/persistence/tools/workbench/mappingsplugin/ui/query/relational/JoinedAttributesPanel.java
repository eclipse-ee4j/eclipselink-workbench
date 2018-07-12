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

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAbstractRelationalReadQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWJoinedItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;


final class JoinedAttributesPanel extends AbstractAttributeItemsPanel {

    public JoinedAttributesPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
        super(queryHolder, contextHolder);
    }

    @Override
    protected PropertyValueModel buildQueryHolder(PropertyValueModel queryHolder) {
        return new FilteringPropertyValueModel(queryHolder) {
            @Override
            protected boolean accept(Object value) {
                return value instanceof MWAbstractRelationalReadQuery;
            }
        };
    }

    @Override
    protected String helpTopicId() {
        return "query.joinedAtributes";
    }

    @Override
    String listTitleKey() {
        return "JOINED_ATTRIBUTES_LIST";
    }

    @Override
    AddRemovePanel.UpDownOptionAdapter buildAttributesPanelAdapter() {
        return new AddRemovePanel.UpDownOptionAdapter() {
            @Override
            public String optionalButtonKey() {
                return "JOINED_ATTRIBUTES_LIST_EDIT_BUTTON";
            }

            @Override
            public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
                editSelectedAttribute((MWJoinedItem) listSelectionModel.getSelectedValue());
            }

            @Override
            public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
                return listSelectionModel.getSelectedValuesSize() == 1;
            }

            @Override
            public void addNewItem(ObjectListSelectionModel listSelectionModel) {
                addJoinedAttribute();
            }

            @Override
            public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
                Object[] selectedValues = listSelectionModel.getSelectedValues();
                for (int i = 0; i < selectedValues.length; i++) {
                    ((MWAbstractRelationalReadQuery) getQuery()).removeJoinedItem((MWJoinedItem) selectedValues[i]);
                }
            }

            @Override
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                   ((MWAbstractRelationalReadQuery) getQuery()).moveJoinedItemDown((MWJoinedItem) items[i]);
                }
            }

            @Override
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    ((MWAbstractRelationalReadQuery) getQuery()).moveJoinedItemUp((MWJoinedItem) items[i]);
                 }
            }
        };
    }



    @Override
    protected ListValueModel buildAttributesHolder() {
        return new ListAspectAdapter(getQueryHolder(), MWAbstractRelationalReadQuery.JOINED_ITEMS_LIST) {
            @Override
            protected ListIterator getValueFromSubject() {
                return ((MWAbstractRelationalReadQuery) this.subject).joinedItems();
            }
            @Override
            protected int sizeFromSubject() {
                return ((MWAbstractRelationalReadQuery) this.subject).joinedItemsSize();
            }
        };
    }

    @Override
    protected boolean panelEnabled(MWQueryFormat queryFormat) {
        return true;
    }

    private void addJoinedAttribute() {
        editSelectedAttribute(null);
    }

    @Override
    AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item) {
        return
            new AttributeItemDialog(getQuery(), item, getWorkbenchContext()) {
            @Override
                protected String titleKey() {
                    return "JOINED_ATTRIBUTES_DIALOG_TITLE";
                }

            @Override
                protected String editTitleKey() {
                    return "JOINED_ATTRIBUTES_EDIT_DIALOG_TITLE";
                }

            @Override
                protected String helpTopicId() {
                    return "dialog.joinedAttribute";
                }

            @Override
                protected Filter buildTraversableFilter() {
                    return new Filter() {
                    @Override
                        public boolean accept(Object o) {
                            return ((MWQueryable) o).isTraversableForJoinedAttribute();
                        }
                    };
                }

            @Override
                protected Filter buildChooseableFilter() {
                    return new Filter() {
                    @Override
                        public boolean accept(Object o) {
                            return ((MWQueryable) o).isValidForJoinedAttribute();
                        }
                    };
                }

            @Override
                protected int attributeItemsSize() {
                    return ((MWAbstractRelationalReadQuery) getQuery()).joinedItemsSize();
                }

            @Override
                protected int indexOfAttributeItem(MWAttributeItem attributeItem) {
                    return ((MWAbstractRelationalReadQuery) getQuery()).indexOfJoinedItem((MWJoinedItem) attributeItem);
                }

            @Override
                protected void removeAttributeItem(int index) {
                    ((MWAbstractRelationalReadQuery) getQuery()).removeJoinedItem(index);
                }

            @Override
                protected void addAttributeItem(int index, Iterator queryables, Iterator allowsNulls) {
                    ((MWAbstractRelationalReadQuery) getQuery()).addJoinedItem(index, queryables, allowsNulls);
                }
            };
    }

}
