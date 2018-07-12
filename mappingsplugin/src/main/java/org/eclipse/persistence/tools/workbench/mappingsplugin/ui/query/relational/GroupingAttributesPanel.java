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
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemovePanel.UpDownOptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWAttributeItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWGroupingItem;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryFormat;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWReportQuery;
import org.eclipse.persistence.tools.workbench.uitools.app.ListAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;


final class GroupingAttributesPanel extends AbstractAttributeItemsPanel {

    GroupingAttributesPanel(PropertyValueModel queryHolder, WorkbenchContextHolder contextHolder) {
        super(queryHolder, contextHolder);
    }

    @Override
    String listTitleKey() {
        return "REPORT_QUERY_GROUPING_ATTRIBUTES_LIST";
    }

    @Override
    protected String helpTopicId() {
        return "query.report.groupings";
    }

    @Override
    UpDownOptionAdapter buildAttributesPanelAdapter() {
        return new UpDownOptionAdapter() {
            @Override
            public String optionalButtonKey() {
                return "REPORT_QUERY_GROUPING_ATTRIBUTES_LIST_EDIT_BUTTON";
            }

            @Override
            public void optionOnSelection(ObjectListSelectionModel listSelectionModel) {
                editSelectedAttribute((MWGroupingItem) listSelectionModel.getSelectedValue());
            }

            @Override
            public boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel) {
                return listSelectionModel.getSelectedValuesSize() == 1;
            }

            @Override
            public void addNewItem(ObjectListSelectionModel listSelectionModel) {
                addGroupingAttribute();
            }

            @Override
            public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
                Object[] selectedValues = listSelectionModel.getSelectedValues();
                for (int i = 0; i < selectedValues.length; i++) {
                    ((MWReportQuery) getQuery()).removeGroupingItem((MWGroupingItem) selectedValues[i]);
                }
            }

            @Override
            public void moveItemsDown(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                   ((MWReportQuery) getQuery()).moveGroupingItemDown((MWGroupingItem) items[i]);
                }
            }

            @Override
            public void moveItemsUp(Object[] items) {
                for (int i = 0; i < items.length; i++) {
                    ((MWReportQuery) getQuery()).moveGroupingItemUp((MWGroupingItem) items[i]);
                 }
            }
        };
    }


    @Override
    protected ListValueModel buildAttributesHolder() {
        return new ListAspectAdapter(getQueryHolder(), MWReportQuery.GROUPING_ITEMS_LIST) {
            @Override
            protected ListIterator getValueFromSubject() {
                return ((MWReportQuery) this.subject).groupingItems();
            }
            @Override
            protected int sizeFromSubject() {
                return ((MWReportQuery) this.subject).groupingItemsSize();
            }
        };
    }

    @Override
    protected boolean panelEnabled(MWQueryFormat queryFormat) {
        return queryFormat.groupingAtributesAllowed();
    }

    private void addGroupingAttribute() {
        editSelectedAttribute(null);
    }

    @Override
    AttributeItemDialog buildAttributeItemDialog(MWAttributeItem item) {
        AttributeItemDialog dialog = new AttributeItemDialog(getQuery(), item, getWorkbenchContext()) {
            @Override
            protected String titleKey() {
                return "GROUPING_ATTRIBUTES_DIALOG_TITLE";
            }

            @Override
            protected String editTitleKey() {
                return "GROUPING_ATTRIBUTES_EDIT_DIALOG_TITLE";
            }

            @Override
            protected String helpTopicId() {
                return "dialog.groupingAttribute";
            }

            @Override
            protected int attributeItemsSize() {
                return ((MWReportQuery) getQuery()).groupingItemsSize();
            }

            @Override
            protected int indexOfAttributeItem(MWAttributeItem attributeItem) {
                return ((MWReportQuery) getQuery()).indexOfGroupingItem((MWGroupingItem) attributeItem);
            }

            @Override
            protected void removeAttributeItem(int index) {
                ((MWReportQuery) getQuery()).removeGroupingItem(index);
            }

            @Override
            protected void addAttributeItem(int index, Iterator queryables, Iterator allowsNulls) {
                ((MWReportQuery) getQuery()).addGroupingItem(index, queryables, allowsNulls);
            }
        };
        return dialog;
    }


}
