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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingNode;


final class MapAsTypeConversionAction extends MapAsRelationalDirectMapping {

    public MapAsTypeConversionAction(WorkbenchContext context) {
        super(context);
    }

    @Override
    protected void initialize() {
        super.initialize();
        initializeIcon("mapping.typeConversion");
        initializeText("MAP_AS_TYPE_CONVERSION_ACTION");
        initializeMnemonic("MAP_AS_TYPE_CONVERSION_ACTION");
        initializeToolTipText("MAP_AS_TYPE_CONVERSION_ACTION.toolTipText");
    }

    @Override
    protected MappingNode morphNode(MappingNode selectedNode) {
        MappingNode mappingNode = super.morphNode(selectedNode);
        ((MWDirectMapping) mappingNode.getMapping()).setTypeConversionConverter();
        return mappingNode;
    }

    @Override
    protected String converterType() {
        return MWConverter.TYPE_CONVERSION_CONVERTER;
    }

}
