/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class PostResumeUnitOfWorkTest extends UnitOfWorkEventTest {
    public void setup() {
        super.setup();
        setDescription("Test the postResumeUnitOfWork Event");
        SessionEventAdapter tvAdapter = new SessionEventAdapter() {
                // Listen for PostResumeUnitOfWorkEvents

                public void postResumeUnitOfWork(SessionEvent event) {
                    setEventTriggered(true);
                }
            };
        getSession().getEventManager().addListener(tvAdapter);
    }

    public void test() {
        UnitOfWork tvUnitOfWork = getSession().acquireUnitOfWork();
        tvUnitOfWork.commitAndResume();
    }

    public void verify() {
        if (!isEventTriggered()) {
            throw new TestErrorException("The postResumeUnitOfWork event was not triggered.");
        }
    }
}