/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.schema.model;

public class Restriction implements SimpleDerivation {
    private String baseType;//QName lateR??

    //can have a typeDefParticle (seq choice all if in complexContent) OR simplerestmodel (facets
    private TypeDefParticle typeDefParticle;
    private Choice choice;
    private Sequence sequence;
    private All all;
    private SimpleType simpleType;
    private java.util.ArrayList enumerationFacets;
    private AnyAttribute anyAttribute;

    //private List facets
    private java.util.List attributes;
    private Restrictable owner;

    public Restriction() {
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setTypeDefParticle(TypeDefParticle typeDefParticle) {
        this.typeDefParticle = typeDefParticle;
        if (typeDefParticle instanceof Choice) {
            setChoice((Choice)typeDefParticle);
        } else if (typeDefParticle instanceof Sequence) {
            setSequence((Sequence)typeDefParticle);
        } else {
            setAll((All)typeDefParticle);
        }
    }

    public TypeDefParticle getTypeDefParticle() {
        return typeDefParticle;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
        typeDefParticle = choice;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
        if (sequence != null) {
            this.typeDefParticle = sequence;
        }
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setAll(All all) {
        this.all = all;
        typeDefParticle = all;
    }

    public All getAll() {
        return all;
    }

    public void setSimpleType(SimpleType simpleType) {
        this.simpleType = simpleType;
    }

    public SimpleType getSimpleType() {
        return simpleType;
    }

    public void setAttributes(java.util.List attributes) {
        this.attributes = attributes;
    }

    public java.util.List getAttributes() {
        return attributes;
    }

    public java.util.ArrayList getEnumerationFacets() {
        return enumerationFacets;
    }

    public void setEnumerationFacets(java.util.ArrayList values) {
        enumerationFacets = values;
    }

    public void setOwner(Restrictable owner) {
        this.owner = owner;
    }

    public Restrictable getOwner() {
        return owner;
    }

    public String getOwnerName() {
        if (owner != null) {
            return owner.getOwnerName();
        }
        return null;
    }

    public AnyAttribute getAnyAttribute() {
        return anyAttribute;
    }

    public void setAnyAttribute(AnyAttribute any) {
        anyAttribute = any;
    }
}