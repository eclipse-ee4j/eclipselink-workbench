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

import static javax.persistence.GenerationType.TABLE;
import javax.persistence.*;
import java.util.Collection;

/**
 * Composite Key Entity.
 * 
 * @author Wonseok Kim
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "DDL_CKENTB", 
    uniqueConstraints = { 
        @UniqueConstraint(columnNames={"UNQ2, UNQ1"}) //The order of columns shoud not be changed. See CKeyEntityA. 
})
public class CKeyEntityB {
    @EmbeddedId
    private CKeyEntityBPK key;

    @Column(name = "UNQ1", nullable = false)
    private String unq1;

    @Column(name = "UNQ2", nullable = false)
    private String unq2;
    

    @OneToMany(mappedBy="bs")
    private Collection<CKeyEntityA> as;
    
    @ManyToMany(mappedBy="bs")
    private Collection<CKeyEntityC> cs;

    @OneToOne(mappedBy="uniqueB")
    private CKeyEntityA uniqueA;

    public CKeyEntityB() {
    }

    public CKeyEntityB(CKeyEntityBPK key) {
        this.key = key;
    }

    public CKeyEntityBPK getKey() {
        return key;
    }

    public void setKey(CKeyEntityBPK key) {
        this.key = key;
    }

    public String getUnq1() {
        return unq1;
    }

    public void setUnq1(String unq1) {
        this.unq1 = unq1;
    }

    public String getUnq2() {
        return unq2;
    }

    public void setUnq2(String unq2) {
        this.unq2 = unq2;
    }

    public Collection<CKeyEntityA> getAs() {
        return as;
    }

    public void setAs(Collection<CKeyEntityA> as) {
        this.as = as;
    }

    public Collection<CKeyEntityC> getCs() {
        return cs;
    }

    public void setCs(Collection<CKeyEntityC> cs) {
        this.cs = cs;
    }

    public CKeyEntityA getUniqueA() {
        return uniqueA;
    }

    public void setUniqueA(CKeyEntityA uniqueA) {
        this.uniqueA = uniqueA;
    }
}