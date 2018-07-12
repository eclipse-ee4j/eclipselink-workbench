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

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;

public final class ReferencedElementDeclaration
    extends SchemaComponentReference
    implements MWElementDeclaration
{
    // **************** Variables *********************************************

    private volatile ExplicitElementDeclaration element;

    /** The minimum number of times the element occurs in a document */
    private volatile int minOccurs;

    /** The maximum number of times the element occurs in a document */
    private volatile int maxOccurs;

    // **************** Constructors ******************************************

    /** Toplink Use Only */
    private ReferencedElementDeclaration() {
        super();
    }

    ReferencedElementDeclaration(AbstractSchemaModel parent, String elementName, String elementNamespace) {
        super(parent, elementName, elementNamespace);
    }


    // **************** SchemaComponentReference contract *********************

    @Override
    protected MWNamedSchemaComponent getReferencedComponent() {
        return this.element;
    }

    @Override
    protected void resolveReference(String elementNamespace, String elementName) {
        this.element = (ExplicitElementDeclaration) this.getSchema().element(elementNamespace, elementName);
    }


    // **************** MWElementDeclaration contract *************************

    @Override
    public MWSchemaTypeDefinition getType() {
        return this.element.getType();
    }

    @Override
    public MWElementDeclaration getSubstitutionGroup() {
        return this.element.getSubstitutionGroup();
    }

    @Override
    public boolean isAbstract() {
        return this.element.isAbstract();
    }

    @Override
    public String getDefaultValue() {
        return this.element.getDefaultValue();
    }

    @Override
    public String getFixedValue() {
        return this.element.getFixedValue();
    }

    @Override
    public boolean isNillable() {
        return this.element.isNillable();
    }


    // **************** MWParticle contract ***********************************

    @Override
    public int getMinOccurs() {
        return this.minOccurs;
    }

    @Override
    public int getMaxOccurs() {
        return this.maxOccurs;
    }

    @Override
    public boolean isDescriptorContextComponent() {
        return false;
    }

    @Override
    protected void reloadInternal(XSObject schemaObject) {
        super.reloadInternal(schemaObject);
        if (schemaObject instanceof XSParticleDecl) {
            this.minOccurs = ((XSParticleDecl)schemaObject).getMinOccurs();
            this.maxOccurs = ((XSParticleDecl)schemaObject).getMaxOccurs();
            if (((XSParticleDecl)schemaObject).getMaxOccursUnbounded()) {
                this.maxOccurs = MWXmlSchema.INFINITY;
            }
        }
    }

    @Override
    public boolean isEquivalentTo(XSParticleDecl xsParticle) {
        return this.element.isEquivalentTo(xsParticle);
    }


    // **************** MWXpathableSchemaComponent contract *******************

    @Override
    public Iterator baseBuiltInTypes() {
        return this.element.baseBuiltInTypes();
    }


    // **************** MWSchemaContextComponent contract *********************

    @Override
    public boolean hasType() {
        return true;
    }

    @Override
    public String contextTypeQname() {
        return this.element.contextTypeQname();
    }

    @Override
    public boolean containsText() {
        return this.element.containsText();
    }

    @Override
    public boolean containsWildcard() {
        return this.element.containsWildcard();
    }

    @Override
    public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
        return this.element.compareSchemaOrder(element1, element2);
    }


    // **************** MWSchemaModel contract ********************************

    @Override
    public MWNamedSchemaComponent nestedNamedComponent(QName qName) {
        return this.element.nestedNamedComponent(qName);
    }

    @Override
    public int totalElementCount() {
        return this.element.totalElementCount();
    }


    // **************** Static methods ****************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReferencedElementDeclaration.class);
        descriptor.getInheritancePolicy().setParentClass(SchemaComponentReference.class);

        ((XMLDirectMapping) descriptor.addDirectMapping("minOccurs", "min-occurs/text()")).setNullValue(new Integer(1));
        ((XMLDirectMapping) descriptor.addDirectMapping("maxOccurs", "max-occurs/text()")).setNullValue(new Integer(1));

        return descriptor;
    }
}
