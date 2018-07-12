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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.io.Serializable;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Facile implementation of the IconBuilder interface.
 */
public class SimpleIconBuilder
    implements IconBuilder, Cloneable, Serializable
{
    private Icon icon;

    private static final long serialVersionUID = 1L;


    /**
     * Construct an icon builder that will simply return the specified icon.
     */
    public SimpleIconBuilder(Icon icon) {
        super();
        this.icon = icon;
    }

    /**
     * @see IconBuilder#buildIcon()
     */
    @Override
    public Icon buildIcon() {
        return this.icon;
    }

    /**
     * @see Object#clone()
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof SimpleIconBuilder) {
            return this.equals((SimpleIconBuilder) o);
        }
        return false;
    }

    public boolean equals(SimpleIconBuilder other) {
        return (this.icon == null) ? other.icon == null : this.icon.equals(other.icon);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (this.icon == null) ? 0 : this.icon.hashCode();
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return StringTools.buildToStringFor(this, this.icon);
    }

}
