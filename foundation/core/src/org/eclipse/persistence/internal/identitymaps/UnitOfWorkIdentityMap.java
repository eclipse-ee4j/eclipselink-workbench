/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.identitymaps;

import java.util.*;

/**
 * Unit of work specific identity map which avoid additional overhead not required in unit of work,
 * such as locking and synchronziation.
 * @author James Sutherland
 * @since TopLink 10.1.3.1
 */
public class UnitOfWorkIdentityMap extends FullIdentityMap {

    public UnitOfWorkIdentityMap(int size) {
        super();
        this.maxSize = size;
        // PERF: Use a HashMap as more efficient than a ConcurrentMap and single threaded.
        this.cacheKeys = new HashMap(size);
    }
    
    public CacheKey createCacheKey(Vector primaryKey, Object object, Object writeLockValue, long readTime) {
        return new UnitOfWorkCacheKey(primaryKey, object, writeLockValue, readTime);
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    public CacheKey acquireDeferredLock(Vector primaryKey) {
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null);
        CacheKey cacheKey = getCacheKeyIfAbsentPut(newCacheKey);
        if (cacheKey == null) {
            return newCacheKey;
        }
        return cacheKey;
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    public CacheKey acquireLock(Vector primaryKey, boolean forMerge) {
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null);
        CacheKey cacheKey = getCacheKeyIfAbsentPut(newCacheKey);
        if (cacheKey == null) {
            return newCacheKey;
        }
        return cacheKey;
    }
    
    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    public CacheKey acquireLockNoWait(Vector primaryKey, boolean forMerge) {
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null);
        CacheKey cacheKey = getCacheKeyIfAbsentPut(newCacheKey);
        if (cacheKey == null) {
            return newCacheKey;
        }
        return cacheKey;
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    public CacheKey acquireReadLockOnCacheKey(Vector primaryKey) {
        return acquireReadLockOnCacheKeyNoWait(primaryKey);
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    public CacheKey acquireReadLockOnCacheKeyNoWait(Vector primaryKey) {
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null);
        CacheKey cacheKey = getCacheKey(newCacheKey);
        if (cacheKey == null) {
            return newCacheKey;
        }
        return cacheKey;
    }
    
    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    protected CacheKey getCacheKeyWithReadLock(Vector primaryKey) {
        return getCacheKey(primaryKey);
    }
    
    /**
     * Since a HashMap is used, must do a get and put.
     * However HashMap is more efficient than a ConcurrentMap so is ok.
     */
    protected CacheKey getCacheKeyIfAbsentPut(CacheKey searchKey) {
        CacheKey cacheKey = getCacheKeys().get(searchKey);
        if (cacheKey == null) {
            searchKey.setOwningMap(this);
            getCacheKeys().put(searchKey, searchKey);
            return null;
        }
        return cacheKey;
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    public Object remove(CacheKey cacheKey) {
        if (cacheKey != null) {
            getCacheKeys().remove(cacheKey);
        } else {
            return null;
        }
        return cacheKey.getObject();
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    public void resetCacheKey(CacheKey key, Object object, Object writeLockValue, long readTime) {
        key.setObject(object);
        key.setWriteLockValue(writeLockValue);
        key.setReadTime(readTime);
    }

    /**
     * Avoid acquiring any lock as uow is single threaded.
     */
    public void setWriteLockValue(Vector primaryKey, Object writeLockValue) {
        CacheKey cacheKey = getCacheKey(primaryKey);
        if (cacheKey != null) {
            cacheKey.setWriteLockValue(writeLockValue);
        }
    }
}