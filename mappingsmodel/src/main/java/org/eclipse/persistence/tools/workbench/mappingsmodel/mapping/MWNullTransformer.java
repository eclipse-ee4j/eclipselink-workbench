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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateFieldDescription;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.AggregateRuntimeFieldNameGenerator;

public final class MWNullTransformer
    extends MWTransformer
{
    // **************** Constructors ******************************************

    /** Default constructor - for TopLink use only (sorta) */
    MWNullTransformer() {
        super();
    }

    MWNullTransformer(Parent parent) {
        super(parent);
    }


    // **************** Aggregate Support *************************************

    @Override
    public String fieldNameForRuntime() {
        return "NULL_TRANSFORMER";
    }

    @Override
    public AggregateFieldDescription fullFieldDescription() {
        return new AggregateFieldDescription() {
            @Override
            public String getMessageKey() {
                return "AGGREGATE_FIELD_DESCRIPTION_FOR_NULL_TRANSFORMER";
            }

            @Override
            public Object[] getMessageArguments() {
                return new Object[0];
            }
        };
    }

    /** @see AggregateRuntimeFieldNameGenerator#fieldIsWritten() */
    @Override
    public boolean fieldIsWritten() {
        return true;
    }


    // **************** UI support *********************************************

    @Override
    public String transformerDisplayString() {
        return null;
    }


    // **************** Problems *********************************************

    @Override
    public void addAttributeTransformerProblemsForMapping(List newProblems, MWTransformationMapping mapping) {
        newProblems.add(this.buildProblem(ProblemConstants.MAPPING_ATTRIBUTE_TRANSFORMER_NOT_SPECIFIED));
    }

    @Override
    public void addFieldTransformerProblemsForAssociation(List newProblems, MWFieldTransformerAssociation association) {
        newProblems.add(this.buildProblem(ProblemConstants.MAPPING_FIELD_TRANSFORMER_NOT_SPECIFIED, association.fieldName()));
    }


    // **************** Runtime conversion ************************************

    @Override
    public void setRuntimeAttributeTransformer(AbstractTransformationMapping mapping) {
        // NOP
    }

    @Override
    public void addRuntimeFieldTransformer(AbstractTransformationMapping mapping, DatabaseField runtimeField) {
        // NOP
    }


    // **************** TopLink methods ************************************

    @Override
    public MWTransformer valueForTopLink() {
        return null;
    }

}
