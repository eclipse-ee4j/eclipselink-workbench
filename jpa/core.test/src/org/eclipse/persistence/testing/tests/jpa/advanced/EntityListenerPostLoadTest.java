/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.advanced;

import org.eclipse.persistence.testing.models.jpa.advanced.EmployeeListener;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;

/**
 * Tests the @PostLoad events from an EntityListener.
 *
 * @author Guy Pelletier
 */
public class EntityListenerPostLoadTest extends CallbackEventTest {
    public void test() throws Exception {
        m_beforeEvent = EmployeeListener.POST_LOAD_COUNT;
        
        getEntityManager().find(Employee.class, m_employee.getId());
        
        m_afterEvent = EmployeeListener.POST_LOAD_COUNT;
    }
}