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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;

import org.eclipse.persistence.descriptors.ClassDescriptor;

public final class MWNullTransactionalPolicy extends MWModel
    implements MWTransactionalPolicy
{

    private MWLockingPolicy lockingPolicy = new MWNullLockingPolicy(this);
    private MWCachingPolicy cachingPolicy = new MWNullCachingPolicy(this);

    // ********** Constructors **********

    MWNullTransactionalPolicy(MWModel parent) {
        super(parent);
    }

    @Override
    public MWQueryManager getQueryManager() {
        return null;
    }

    @Override
    public MWRefreshCachePolicy getRefreshCachePolicy() {
        return null;
    }

    @Override
    public MWCachingPolicy getCachingPolicy() {
        return this.cachingPolicy;
    }

    public void setCachingPolicy(MWCachingPolicy cachingPolicy) {
        throw new UnsupportedOperationException("Cannot modify a Null Transactional Policy");
    }

    @Override
    public MWLockingPolicy getLockingPolicy() {
        return this.lockingPolicy;
    }

    @Override
    public boolean isConformResultsInUnitOfWork() {
        return false;
    }

    @Override
    public void setConformResultsInUnitOfWork(boolean conform){
        throw new UnsupportedOperationException("Cannot modify a Null Transactional Policy");
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setReadOnly(boolean newValue) {
        throw new UnsupportedOperationException("A non-transactional descriptor cannot set the readOnly property");
    }

    @Override
    public void descriptorInheritanceChanged() {
        // no op
    }

    @Override
    public String getDescriptorAlias() {
        return null;
    }

    @Override
    public void setDescriptorAlias(String descriptorAlias) {
        throw new UnsupportedOperationException("Cannot modify a Null Transactional Policy");
    }

    // ************ Runtime Conversion ***********

    @Override
    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {

    }


    // ************ TopLink Methods ***********

    @Override
    public MWAbstractTransactionalPolicy getValueForTopLink() {
        return null;
    }

}
