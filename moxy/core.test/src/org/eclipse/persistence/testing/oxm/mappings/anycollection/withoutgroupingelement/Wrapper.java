/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement;

import java.util.ArrayList;

public class Wrapper {
    private java.util.List roots;

    public Wrapper() {
        roots = new ArrayList();
    }

    public void setRoots(java.util.List roots) {
        this.roots = roots;
    }

    public java.util.List getRoots() {
        return roots;
    }

    public boolean equals(Object object) {
        if (object instanceof Wrapper) {
            if (roots.size() == ((Wrapper)object).getRoots().size()) {
                for (int i = 0; i < roots.size(); i++) {
                    Object a = roots.get(i);
                    Object b = ((Wrapper)object).getRoots().get(i);
                    if (!a.equals(b)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }
}