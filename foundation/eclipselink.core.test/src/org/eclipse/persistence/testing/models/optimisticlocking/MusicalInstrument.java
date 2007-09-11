/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.optimisticlocking;

public abstract class MusicalInstrument implements SelfUpdatable {
    public int id;
    public String colour;
    public String make;
    public String lockField;

    /**
     * Instrument constructor comment.
     */
    public MusicalInstrument() {
        super();
        updateLockField();
    }

    public void updateLockField() {
        if (lockField == null) {
            lockField = "";
        }

        lockField = lockField + "#";

    }

    public void verify() {
    }
}