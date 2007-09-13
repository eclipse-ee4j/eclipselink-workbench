/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

// Javase imports
import java.util.Vector;

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import static org.eclipse.persistence.internal.helper.Helper.getShortClassName;

/**
 * <p>
 * <b>INTERNAL:</b> BaseEntity is used for models where Java classes do not
 * exist.
 * <p>
 * TopLink is based around mapping attributes of a Java class to a table (or
 * tables) with the attributes representing either the column data or
 * foreign-key contraints as relationships to other (mapped) classes. For
 * applications that are based around meta-data and the Java class is either not
 * needed or not available, this basic entity can be used. Subclasses of this
 * abstract class can be dynamically generated at runtime.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
@SuppressWarnings("unchecked")
public class BaseEntity implements PersistenceEntity {

    protected Object[] fields; // BaseEntities only deal with Objects, never
                                // primitives

    protected BaseEntity() {
    }

    public Object get(int i) {
        return fields[i];
    }

    public void set(int i, Object aFieldValue) {
        fields[i] = aFieldValue;
    }

    protected CacheKey __cacheKey;
    protected Vector __pk;
    // perf. optimization - cache the __cacheKey and pkVector
    public CacheKey _persistence_getCacheKey() {
        return __cacheKey;
    }
    public void _persistence_setCacheKey(CacheKey cacheKey) {
        this.__cacheKey = cacheKey;
    }

    public Vector _persistence_getPKVector() {
        return __pk;
    }
    public void _persistence_setPKVector(Vector pk) {
        this.__pk = pk;
    }

    public String toString() {
        // this will print something like {Emp 10} or {Phone 234-5678 10}
        StringBuilder sb = new StringBuilder(20);
        sb.append('{');
        sb.append(getShortClassName(this.getClass()));
        if (__pk != null) {
            for (int i = 0; i < __pk.size(); i++) {
                sb.append(' ');
                sb.append(__pk.elementAt(i));
            }
        }
        sb.append('}');
        return sb.toString();
    }
}