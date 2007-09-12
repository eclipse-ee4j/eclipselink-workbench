/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.oxm.XMLUnmarshalListener;

/**
 * <p><b>Purpose</b>: Implementation of XMLUnmarshalListener used when unmarshalling XML to XMLDocuments
 * <p><b>Responsibilities</b>:<ul>
 * <li> When creating a DataObject we need to call setType and setHelperContext with the appropriate values
 * </ul>
 */
public class SDOCSUnmarshalListener implements XMLUnmarshalListener {
    private boolean isCSUnmarshalListener;
	/** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    protected HelperContext aHelperContext;

    public SDOCSUnmarshalListener(HelperContext aContext, boolean bIsCSUnmarshalListener) {
        aHelperContext = aContext;
        isCSUnmarshalListener = bIsCSUnmarshalListener;
    }

    public SDOCSUnmarshalListener(HelperContext aContext) {
        aHelperContext = aContext;
    }

    public void beforeUnmarshal(Object target, Object parent) {
        try {
            if (target instanceof SDODataObject) {
                String className = target.getClass().getName();
                String interfaceName = className.substring(0, className.length() - 4);

                Class interfaceClass = target.getClass().getClassLoader().loadClass(interfaceName);
                Type type = aHelperContext.getTypeHelper().getType(interfaceClass);

                // perform cleanup operations on objects that were instantiated with getInstance()
                SDODataObject aDataObject = (SDODataObject)target;
                // reset the HelperContext on target DataObject from default static context
                // setting the Type requires a helpercontext so following 2 calls must be in this order
                aDataObject._setHelperContext(aHelperContext);
                aDataObject._setType(type);
            } else if (target instanceof SDOChangeSummary) {
                if (isCSUnmarshalListener) {
                    //TODO:if not root throw exception can't have nested cs or skip it                                
                } else {
                    ((SDOChangeSummary)target).setHelperContext(aHelperContext);
                }
            }
        } catch (ClassNotFoundException e) {
            throw SDOException.classNotFound(e, null, null);
        }
    }

    public void afterUnmarshal(Object target, Object parent) {
    }
}