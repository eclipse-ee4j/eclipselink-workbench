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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Iterator;
import org.eclipse.persistence.oxm.XMLDescriptor;

public final class ReferencedAttributeDeclaration
    extends SchemaComponentReference
    implements MWAttributeDeclaration
{
    // **************** Variables *********************************************

    private volatile ExplicitAttributeDeclaration attribute;


    // **************** Constructors ******************************************

    /** For Toplink Use Only */
    protected ReferencedAttributeDeclaration() {
        super();
    }

    ReferencedAttributeDeclaration(AbstractSchemaModel parent, String attributeName, String attributeNamespace) {
        super(parent, attributeName, attributeNamespace);
    }


    // **************** SchemaComponentReference contract *********************

    @Override
    protected MWNamedSchemaComponent getReferencedComponent() {
        return this.attribute;
    }

    @Override
    protected void resolveReference(String attributeNamespace, String attributeName) {
        this.attribute = (ExplicitAttributeDeclaration) this.getSchema().attribute(attributeNamespace, attributeName);
    }


    // **************** MWAttributeDeclaration contract ***********************

    @Override
    public MWSimpleTypeDefinition getType() {
        return this.attribute.getType();
    }

    @Override
    public String getDefaultValue() {
        return this.attribute.getDefaultValue();
    }

    @Override
    public String getFixedValue() {
        return this.attribute.getFixedValue();
    }

    @Override
    public String getUse() {
        return this.attribute.getUse();
    }


    // **************** MWXpathableSchemaComponent contract *******************

    @Override
    public int getMaxOccurs() {
        return this.attribute.getMaxOccurs();
    }

    @Override
    public Iterator baseBuiltInTypes() {
        return this.attribute.baseBuiltInTypes();
    }


    // **************** MWSchemaContextComponent contract *********************

    @Override
    public boolean hasType() {
        return true;
    }

    @Override
    public String contextTypeQname() {
        return this.attribute.contextTypeQname();
    }

    @Override
    public boolean containsText() {
        // should always be false, but consistently asking the attribute seems a good thing
        return this.attribute.containsText();
    }

    @Override
    public boolean containsWildcard() {
        // should always be false, but consistently asking the attribute seems a good thing
        return this.attribute.containsWildcard();
    }

    @Override
    public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
        return this.attribute.compareSchemaOrder(element1, element2);
    }


    // **************** TopLink methods ***************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReferencedAttributeDeclaration.class);
        descriptor.getInheritancePolicy().setParentClass(SchemaComponentReference.class);
        return descriptor;
    }
}
