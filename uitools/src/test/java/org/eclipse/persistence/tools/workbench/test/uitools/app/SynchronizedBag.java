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
package org.eclipse.persistence.tools.workbench.test.uitools.app;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;

/**
 * Helper class that keeps an internal collection in synch with the
 * collection held by a collection value model.
 */
class SynchronizedBag implements Bag, CollectionChangeListener {

    private Bag synchBag = new HashBag();

    SynchronizedBag(CollectionValueModel cvm) {
        cvm.addCollectionChangeListener(ValueModel.VALUE, this);
    }


    // ********** Collection implementation **********

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    @Override
    public boolean add(Object o) {
        return this.synchBag.add(o);
    }

    /**
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection c) {
        return this.synchBag.addAll(c);
    }

    /**
     * @see java.util.Collection#clear()
     */
    @Override
    public void clear() {
        this.synchBag.clear();
    }

    /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return this.synchBag.contains(o);
    }

    /**
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection c) {
        return this.synchBag.containsAll(c);
    }

    /**
     * @see java.util.Collection#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return this.synchBag.isEmpty();
    }

    /**
     * @see java.util.Collection#iterator()
     */
    @Override
    public Iterator iterator() {
        return this.synchBag.iterator();
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return this.synchBag.remove(o);
    }

    /**
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection c) {
        return this.synchBag.removeAll(c);
    }

    /**
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection c) {
        return this.synchBag.retainAll(c);
    }

    /**
     * @see java.util.Collection#size()
     */
    @Override
    public int size() {
        return this.synchBag.size();
    }

    /**
     * @see java.util.Collection#toArray()
     */
    @Override
    public Object[] toArray() {
        return this.synchBag.toArray();
    }

    /**
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    @Override
    public Object[] toArray(Object[] a) {
        return this.synchBag.toArray(a);
    }


    // ********** CollectionChangeListener implementation **********

    /**
     * @see org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener#itemsAdded(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
     */
    @Override
    public void itemsAdded(CollectionChangeEvent e) {
        for (Iterator stream = e.items(); stream.hasNext(); ) {
            this.synchBag.add(stream.next());
        }
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener#itemsRemoved(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
     */
    @Override
    public void itemsRemoved(CollectionChangeEvent e) {
        for (Iterator stream = e.items(); stream.hasNext(); ) {
            this.synchBag.remove(stream.next());
        }
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener#collectionChanged(org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent)
     */
    @Override
    public void collectionChanged(CollectionChangeEvent e) {
        this.synchBag.clear();
        CollectionTools.addAll(this.synchBag, (Iterator) ((CollectionValueModel) e.getSource()).getValue());
    }


    // ********** standard methods **********

    /**
     * @see java.util.List#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return this.synchBag.equals(o);
    }

    /**
     * @see java.util.List#hashCode()
     */
    @Override
    public int hashCode() {
        return this.synchBag.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.synchBag.toString();
    }

}
