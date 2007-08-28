/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.transformation;

public class RootWithAnyObject {
    public Object object;

    public boolean equals(Object object) {
        try {
            RootWithAnyObject rootWithAnyObject = (RootWithAnyObject)object;
            if (this.object == rootWithAnyObject.object) {
                return true;
            }
            if ((null == this.object) || (null == rootWithAnyObject.object)) {
                return false;
            }
            return this.object.equals(rootWithAnyObject.object);
        } catch (ClassCastException e) {
            return false;
        }
    }
    
    public String toString() {
        String string = "RootWithAnyObject(";
        string += object.toString();
        string += ")";
        return string;
    }    
}