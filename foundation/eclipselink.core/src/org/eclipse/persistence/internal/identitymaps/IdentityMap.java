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
import java.io.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * <p><b>Purpose</b>: Caches objects, and allows their retrieval  by their primary key.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Store CacheKeys containing objects and possibly writeLockValues.
 * <li> Insert & retrieve objects from the cache.
 * <li> Allow retrieval and modification of writeLockValue for a cached object.
 * </ul>
 * @see CacheKey
 * @since TOPLink/Java 1.0
 */
public abstract class IdentityMap implements Serializable, Cloneable {

    /** The innitial or maximum size of the cache depending upon the concrete implementation. */
    protected int maxSize;

    /** PERF: Store the descriptor class to allow lastAccessed cache lookup optimization. */
    protected Class descriptorClass;
    
    public IdentityMap() {}
    
    /**
     * Instantiate an new IdentityMap with it's maximum size.<p>
     * <b>NOTE</b>: Subclasses may provide different behaviour for maxSize.
     * @param size is the maximum size to be allocated for the receiver.
     */
    public IdentityMap(int size) {
        this.maxSize = size;
    }

    /**
     * Acquire a deferred lock on the object.
     * This is used while reading if the object has relationships without indirection.
     * This first thread will get an active lock.
     * Other threads will get deferred locks, all threads will wait until all other threads are complete before releasing their locks.
     */
    public CacheKey acquireDeferredLock(Vector primaryKey) {
        // Create and lock a new cacheKey.
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null);
        newCacheKey.acquireDeferredLock();
        // PERF: To avoid synchronization, getIfAbsentPut is used.
        CacheKey cacheKey = getCacheKeyIfAbsentPut(newCacheKey);
        // Return if the newCacheKey was put as it was already locked, otherwise must still lock it.
        if (cacheKey == null) {
            return newCacheKey;
        } else {
            newCacheKey.releaseDeferredLock();
        }
        // Acquire a lock on the cache key.
        cacheKey.acquireDeferredLock();
        return cacheKey;
    }

    /**
     * Acquire an active lock on the object.
     * This is used by reading (when using indirection or no relationships) and by merge.
     */
    public CacheKey acquireLock(Vector primaryKey, boolean forMerge) {
        // Create and lock a new cacheKey.
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null);
        newCacheKey.acquire(forMerge);
        // PERF: To avoid synchronization, getIfAbsentPut is used.
        CacheKey cacheKey = getCacheKeyIfAbsentPut(newCacheKey);
        // Return if the newCacheKey was put as it was already locked, otherwise must still lock it.
        if (cacheKey == null) {
            return newCacheKey;
        } else {
            newCacheKey.release();
        }
        // Acquire a lock on the cache key.
        cacheKey.acquire();
        return cacheKey;
    }

    /**
     * Acquire an active lock on the object, if not already locked.
     * This is used by merge for missing existing objects.
     */
    public CacheKey acquireLockNoWait(Vector primaryKey, boolean forMerge) {
        // Create and lock a new cacheKey.
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null);
        newCacheKey.acquire(forMerge);
        // PERF: To avoid synchronization, getIfAbsentPut is used.
        CacheKey cacheKey = getCacheKeyIfAbsentPut(newCacheKey);
        // Return if the newCacheKey was put as already lock, otherwise must still lock.
        if (cacheKey == null) {
            return newCacheKey;
        } else {
            newCacheKey.release();
        }
        // Acquire a lock on the cache key.
        // Return null if already locked.
        if (!cacheKey.acquireNoWait(forMerge)) {
            return null;
        }
        return cacheKey;
    }

    /**
     * Acquire a read lock on the object.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKey(Vector primaryKey) {
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null);
        CacheKey cacheKey = getCacheKey(newCacheKey);
        if (cacheKey == null) {
            // Lock new cacheKey.
            newCacheKey.acquireReadLock();
            // Create one but not put it in the cache, as we are only reading
            // and should not be writing to the identitymap.
            return newCacheKey;
        }
        // Acquire a lock on the cache key.
        // Return null if already locked.
        cacheKey.acquireReadLock();
        return cacheKey;
    }

    /**
     * Acquire a read lock on the object, if not already locked.
     * This is used by UnitOfWork cloning.
     * This will allow multiple users to read the same object but prevent writes to the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKeyNoWait(Vector primaryKey) {
        CacheKey newCacheKey = createCacheKey(primaryKey, null, null);
        CacheKey cacheKey = getCacheKey(newCacheKey);
        if (cacheKey == null) {
            // Lock new cacheKey.
            newCacheKey.acquireReadLock();
            // Create one but not put it in the cache, as we are only reading
            // and should not be writing to the identitymap.
            return newCacheKey;
        }
        // Acquire a lock on the cache key.
        // Return null if already locked.
        if (!cacheKey.acquireReadLockNoWait()) {
            return null;
        }
        return cacheKey;
    }
    
    /**
     * Add all locked CacheKeys to the map grouped by thread.
     * Used to print all the locks in the identity map.
     */
    public abstract void collectLocks(HashMap threadList);

    /**
     * Clone the map and all of the CacheKeys.
     * This is used by UnitOfWork commitAndResumeOnFailure to avoid corrupting the cache during a failed commit.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
    }

    /**
     * Return true if an CacheKey with the primary key is in the map.
     * User API.
     * @param primaryKey is the primary key for the object to search for.
     */
    public boolean containsKey(Vector primaryKey) {
        return getCacheKeyWithReadLock(primaryKey) != null;
    }

    /**
     * Create the correct type of CacheKey for this map.
     */
    public CacheKey createCacheKey(Vector primaryKey, Object object, Object writeLockValue) {
        return createCacheKey(primaryKey, object, writeLockValue, 0);
    }

    /**
     * Create the correct type of CacheKey for this map.
     */
    public CacheKey createCacheKey(Vector primaryKey, Object object, Object writeLockValue, long readTime) {
        return new CacheKey(primaryKey, object, writeLockValue, readTime);
    }

    /**
     * Allow for the cache to be iterated on.
     */
    public abstract Enumeration elements();

    /**
     * Return the object cached in the identity map or null if it could not be found.
     * User API.
     */
    public Object get(Vector primaryKey) {
        CacheKey cacheKey = getCacheKeyWithReadLock(primaryKey);
        if (cacheKey == null) {
            return null;
        }
        return cacheKey.getObject();
    }

    /**
     * Get the cache key (with object) for the primary key.
     */
    protected CacheKey getCacheKey(Vector primaryKey) {
        CacheKey searchKey = new CacheKey(primaryKey);
        return getCacheKey(searchKey);
    }

    /**
     * Return the cache key (with object) matching the searchKey.
     */
    protected abstract CacheKey getCacheKey(CacheKey searchKey);
    
    /**
     * Return the CacheKey (with object) matching the searchKey.
     * If the CacheKey is missing then put the searchKey in the map.
     * The searchKey should have already been locked. 
     */
    protected abstract CacheKey getCacheKeyIfAbsentPut(CacheKey cacheKey);

    /**
     * Get the cache key (with object) for the primary key with read lock.
     */
    protected CacheKey getCacheKeyWithReadLock(Vector primaryKey) {
        CacheKey key = getCacheKey(primaryKey);
        if (key != null) {
            key.checkReadLock();
        }
        return key;
    }

    /**
     * Returns the class which should be used as an identity map in a descriptor by default.
     */
    public static Class getDefaultIdentityMapClass() {
        return ClassConstants.SoftCacheWeakIdentityMap_Class;
    }

    /**
     * @return The maxSize for the IdentityMap (NOTE: some subclasses may use this differently).
     */
    public int getMaxSize() {
        if (maxSize == -1) {
            maxSize = 100;
        }
        return maxSize;
    }

    /**
     * Return the number of CacheKeys in the IdentityMap.
     * This may contain weak referenced objects that have been garbage collected.
     */
    public abstract int getSize();

    /**
     * Return the number of actual objects of type myClass in the IdentityMap.
     * Recurse = true will include subclasses of myClass in the count.
     */
    public abstract int getSize(Class myClass, boolean recurse);

    /**
     * Get the wrapper object from the cache key associated with the given primary key,
     * this is used for EJB2.
     */
    public Object getWrapper(Vector primaryKey) {
        CacheKey cacheKey = getCacheKeyWithReadLock(primaryKey);
        if (cacheKey == null) {
            return null;
        } else {
            return cacheKey.getWrapper();
        }
    }

    /**
     * Get the write lock value from the cache key associated to the primarykey.
     * User API.
     */
    public Object getWriteLockValue(Vector primaryKey) {
        CacheKey cacheKey = getCacheKeyWithReadLock(primaryKey);
        if (cacheKey == null) {
            return null;
        } else {
            return cacheKey.getWriteLockValue();
        }
    }

    /**
     * Allow for the CacheKeys to be iterated on.
     */
    public abstract Enumeration keys();

    /**
     * Store the object in the cache at its primary key.
     * This is used by InsertObjectQuery, typically into the UnitOfWork identity map.
     * Merge and reads do not use put, but acquireLock.
     * Also an advanced (very) user API.
     * @param primaryKey is the primary key for the object.
     * @param object is the domain object to cache.
     * @param writeLockValue is the current write lock value of object, if null the version is ignored.
     */
    public abstract CacheKey put(Vector primaryKey, Object object, Object writeLockValue, long readTime);

    /**
     * Store the CacheKey in the map.
     */
    protected abstract void put(CacheKey cacheKey);

    /**
     * Remove the CacheKey with the primaryKey from the map.
     * This is used by DeleteObjectQuery and merge.
     * This is also an advanced (very) user API.
     */
    public Object remove(Vector primaryKey) {
        CacheKey key = getCacheKey(primaryKey);
        return remove(key);
    }

    /**
     * Remove the CacheKey from the map.
     */
    public abstract Object remove(CacheKey cacheKey);

    /**
     * Set the maximum size for the recevier.
     * @param size is the new maximum size.
     */
    protected synchronized void setMaxSize(int size) {
        maxSize = size;
    }

    /**
     * This method will be used to update the max cache size, any objects exceeding the max cache size will
     * be remove from the cache. Please note that this does not remove the object from the identityMap, except in
     * the case of the CacheIdentityMap.
     */
    public void updateMaxSize(int maxSize) {
        setMaxSize(maxSize);
    }
    
    /**
     * Return the class that this is the map for.
     */
    protected Class getDescriptorClass() {
        return descriptorClass;
    }
    
    /**
     * Set the class that this is the map for.
     */
    protected void setDescriptorClass(Class descriptorClass) {
        this.descriptorClass = descriptorClass;
    }

    /**
     * Update the wrapper object in the CacheKey associated with the given primaryKey,
     * this is used for EJB2.
     */
    public void setWrapper(Vector primaryKey, Object wrapper) {
        CacheKey cacheKey = getCacheKey(primaryKey);
        if (cacheKey != null) {
            cacheKey.setWrapper(wrapper);
        }
    }

    /**
     * Update the write lock value of the CacheKey associated with the given primaryKey.
     * This is used by UpdateObjectQuery, and is also an advanced (very) user API.
     */
    public void setWriteLockValue(Vector primaryKey, Object writeLockValue) {
        CacheKey cacheKey = getCacheKey(primaryKey);
        if (cacheKey != null) {
            //lock/release the cache key during the lock value updating
            cacheKey.acquire();
            cacheKey.setWriteLockValue(writeLockValue);
            cacheKey.release();
        }
    }

    public String toString() {
        return Helper.getShortClassName(getClass()) + "[" + getSize() + "]";
    }
}