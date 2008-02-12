/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.oxm.XMLDescriptor;

public class MWNullArgument extends MWArgument {

	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWNullArgument.class);
		descriptor.getInheritancePolicy().setParentClass(MWArgument.class);
	
		return descriptor;
	}
		
	private MWNullArgument() {
		super();
	}

	MWNullArgument(MWBasicExpression expression) {
		super(expression);
	}

	public String getType() {
		return null;
	}

	Expression runtimeExpression(ExpressionBuilder builder) {
		return null;
	}

	public void undoChange(String propertyName, Object oldValue, Object newValue) {

	}
	
	public String displayString() {
		return "";
	}

}