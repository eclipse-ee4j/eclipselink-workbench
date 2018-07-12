/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;

import org.eclipse.persistence.descriptors.ClassDescriptor;

final public class MWNullLockingPolicy extends MWModel implements MWLockingPolicy, MWXmlNode {

    public MWNullLockingPolicy(MWTransactionalPolicy parent) {
        super(parent);
    }

    @Override
    public String getLockingType() {
        return "";
    }

    @Override
    public void setLockingType(String newLockingType) {
        throw new UnsupportedOperationException();

    }

    @Override
    public MWDataField getVersionLockField() {
        return null;
    }

    @Override
    public void setVersionLockField(MWDataField newLockField) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldStoreVersionInCache() {
        return false;
    }

    @Override
    public void setStoreInCache(boolean newStoreInCache) {
        throw new UnsupportedOperationException();
    }

    // **************** Model synchronization *********************************

    /** @see MWXmlNode#resolveXpaths() */
    @Override
    public void resolveXpaths() {
        // Do nothing.  Null policy.
    }

    /** @see MWXmlNode#schemaChanged(SchemaChange) */
    @Override
    public void schemaChanged(SchemaChange change) {
        // Do nothing.  Null policy.
    }

    @Override
    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
        // Do nothing.  Null policy.
    }
}
