/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.Iterator;

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.QName;

public final class ReferencedElementDeclaration 
	extends SchemaComponentReference
	implements MWElementDeclaration 
{
	// **************** Variables *********************************************
	
	private volatile ExplicitElementDeclaration element;
	
	
	// **************** Constructors ******************************************
	
	/** Toplink Use Only */
	private ReferencedElementDeclaration() {
		super();
	}

	ReferencedElementDeclaration(AbstractSchemaModel parent, String elementName, String elementNamespace) {
		super(parent, elementName, elementNamespace);
	}
	
	
	// **************** SchemaComponentReference contract *********************
	
	protected MWNamedSchemaComponent getReferencedComponent() {
		return this.element;
	}
	
	protected void resolveReference(String elementNamespace, String elementName) {
		this.element = (ExplicitElementDeclaration) this.getSchema().element(elementNamespace, elementName);
	}
	
	
	// **************** MWElementDeclaration contract *************************
	
	public MWSchemaTypeDefinition getType() {
		return this.element.getType();
	}
	
	public MWElementDeclaration getSubstitutionGroup() {
		return this.element.getSubstitutionGroup();
	}
	
	public boolean isAbstract() {
		return this.element.isAbstract();
	}
	
	public String getDefaultValue() {
		return this.element.getDefaultValue();
	}
	
	public String getFixedValue() {
		return this.element.getFixedValue();
	}
	
	public boolean isNillable() {
		return this.element.isNillable();
	}
	
	
	// **************** MWParticle contract ***********************************
	
	public int getMinOccurs() {
		return this.element.getMinOccurs();
	}
	
	public int getMaxOccurs() {
		return this.element.getMaxOccurs();
	}
	
	public boolean isDescriptorContextComponent() {
		return false;
	}
	
	public boolean isEquivalentTo(XSParticleDecl xsParticle) {
		return this.element.isEquivalentTo(xsParticle);
	}
	
	
	// **************** MWXpathableSchemaComponent contract *******************
	
	public Iterator baseBuiltInTypes() {
		return this.element.baseBuiltInTypes();
	}
	
	
	// **************** MWSchemaContextComponent contract *********************
	
	public boolean hasType() {
		return true;
	}
	
	public String contextTypeQname() {
		return this.element.contextTypeQname();
	}
	
	public boolean containsText() {
		return this.element.containsText();
	}
	
	public boolean containsWildcard() {
		return this.element.containsWildcard();
	}
	
	public int compareSchemaOrder(MWElementDeclaration element1, MWElementDeclaration element2) {
		return this.element.compareSchemaOrder(element1, element2);
	}
	
	
	// **************** MWSchemaModel contract ********************************
	
	public MWNamedSchemaComponent nestedNamedComponent(QName qName) {
		return this.element.nestedNamedComponent(qName);
	}
	
	public int totalElementCount() {
		return this.element.totalElementCount();
	}
	
	
	// **************** Static methods ****************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(ReferencedElementDeclaration.class);
		descriptor.getInheritancePolicy().setParentClass(SchemaComponentReference.class);	
		return descriptor;
	}
}