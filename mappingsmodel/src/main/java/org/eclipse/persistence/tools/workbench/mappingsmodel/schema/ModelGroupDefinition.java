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
import java.util.List;

import org.apache.xerces.impl.xs.XSGroupDecl;
import org.apache.xerces.xs.XSObject;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
public final class ModelGroupDefinition
    extends AbstractNamedSchemaComponent
    implements MWModelGroupDefinition
{
    private volatile ExplicitModelGroup modelGroup;


    // **************** Static methods ****************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ModelGroupDefinition.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractNamedSchemaComponent.class);

        XMLCompositeObjectMapping modelGroupMapping = new XMLCompositeObjectMapping();
        modelGroupMapping.setAttributeName("modelGroup");
        modelGroupMapping.setReferenceClass(ExplicitModelGroup.class);
        modelGroupMapping.setXPath("model-group");
        descriptor.addMapping(modelGroupMapping);

        return descriptor;
    }


    // **************** Constructors ******************************************

    /** Toplink persistence use only */
    private ModelGroupDefinition() {
        super();
    }

    ModelGroupDefinition(MWModel parent, String name) {
        super(parent, name);
    }


    // **************** Inititalization ***************************************

    @Override
    protected /* private-protected */ void initialize(Node parent) {
        super.initialize(parent);
        this.modelGroup = new ExplicitModelGroup(this);
    }

    @Override
    protected void addChildrenTo(List children) {
        super.addChildrenTo(children);
        children.add(this.modelGroup);
    }


    // **************** MWModelGroupDefinition contract ***********************

    @Override
    public MWModelGroup getModelGroup() {
        return this.modelGroup;
    }


    // **************** MWSchemaContextComponent contract *********************

    @Override
    public boolean hasType() {
        return false;
    }

    @Override
    public String contextTypeQname() {
        return null;
    }

    @Override
    public boolean containsText() {
        return false;
    }

    @Override
    public boolean containsWildcard() {
        return this.modelGroup.containsWildcard();
    }

    @Override
    public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
        return this.modelGroup.compareSchemaOrder(element1, element2);
    }

    @Override
    public Iterator baseBuiltInTypes() {
        return NullIterator.instance();
    }


    // **************** MWNamedSchemaComponent contract ***********************

    @Override
    public String componentTypeName() {
        return "group";
    }

    @Override
    public void addDirectlyOwnedComponentsTo(Collection directlyOwnedComponents) {
        this.modelGroup.addDirectlyOwnedComponentsTo(directlyOwnedComponents);
    }


    // **************** MWSchemaModel contract ********************************

    @Override
    public Iterator structuralComponents() {
        return this.modelGroup.structuralComponents();
    }

    @Override
    public Iterator descriptorContextComponents() {
        return this.modelGroup.descriptorContextComponents();
    }

    @Override
    public Iterator xpathComponents() {
        return this.modelGroup.xpathComponents();
    }

    @Override
    public MWNamedSchemaComponent nestedNamedComponent(QName qName) {
        return this.modelGroup.nestedNamedComponent(qName);
    }

    @Override
    public MWElementDeclaration nestedElement(String namespaceUrl, String elementName) {
        return this.modelGroup.nestedElement(namespaceUrl, elementName);
    }

    @Override
    public int totalElementCount() {
        return this.modelGroup.totalElementCount();
    }


    // **************** SchemaModel contract **********************************

    @Override
    protected void reloadInternal(XSObject xsModelGroupDef) {
        super.reloadInternal(xsModelGroupDef);
        this.modelGroup.reload(((XSGroupDecl)xsModelGroupDef).getModelGroup());
    }

    @Override
    public void resolveReferences() {
        super.resolveReferences();
        this.modelGroup.resolveReferences();
    }
}
