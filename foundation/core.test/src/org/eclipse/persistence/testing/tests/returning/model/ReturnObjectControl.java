/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.returning.model;

import org.eclipse.persistence.sessions.Session;

public interface ReturnObjectControl {
    Object getObjectForInsert(Session session, Object objectToInsert);

    Object getObjectForUpdate(Session session, Object objectToUpdateBeforeChange, Object objectToUpdateAfterChange, boolean useUOW);
}