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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.TABLE;

/**
 * @author Wonseok Kim
 */
@Embeddable
public class CKeyEntityCPK {
    @GeneratedValue(strategy = TABLE, generator = "CKEYENTITY_TABLE_GENERATOR")
    @Column(name = "SEQ")
    public int seq;

    @Column(name = "ROLE")
    public String role;


    public CKeyEntityCPK() {
    }

    public CKeyEntityCPK(String role) {
        this.role = role;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CKeyEntityCPK that = (CKeyEntityCPK) o;

        return seq == that.seq && role.equals(that.role);
    }

    public int hashCode() {
        int result;
        result = seq;
        result = 31 * result + role.hashCode();
        return result;
    }
}