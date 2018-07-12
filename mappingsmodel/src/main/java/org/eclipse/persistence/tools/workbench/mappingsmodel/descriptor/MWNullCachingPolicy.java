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

import org.eclipse.persistence.descriptors.ClassDescriptor;

public final class MWNullCachingPolicy extends MWModel implements MWCachingPolicy {

    private static final CacheCoordinationOption nullCacheCoordinationOption = new CacheCoordinationOption("", "", -1);
    private static final CacheIsolationOption nullCacheIsolationOption = new CacheIsolationOption("", "");
    private static final ExistenceCheckingOption nullExistenceCheckingOption = new ExistenceCheckingOption("", "", -1);

    public MWNullCachingPolicy(MWTransactionalPolicy parent) {
        super(parent);
    }

    @Override
    public CacheTypeOption getCacheType() {
        return MWDescriptorCachingPolicy.PROJECT_DEFAULT_CACHE_TYPE;
    }

    @Override
    public void setCacheType(CacheTypeOption cacheType) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

    @Override
    public void setCacheType(String cacheTypeString) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

    @Override
    public int getCacheSize() {
        return 0;
    }

    @Override
    public void setCacheSize(int size) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

    @Override
    public boolean usesProjectDefaultCacheSize() {
        return true;
    }

    @Override
    public CacheCoordinationOption getCacheCoordination() {
        return nullCacheCoordinationOption;
    }

    @Override
    public void setCacheCoordination(CacheCoordinationOption cacheCoordination) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

    @Override
    public CacheIsolationOption getCacheIsolation() {
        return nullCacheIsolationOption;
    }

    @Override
    public void setCacheIsolation(CacheIsolationOption cacheIsolation) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

    @Override
    public ExistenceCheckingOption getExistenceChecking() {
        return nullExistenceCheckingOption;
    }

    @Override
    public void setExistenceChecking(ExistenceCheckingOption existenceChecking) {
        throw new UnsupportedOperationException("Can not modify a null policy");
    }

    @Override
    public void setExistenceChecking(String existenceChecking) {
        throw new UnsupportedOperationException("Can not modify a null policy");
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
    public MWMappingDescriptor getOwningDescriptor() {
        return (MWMappingDescriptor) ((MWTransactionalPolicy) getParent()).getParent();
    }

    @Override
    public void initializeFrom(MWCachingPolicy otherPolicy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
        //null policy
    }

    @Override
    public MWCachingPolicy getPersistedPolicy() {
        return null;
    }

    @Override
    public void descriptorInheritanceChanged() {
        //null policy
    }
}
