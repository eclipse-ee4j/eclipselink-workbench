/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/** This class has been modified as per instructions from Tom Ware and Development
 *  the test(), verify() are all inherited from the parent class
 *  We pass in the TopLink project and the string we are looking for and the superclass does the verification and testing
 */
public class UseCollectionClassOnMappingTest extends ProjectClassGeneratorResultFileTest {
    ClassDescriptor descriptorToModify;
    DatabaseMapping mappingToModify;

    public UseCollectionClassOnMappingTest() {
        super(new org.eclipse.persistence.testing.models.employee.relational.EmployeeProject(), 
              ".useCollectionClass(org.eclipse.persistence.testing.models.employee.domain.Employee.class);");
        setDescription("Test addForeignReferenceMappingLines method -> useCollectionClass");
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        descriptorToModify = (ClassDescriptor)project.getDescriptors().get(Employee.class);
        mappingToModify = descriptorToModify.getMappingForAttributeName("projects");

        if (mappingToModify.isForeignReferenceMapping()) {
            if (((ForeignReferenceMapping)mappingToModify).isCollectionMapping()) {
                CollectionMapping collectionMapping = 
                    (CollectionMapping)(((ForeignReferenceMapping)mappingToModify));
                collectionMapping.getContainerPolicy().setContainerClass(Employee.class);
            }
        }
    }
}