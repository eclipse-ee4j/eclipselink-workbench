/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.manual;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.inheritance.Car;

public class ReadToSeeMultipleIDsTest extends ManualVerifyTestCase {
    public ReadToSeeMultipleIDsTest() {
        setDescription("Check select SQL and make sure that ID's are not printed multiple times.");
    }

    public void test() {
        getSession().readAllObjects(Car.class);
    }
}