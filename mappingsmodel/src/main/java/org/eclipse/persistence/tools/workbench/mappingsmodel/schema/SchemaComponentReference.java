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

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.persistence.oxm.XMLDescriptor;

public abstract class SchemaComponentReference
    extends AbstractNamedSchemaComponent
{
    // **************** Static methods ****************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SchemaComponentReference.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractNamedSchemaComponent.class);

        return descriptor;
    }


    // **************** Constructors ******************************************

    /** For Toplink Use Only */
    protected SchemaComponentReference() {
        super();
    }

    SchemaComponentReference(AbstractSchemaModel parent, String name, String namespace) {
        super(parent, name, namespace);
    }


    // **************** SchemaComponentReference contract *********************

    protected abstract MWNamedSchemaComponent getReferencedComponent();


    // **************** MWNamedSchemaComponent contract **********************

    @Override
    public MWNamespace getTargetNamespace() {
        return this.getReferencedComponent().getParentNamespace();
    }

    @Override
    public String componentTypeName() {
        return this.getReferencedComponent().componentTypeName();
    }

    @Override
    public boolean directlyOwns(MWNamedSchemaComponent nestedComponent) {
        return this.getReferencedComponent().directlyOwns(nestedComponent);
    }

    @Override
    public void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {
        this.getReferencedComponent().addDirectlyOwnedComponentsTo(directlyOwnedComponents);
    }


    // **************** MWSchemaComponent contract ****************************

    @Override
    public boolean isReference() {
        return true;
    }


    // **************** MWSchemaModel contract ********************************

    @Override
    public Iterator xpathComponents() {
        return this.getReferencedComponent().xpathComponents();
    }

    /** Overridden from AbstractSchemaModel */
    @Override
    public MWAttributeDeclaration nestedAttribute(String namespaceUrl, String attributeName) {
        return this.getReferencedComponent().nestedAttribute(namespaceUrl, attributeName);
    }

    /** Overridden from AbstractSchemaModel */
    @Override
    public MWElementDeclaration nestedElement(String namespaceUrl, String elementName) {
        return this.getReferencedComponent().nestedElement(namespaceUrl, elementName);
    }


    // **************** SchemaModel contract **********************************

    @Override
    public void resolveReferences() {
        super.resolveReferences();
        this.resolveReference(this.getNamespaceUrl(), this.getName());
    }


    // **************** SchemaComponentReference contract *********************

    protected /* private-protected */ abstract void resolveReference(String componentNamespace, String componentName);
}
