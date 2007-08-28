/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

/**
 * @author Wonseok Kim
 */
public class CKeyEntityAPK {
    public int seq;
    public String firstName;
    public String lastName;


    public CKeyEntityAPK() {
    }

    public CKeyEntityAPK(int seq, String firstName, String lastName) {
        this.seq = seq;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CKeyEntityAPK that = (CKeyEntityAPK) o;

        return seq == that.seq && firstName.equals(that.firstName) && lastName.equals(that.lastName);
    }

    public int hashCode() {
        int result;
        result = seq;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}