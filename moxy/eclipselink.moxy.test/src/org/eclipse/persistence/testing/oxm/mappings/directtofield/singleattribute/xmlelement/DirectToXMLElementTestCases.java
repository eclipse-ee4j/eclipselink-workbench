/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlelement;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.Employee;

public class DirectToXMLElementTestCases extends XMLMappingTestCases {

  private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/singleattribute/xmlelement/DirectToXMLElement.xml";
  private final static int CONTROL_ID = 123;

  public DirectToXMLElementTestCases(String name) throws Exception {
    super(name);
    setControlDocument(XML_RESOURCE);
    setProject(new DirectToXMLElementProject());
  }

  protected Object getControlObject() {
    Employee employee = new Employee();
    employee.setID(CONTROL_ID);
    return employee;
  }
}