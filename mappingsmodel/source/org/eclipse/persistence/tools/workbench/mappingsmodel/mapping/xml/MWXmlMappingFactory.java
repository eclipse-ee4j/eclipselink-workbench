/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMappingFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;

public abstract class MWXmlMappingFactory 
	implements MWMappingFactory 
{
	// **************** Factory methods ***************************************
	
	public MWDirectMapping createDirectMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWXmlDirectMapping((MWXmlDescriptor) descriptor, attribute, name);
	}
	
	public MWDirectCollectionMapping createDirectCollectionMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWXmlDirectCollectionMapping((MWXmlDescriptor) descriptor, attribute, name);
	}
	
	public MWDirectMapMapping createDirectMapMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		throw new UnsupportedOperationException("this is not yet supported, but will be in the future");
	}

	public MWCompositeObjectMapping createCompositeObjectMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWCompositeObjectMapping((MWXmlDescriptor) descriptor, attribute, name);
	}
	
	public MWCompositeCollectionMapping createCompositeCollectionMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWCompositeCollectionMapping((MWXmlDescriptor) descriptor, attribute, name);
	}
	
	public MWTransformationMapping createTransformationMapping(MWMappingDescriptor descriptor, MWClassAttribute attribute, String name) {
		return new MWXmlTransformationMapping((MWXmlDescriptor) descriptor, attribute, name);
	}
}