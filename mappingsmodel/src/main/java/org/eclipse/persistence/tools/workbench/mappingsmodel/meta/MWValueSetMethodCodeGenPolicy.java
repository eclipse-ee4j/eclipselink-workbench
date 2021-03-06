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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

public final class MWValueSetMethodCodeGenPolicy
    extends MWAccessorCodeGenPolicy
{
    MWValueSetMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassCodeGenPolicy classCodeGenPolicy)
    {
        super(method, attribute, classCodeGenPolicy);
    }

    @Override
    void insertArguments(NonreflectiveMethodDefinition methodDef)
    {
        methodDef.addArgument(getAccessedAttribute().getValueType().getName(), getAccessedAttribute().getName());
    }

    /**
     * Return "this.<attribute name>.setValue(<parameter name>);"
     */
    @Override
    void insertMethodBody(NonreflectiveMethodDefinition methodDef)
    {
        methodDef.addLine("this." + getAccessedAttribute().getName()
                          + ".setValue("  + methodDef.argumentNames().next() + ");" );
    }
}
