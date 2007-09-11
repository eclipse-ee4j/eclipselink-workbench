/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.ReadAllTest;

public class ReadAllPartialReadingAddressTest extends ReadAllTest {
    public String attribute;

    public ReadAllPartialReadingAddressTest(int size, String attribute) {
        super(Address.class, size);
        setName("PartialReadingAddressTest" + attribute);
        this.attribute = attribute;
    }

    public ReadAllPartialReadingAddressTest(Class referenceClass, int originalObjectsSize) {
        super(referenceClass, originalObjectsSize);
    }
}