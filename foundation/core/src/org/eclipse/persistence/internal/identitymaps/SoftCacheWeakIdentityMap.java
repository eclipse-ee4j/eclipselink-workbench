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

import java.lang.ref.*;

/**
 * <p><b>Purpose</b>: A SoftCacheWeakIdentityMap is identical to the WeakIdentityMap, however the weak
 * can be a performance problem for some types of apps because it can cause too much garbage collection
 * of objects read causing them to be re-read and re-built (this defeats the purpose of the cache).
 * The SoftCacheWeakIdentityMap solves this through also holding a fixed number of objects is memory to improve caching.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Guarantees identity
 * <li> Allows garbage collection
 * <li> Increases performance through maintaining a fixed size cache of MRU objects when memory is available.
 * <li> The default size of the reference cache is the max size.
 * </ul>
 * @since TOPLink/Java 1.2
 */
public class SoftCacheWeakIdentityMap extends HardCacheWeakIdentityMap {
    public SoftCacheWeakIdentityMap(int size) {
        super(size);
    }

    /**
     * Creates a Soft reference to the object.
     * @param object is the domain object to cache.
     */
    public Object buildReference(Object object) {
        if (object != null) {
            return new SoftReference(object);
        } else {
            return null;
        }
    }

    /**
     * Checks if the object is null, or reference's object is null.
     * @param the object for hard or the reference for soft.
     */
    public boolean hasReference(Object reference) {
        return (reference != null) && (((SoftReference)reference).get() != null);
    }
}