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

import org.eclipse.persistence.testing.models.jpa.advanced.Project;

/**
 * Tests the @PostLoad events from an Entity.
 *
 * @author Guy Pelletier
 */
public class EntityMethodPostLoadTest extends CallbackEventTest {
    public void test() throws Exception {
        m_beforeEvent = 0;  // New object, count starts at 0.
        
        Project project = getEntityManager().find(Project.class, m_project.getId());
        
        m_afterEvent = project.post_load_count;
    }
}