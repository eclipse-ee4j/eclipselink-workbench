/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

import java.io.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.ClassConstants;

/**
 * <p><b>Purpose</b>: This provides an abstract class for setting and retrieving
 * the attribute value for the mapping from an object.
 * It can be used in advanced situations if the attribute
 * requires advanced conversion of the mapping value, or a real attribute does not exist.
 *
 *    @author James
 *    @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public abstract class AttributeAccessor implements Cloneable, Serializable {

    /** Stores the name of the attribute */
    protected String attributeName;

    /**
     * INTERNAL:
     * Clones itself.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * INTERNAL:
     * Return the attribute name.
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * INTERNAL:
     * Set the attribute name.
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * Return the class type of the attribute.
     */
    public Class getAttributeClass() {
        return ClassConstants.OBJECT;
    }

    /**
     * Allow any initialization to be performed with the descriptor class.
     */
    public void initializeAttributes(Class descriptorClass) throws DescriptorException {
        if (getAttributeName() == null) {
            throw DescriptorException.attributeNameNotSpecified();
        }
    }

    /**
     * Return the attribute value from the object.
     */
    public abstract Object getAttributeValueFromObject(Object object) throws DescriptorException;

    /**
     * Set the attribute value into the object.
     */
    public abstract void setAttributeValueInObject(Object object, Object value) throws DescriptorException;
}