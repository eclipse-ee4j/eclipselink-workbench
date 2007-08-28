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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p><b>Purpose</b>: A FullIdentityMap holds all objects stored within it for the life of the application.
 * <p><b>Responsibilities</b>:<ul>
 *    <li> Guarantees identity
 * <li> Holds all cached objects indefinetly.
 * </ul>
 * @since TOPLink/Java 1.0
 */
public class FullIdentityMap extends IdentityMap {

    /** Hashtable of CacheKeys stored using their key. */
    protected Map<CacheKey, CacheKey> cacheKeys;

    public FullIdentityMap() {}
    
    public FullIdentityMap(int size) {
        super(size);
        cacheKeys = new IdentityMapHashMap(size);
    }

    /**
     * INTERNAL:
     * Clones itself.
     */
    public Object clone() {
        FullIdentityMap clone = (FullIdentityMap)super.clone();
        clone.setCacheKeys(new IdentityMapHashMap(getCacheKeys().size()));

        for (Iterator cacheKeysIterator = getCacheKeys().values().iterator(); cacheKeysIterator.hasNext();) {
            CacheKey key = (CacheKey)((CacheKey)cacheKeysIterator.next()).clone();
            clone.getCacheKeys().put(key, key);
        }

        return clone;
    }

    /**
     * INTERNAL:
     * Used to print all the Locks in every identity map in this session.
     */
    public void collectLocks(HashMap threadList) {
        Iterator cacheKeyIterator = getCacheKeys().values().iterator();
        while (cacheKeyIterator.hasNext()) {
            CacheKey cacheKey = (CacheKey)cacheKeyIterator.next();
            if (cacheKey.isAcquired()) {
                Thread activeThread = cacheKey.getMutex().getActiveThread();
                Set set = (Set)threadList.get(activeThread);
                if (set == null) {
                    set = new HashSet();
                    threadList.put(activeThread, set);
                }
                set.add(cacheKey);
            }
        }
    }

    /**
     * Allow for the cache to be iterated on.
     */
    public Enumeration elements() {
        return new IdentityMapEnumeration(this);
    }

    /**
     * Return the cache key matching the primary key of the searchKey.
     * If no object for the key exists, return null.
     */
    protected CacheKey getCacheKey(CacheKey searchKey) {
        return getCacheKeys().get(searchKey);
    }    
        
    /**
     * Return the CacheKey (with object) matching the searchKey.
     * If the CacheKey is missing then put the searchKey in the map.
     * The searchKey should have already been locked. 
     */
    protected CacheKey getCacheKeyIfAbsentPut(CacheKey cacheKey) {
        cacheKey.setOwningMap(this);
        return (CacheKey)((ConcurrentMap)getCacheKeys()).putIfAbsent(cacheKey, cacheKey);
    }

    /**
     * Return the cache keys.
     */
    public Map<CacheKey, CacheKey> getCacheKeys() {
        return cacheKeys;
    }

    /**
     * Return the number of CacheKeys in the IdentityMap.
     * This may contain weak referenced objects that have been garbage collected.
     */
    public int getSize() {
        return this.cacheKeys.size();
    }

    /**
     * Return the number of actual objects of type myClass in the IdentityMap.
     * Recurse = true will include subclasses of myClass in the count.
     */
    public int getSize(Class myClass, boolean recurse) {
        int count = 0;
        Iterator keys = getCacheKeys().values().iterator();

        while (keys.hasNext()) {
            CacheKey key = (CacheKey)keys.next();
            Object object = key.getObject();

            if (object != null) {
                if (recurse && myClass.isInstance(object)) {
                    count++;
                } else if (object.getClass().equals(myClass)) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Allow for the cache keys to be iterated on.
     */
    public Enumeration keys() {
        return new IdentityMapKeyEnumeration(this);
    }

    /**
     * Store the object in the cache at its primary key.
     * This is used by InsertObjectQuery, typically into the UnitOfWork identity map.
     * Merge and reads do not use put, but acquireLock.
     * Also an advanced (very) user API.
     * @param primaryKey is the primary key for the object.
     * @param object is the domain object to cache.
     * @param writeLockValue is the current write lock value of object, if null the version is ignored.
     */
    public CacheKey put(Vector primaryKey, Object object, Object writeLockValue, long readTime) {
        CacheKey newCacheKey = createCacheKey(primaryKey, object, writeLockValue, readTime);
        // Find the cache key in the map, reset it, or put the new one.
        CacheKey cacheKey = getCacheKeyIfAbsentPut(newCacheKey);
        if (cacheKey != null) {
            // The cache key is locked inside resetCacheKey() to keep other threads from accessing the object.
            resetCacheKey(cacheKey, object, writeLockValue, readTime);
        }

        return cacheKey;
    }

    /**
     * Store the object in the cache with the cache key.
     */
    protected void put(CacheKey cacheKey) {
        cacheKey.setOwningMap(this);
        getCacheKeys().put(cacheKey, cacheKey);
    }

    /**
     * Removes the CacheKey from the map.
     * @return the object held within the CacheKey or null if no object cached for given cacheKey.
     */
    public Object remove(CacheKey cacheKey) {
        if (cacheKey != null) {
            // Cache key needs to be locked when removing from the map.
            cacheKey.acquire();
            getCacheKeys().remove(cacheKey);
            // Cache key needs to be released after removing from the map.
            cacheKey.release();
            return cacheKey.getObject();
        } else {
            return null;
        }
    }

    /**
     * Reset the cache key with new data.
     */
    public void resetCacheKey(CacheKey key, Object object, Object writeLockValue, long readTime) {
        key.acquire();
        key.setObject(object);
        key.setWriteLockValue(writeLockValue);
        key.setReadTime(readTime);
        key.release();
    }

    protected void setCacheKeys(Map<CacheKey, CacheKey> cacheKeys) {
        this.cacheKeys = cacheKeys;
    }
}