/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.transparentindirection;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

public class TransparentIndirectionSystem extends TestSystem {
    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        (new GeneratedIndirectContainerTableCreator()).replaceTables(session);
    }
}