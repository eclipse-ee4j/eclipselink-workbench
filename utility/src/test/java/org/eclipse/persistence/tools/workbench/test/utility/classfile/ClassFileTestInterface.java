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
package org.eclipse.persistence.tools.workbench.test.utility.classfile;

import java.util.Iterator;

/**
 *
 */
public interface ClassFileTestInterface extends java.util.Collection, java.lang.Comparable {
    String test = "test";
    int limit = 99;

    void foo();
    int bar(int[][] indexPairs);
    String baz(String[][] names);

    interface InnerInterface1 {
        void innerFoo();
    }

    class NullClassFileTestInterface extends java.util.AbstractCollection implements ClassFileTestInterface {
        @Override
        public int compareTo(Object o) {
            return 0;
        }
        @Override
        public void foo() {
            // do nothing
        }
        @Override
        public String baz(String[][] names) {
            return null;
        }
        @Override
        public Iterator iterator() {
            return null;
        }
        @Override
        public int bar(int[][] indexPairs) {
            return 0;
        }
        @Override
        public int size() {
            return 0;
        }
    }

}
