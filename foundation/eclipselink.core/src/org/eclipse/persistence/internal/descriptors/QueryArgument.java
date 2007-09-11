/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.mappings.TypedAssociation;

/**
 * <p><b>Purpose</b>: Used to define the query argument mapping.
 * This is used for the deployment XML mapping.
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class QueryArgument extends TypedAssociation {

    /** The type of the query argument */
    protected Class type;
    protected String typeName;// bug 3256198 types can now be set by name

    /**
     * Default constructor.
     */
    public QueryArgument() {
        super();
    }

    public QueryArgument(String argumentName, Object value, Class type) {
        super(argumentName, value);
        this.type = type;
    }

    public Class getType() {
        return type;
    }

    /**
     * INTERNAL:
     * Return the classname of the type.
     */
    public String getTypeName() {
        return typeName;
    }

    public void setType(Class type) {
        this.type = type;
        if (type != null) {
            this.typeName = type.getName();
        }
    }

    /**
     * INTERNAL:
     * Set the classname of the type.
     * This information will be used to avoid Mapping Workbench classpath dependancies by
     * allowing the type to be set by classname instead of the class itself.
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}