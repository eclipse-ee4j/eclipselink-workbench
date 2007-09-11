/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sequencing;

import java.util.Hashtable;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.internal.sessions.remote.RemoteFunctionCall;
import org.eclipse.persistence.internal.sessions.remote.SequencingFunctionCall;

/*
 * RemoteConnectionSequencing class provides Sequencing through RemoteConnection.
 * It caches some info and communicates with Sequencing object
 * on its master session by sending SequencingFunctionCall objects
 * through the RemoteConnection.
 * Note that individual SequencingFunctionCalls
 * are implemented as static inner classes in SequenceFunctionCall class:
 * like SequencingFunctionCall.DoesExist.
 */
class RemoteConnectionSequencing implements Sequencing {
    protected RemoteConnection remoteConnection;
    protected Hashtable classToShouldAcquireValueAfterInsert;
    protected int whenShouldAcquireValueForAll;

    public static boolean masterSequencingExists(RemoteConnection con) {
        return ((Boolean)con.getSequenceNumberNamed(new SequencingFunctionCall.DoesExist())).booleanValue();
    }

    public RemoteConnectionSequencing(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
        whenShouldAcquireValueForAll = ((Integer)processFunctionCall(new SequencingFunctionCall.WhenShouldAcquireValueForAll())).intValue();
        if (whenShouldAcquireValueForAll == UNDEFINED) {
            classToShouldAcquireValueAfterInsert = new Hashtable(20);
        }
    }

    public int whenShouldAcquireValueForAll() {
        return whenShouldAcquireValueForAll;
    }

    public boolean shouldAcquireValueAfterInsert(Class cls) {
        if (whenShouldAcquireValueForAll == BEFORE_INSERT) {
            return false;
        } else if (whenShouldAcquireValueForAll == AFTER_INSERT) {
            return true;
        } else {
            Boolean after = (Boolean)classToShouldAcquireValueAfterInsert.get(cls);
            if (after == null) {
                after = (Boolean)processFunctionCall(new SequencingFunctionCall.ShouldAcquireValueAfterInsert(cls));
                classToShouldAcquireValueAfterInsert.put(cls, after);
            }
            return after.booleanValue();
        }
    }

    public boolean shouldOverrideExistingValue(Class cls, Object existingValue) {
        Boolean shouldOverride = (Boolean)processFunctionCall(new SequencingFunctionCall.ShouldOverrideExistingValue(cls, existingValue));
        return shouldOverride.booleanValue();
    }

    public Object getNextValue(Class cls) {
        return processFunctionCall(new SequencingFunctionCall.GetNextValue(cls));
    }

    protected Object processFunctionCall(RemoteFunctionCall call) {
        return remoteConnection.getSequenceNumberNamed(call);
    }
}