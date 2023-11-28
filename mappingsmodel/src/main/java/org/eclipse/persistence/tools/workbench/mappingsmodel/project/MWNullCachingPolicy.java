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
package org.eclipse.persistence.tools.workbench.mappingsmodel.project;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCacheExpiry;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWCachingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;

import org.eclipse.persistence.descriptors.ClassDescriptor;

final class MWNullCachingPolicy extends MWModel
                                            implements MWCachingPolicy
{
    MWNullCachingPolicy(MWModel parent)
    {
        super(parent);
    }

    @Override
    public void initializeFrom(MWCachingPolicy otherPolicy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExistenceCheckingOption getExistenceChecking() {
        return null;
    }

    @Override
    public CacheTypeOption getCacheType() {
        return null;
    }

    @Override
    public int getCacheSize() {
        return 0;
    }

    @Override
    public void setExistenceChecking(ExistenceCheckingOption newExistenceChecking) {
        throw new UnsupportedOperationException("Null policy cannot be modified.");
    }

    @Override
    public void setExistenceChecking(String existenceChecking) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

    @Override
    public void setCacheType(CacheTypeOption cacheType) {
        throw new UnsupportedOperationException("Null policy cannot be modified.");
    }

    @Override
    public void setCacheType(String cacheTypeString) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

    @Override
    public void setCacheSize(int size) {
        throw new UnsupportedOperationException("Null policy cannot be modified.");
    }

    @Override
    public MWCacheExpiry getCacheExpiry() {
        return null;
    }

    @Override
    public void setUseProjectDefaultCacheExpiry(boolean useProjectCacheExpiry) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

    @Override
    public CacheCoordinationOption getCacheCoordination() {
        return null;
    }

    @Override
    public void setCacheCoordination(CacheCoordinationOption cacheCoordination) {
        throw new UnsupportedOperationException("Null policy cannot be modified.");
    }

    @Override
    public CacheIsolationOption getCacheIsolation() {
        return null;
    }

    @Override
    public void setCacheIsolation(CacheIsolationOption cacheIsolation) {
        throw new UnsupportedOperationException("Null policy cannot be modified.");
    }

    @Override
    public MWMappingDescriptor getOwningDescriptor() {
        throw new UnsupportedOperationException();//TODO grrrr, need to make a separate interface for the descriptor caching policy for this and adjustRuntimeDescriptor
    }

    @Override
    public boolean usesProjectDefaultCacheSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void descriptorInheritanceChanged() {
    }



    // ***************** runtime conversion ***************

    @Override
    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor)     {
        //nothing here
    }


    @Override
    public MWCachingPolicy getPersistedPolicy() {
        return null;
    }
}