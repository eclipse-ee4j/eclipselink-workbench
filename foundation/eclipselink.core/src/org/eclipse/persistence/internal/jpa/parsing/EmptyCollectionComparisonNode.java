/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;


// TopLink imports
import org.eclipse.persistence.expressions.*;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Used for the EMPTY collection handling
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an IS EMPTY in EJBQL
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since August 2003
 */
public class EmptyCollectionComparisonNode extends SimpleConditionalExpressionNode {
    public boolean notIndicated = false;

    /**
     * EmptyCollectionComparisonNode constructor comment.
     */
    public EmptyCollectionComparisonNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        if (left != null) {
            left.validate(context);
        }
        TypeHelper typeHelper = context.getTypeHelper();
        setType(typeHelper.getBooleanType());
    }

    /**
     * INTERNAL
     * Return a TopLink expression for this node.
     */
    public Expression generateExpression(GenerationContext context) {
        //get the variable name from the DotNode's right attribute
        //...WHERE emp.firstName IS EMPTY
        String emptyAttributeName = ((AttributeNode)getLeft().getRight()).getAttributeName();
        Expression whereClause = getLeft().getLeft().generateExpression(context);
        if (notIndicated) {
            return whereClause.notEmpty(emptyAttributeName);
        } else {
            return whereClause.isEmpty(emptyAttributeName);
        }
    }

    /**
     * INTERNAL
     * Indicate if a NOT was found in the WHERE clause.
     * Examples:
     *        ...WHERE emp.phoneNumbers IS NOT EMPTY
     */
    public void indicateNot() {
        notIndicated = true;
    }

}