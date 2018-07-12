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
package org.eclipse.persistence.tools.workbench.framework.resources;

import javax.swing.Icon;
import javax.swing.KeyStroke;


/**
 * Consolidate all the resource repository interfaces into a single interface
 * that can used by the typical UI component.
 */
public interface ResourceRepository
    extends StringRepository, MnemonicRepository, AcceleratorRepository, IconRepository {


    // ********** null implementation **********

    ResourceRepository NULL_INSTANCE =
        new ResourceRepository() {
            @Override
            public boolean hasString(String key) {
                return StringRepository.NULL_INSTANCE.hasString(key);
            }
            @Override
            public String getString(String key, Object argument) {
                return StringRepository.NULL_INSTANCE.getString(key, argument);
            }
            @Override
            public String getString(String key, Object argument1, Object argument2) {
                return StringRepository.NULL_INSTANCE.getString(key, argument1, argument2);
            }
            @Override
            public String getString(String key, Object argument1, Object argument2, Object argument3) {
                return StringRepository.NULL_INSTANCE.getString(key, argument1, argument2, argument3);
            }
            @Override
            public String getString(String key, Object[] arguments) {
                return StringRepository.NULL_INSTANCE.getString(key, arguments);
            }
            @Override
            public String getString(String key) {
                return StringRepository.NULL_INSTANCE.getString(key);
            }
            @Override
            public boolean hasMnemonic(String key) {
                return MnemonicRepository.NULL_INSTANCE.hasMnemonic(key);
            }
            @Override
            public int getMnemonic(String key) {
                return MnemonicRepository.NULL_INSTANCE.getMnemonic(key);
            }
            @Override
            public int getMnemonicIndex(String key) {
                return MnemonicRepository.NULL_INSTANCE.getMnemonicIndex(key);
            }
            @Override
            public boolean hasAccelerator(String key) {
                return AcceleratorRepository.NULL_INSTANCE.hasAccelerator(key);
            }
            @Override
            public KeyStroke getAccelerator(String key) {
                return AcceleratorRepository.NULL_INSTANCE.getAccelerator(key);
            }
            @Override
            public Icon getIcon(String key) {
                return IconRepository.NULL_INSTANCE.getIcon(key);
            }
            @Override
            public boolean hasIcon(String key) {
                return IconRepository.NULL_INSTANCE.hasIcon(key);
            }
            @Override
            public String toString() {
                return "NullResourceRepository";
            }
        };

}
