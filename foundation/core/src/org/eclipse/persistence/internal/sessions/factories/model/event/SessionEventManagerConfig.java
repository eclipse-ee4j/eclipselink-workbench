/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.factories.model.event;

import java.util.Vector;

/**
 * INTERNAL:
 */
public class SessionEventManagerConfig {
    private Vector m_sessionEventListeners;

    public SessionEventManagerConfig() {
        m_sessionEventListeners = new Vector();
    }

    public void addSessionEventListener(String listener) {
        m_sessionEventListeners.add(listener);
    }

    public void setSessionEventListeners(Vector sessionEventListeners) {
        m_sessionEventListeners = sessionEventListeners;
    }

    public Vector getSessionEventListeners() {
        return m_sessionEventListeners;
    }
}