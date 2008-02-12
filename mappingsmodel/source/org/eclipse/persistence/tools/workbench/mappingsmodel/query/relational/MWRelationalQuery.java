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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;


public interface MWRelationalQuery extends MWNode {

	MWRelationalSpecificQueryOptions getRelationalOptions();

	
	MWQueryFormat getQueryFormat();
	String getQueryFormatType();
	void setQueryFormatType(String type);
		String QUERY_FORMAT_TYPE_PROPERTY = "queryFormatType";
		String EXPRESSION_FORMAT = "expressionFormat";
		String SQL_FORMAT = "sqlFormat";
		String EJBQL_FORMAT = "ejbqlFormat";
		String AUTO_GENERATED_FORMAT = "autoGenerateFormat";

	
	TriStateBoolean isCacheStatement();	
	void setCacheStatement(TriStateBoolean cacheStatement);
		String CACHE_STATEMENT_PROPERTY = "cacheStatement";

		
	TriStateBoolean isBindAllParameters();
	void setBindAllParameters(TriStateBoolean bindAllParameters);
		String BIND_ALL_PARAMETERS_PROPERTY = "bindAllParameters";


	boolean isPrepare();	
	void setPrepare(boolean prepare);
		String PREPARE_PROPERTY = "prepare";

	void notifyExpressionsToRecalculateQueryables();

    /**
     * Certain query options do not apply when the user chooses
     * to define custom sql. Inform the query so that the applicable
     * options can be removed.
     */
    void formatSetToSql();
    
    /**
     * Certain query options do not apply when the user chooses
     * to use EJBQL. Inform the query so that the applicable
     * options can be removed.
     */
    void formatSetToEjbql();
    
}