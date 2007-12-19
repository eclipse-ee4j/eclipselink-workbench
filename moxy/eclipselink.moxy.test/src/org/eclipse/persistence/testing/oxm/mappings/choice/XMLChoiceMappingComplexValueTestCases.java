/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.choice;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class XMLChoiceMappingComplexValueTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/choice/ChoiceComplexValue.xml";

  public XMLChoiceMappingComplexValueTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    //setSession(SESSION_NAME);
    setProject(new EmployeeProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.name = "Jane Doe";
    
    Address addr = new Address();
    addr.street = "45 O'Connor";
    addr.city = "Ottawa";
    employee.choice = addr;
    
    employee.phone = "123-4567"; 
    
    return employee;

  }

}