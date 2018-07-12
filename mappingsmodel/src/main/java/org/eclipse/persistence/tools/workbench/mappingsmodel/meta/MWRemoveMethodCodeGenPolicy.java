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

public final class MWRemoveMethodCodeGenPolicy
    extends MWContainerAccessorCodeGenPolicy
{
    private boolean isMappingPrivateOwned;

    MWRemoveMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassCodeGenPolicy classCodeGenPolicy)
    {
        super(method, attribute, classCodeGenPolicy);
    }

    MWRemoveMethodCodeGenPolicy(MWMethod method, MWClassAttribute attribute, MWClassAttribute backPointerAttribute, boolean isMappingPrivateOwned, MWClassCodeGenPolicy classCodeGenPolicy)
    {
        super(method, attribute, backPointerAttribute, classCodeGenPolicy);
        this.isMappingPrivateOwned = isMappingPrivateOwned;
    }

    @Override
    void insertArguments(NonreflectiveMethodDefinition methodDef)
    {
        if (getMethod().methodParametersSize() != 1)
            super.insertArguments(methodDef);

        if (getAccessedAttribute().canHaveMapKeyAndValueTypes())
            methodDef.addArgument(getMethod().getMethodParameter(0).declaration(), "key");
        else
            addArgument(methodDef, getMethod().getMethodParameter(0));
    }

    /**
     * Return
     *     "{@code this.<attribute name>.remove(<argument name>);}"
     *         -or-
     *     "{@code <value get method>.remove(<argument name>);}"
     *
     * if back pointer, add
     *     "{@code <argument name>.<set method name>(null);}"
     */
    @Override
    protected void insertCollectionMethodBody(NonreflectiveMethodDefinition methodDef)
    {
        if (methodDef.argumentNamesSize() != 1)
            super.insertMethodBody(methodDef);

        String argumentName = methodDef.getArgumentName(0);
        methodDef.addLine(attributeValueCode()
                          + ".remove("  + argumentName
                          + ");" );

        if (getBackPointerSetMethod() != null && !isMappingPrivateOwned)
            methodDef.addLine(argumentName + "."
                              + getBackPointerSetMethod().getName() + "(null);");
    }

    /**
     * Return
     *     "{@code this.<attribute name>.remove(<argument name>);}"
     *         -or-
     *     "{@code <value get method>.remove(<argument name>);}"
     *
     * *or* if back pointer, return
     *     "{@code ((<attribute type>) <attribute name>.remove(<argument name>)).<set method name>(null);}"
     *         -or-
     *     "{@code ((<attribute type>) <value get method>.remove(<argument name>)).<set method name>(null);}"
     */
    @Override
    protected void insertMapMethodBody(NonreflectiveMethodDefinition methodDef)
    {
        if (methodDef.argumentNamesSize() != 1)
            super.insertMethodBody(methodDef);

        String argumentName = methodDef.getArgumentName(0);

        if (getBackPointerSetMethod() == null) {
            methodDef.addLine(attributeValueCode()
                              + ".remove(" + argumentName
                              + ");" );
        } else if (!isMappingPrivateOwned) {
            methodDef.addLine("(("  + getAccessedAttribute().getType().getName()
                              + ") "  + attributeValueCode()
                              + ".remove(" + argumentName
                              + "))."  + getBackPointerSetMethod().getName()
                              + "(null);" );
        } else {
            methodDef.addLine("(("  + getAccessedAttribute().getType().getName()
                      + ") "  + attributeValueCode()
                      + ".remove(" + argumentName
                      + "))." );
        }
    }
}
