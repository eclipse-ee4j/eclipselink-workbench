/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.relationships.manyToMany;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.TableGenerator;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name="CMP3_ENTITYC")
public class EntityC
{
    private int id;
    private String name;
    private Collection<EntityD> ds = new HashSet<EntityD>();

    public EntityC() {
    }

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator="ENTITYC_TABLE_GENERATOR")
    @TableGenerator(
        name="ENTITYC_TABLE_GENERATOR",
        table="CMP3_ENTITYC_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ENTITYC_SEQ"
    )
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(cascade={CascadeType.ALL})
    @JoinTable(
            name="CMP3_UNIDIR_ENTITYC_ENTITYD",
            joinColumns=
            @JoinColumn(name="ENTITYC_ID", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="ENTITYD_ID", referencedColumnName="ID")
    )
    public Collection<EntityD> getDs() {
        return ds;
    }
    public void setDs(Collection<EntityD> ds) {
        this.ds = ds;
    }
}