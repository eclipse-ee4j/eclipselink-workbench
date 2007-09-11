/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.legacy;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

public class LegacySystem extends TestSystem {
    public LegacySystem() {
        project = new LegacyProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new LegacyProject();
        }
        ((DatabaseSession)session).addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        new LegacyTables().replaceTables((DatabaseSession)session);
    }

    public void populate(DatabaseSession session) {
        Employee instance;
        PopulationManager manager = PopulationManager.getDefaultManager();

        instance = Employee.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");
        manager.registerObject(instance.computer, "example1");
        manager.registerObject(instance.shipments.firstElement(), "example1");
        manager.registerObject(((Shipment)instance.shipments.firstElement()).orders.firstElement(), "example1");

        instance = Employee.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");
        manager.registerObject(instance.computer, "example2");
        manager.registerObject(instance.shipments.firstElement(), "example2");
        manager.registerObject(((Shipment)instance.shipments.firstElement()).orders.firstElement(), "example2");

        instance = Employee.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "example3");
        manager.registerObject(instance.computer, "example3");
        manager.registerObject(instance.shipments.firstElement(), "example3");
        manager.registerObject(((Shipment)instance.shipments.firstElement()).orders.firstElement(), "example3");
    }
}