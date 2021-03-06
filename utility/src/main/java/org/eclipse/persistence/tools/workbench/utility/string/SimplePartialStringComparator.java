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
package org.eclipse.persistence.tools.workbench.utility.string;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * This implementation of PartialStringComparator will simply compare
 * the specified strings, returning 1 if they are equal, 0 if they are not.
 */
public final class SimplePartialStringComparator
    implements PartialStringComparator
{

    // singleton
    private static SimplePartialStringComparator INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized PartialStringComparator instance() {
        if (INSTANCE == null) {
            INSTANCE = new SimplePartialStringComparator();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private SimplePartialStringComparator() {
        super();
    }

    /**
     * @see PartialStringComparator#compare(String, String)
     */
    @Override
    public double compare(String s1, String s2) {
        return s1.equals(s2) ? 1 : 0;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return ClassTools.shortClassNameForObject(this);
    }

}
