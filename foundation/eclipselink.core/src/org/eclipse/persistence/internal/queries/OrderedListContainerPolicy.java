/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.queries;

import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.ListIterator;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.IdentityHashtable;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.IndirectCollection;

/**
 * <p><b>Purpose</b>: A OrderedListContainerPolicy is ContainerPolicy whose
 * container class implements the List interface and is ordered by an @OrderBy.
 * <p>
 * <p><b>Responsibilities</b>:
 * Provide the functionality to operate on an instance of a List.
 *
 * @see ContainerPolicy
 * @see CollectionContainerPolicy
 * @see ListContainerPolicy
 */
public class OrderedListContainerPolicy extends ListContainerPolicy {
    /**
     * INTERNAL:
     * Construct a new policy.
     */
    public OrderedListContainerPolicy() {
        super();
    }
    
    /**
     * INTERNAL:
     * Construct a new policy for the specified class.
     */
    public OrderedListContainerPolicy(Class containerClass) {
        super(containerClass);
    }
    
    /**
     * INTERNAL:
     * Construct a new policy for the specified class name.
     */
    public OrderedListContainerPolicy(String containerClassName) {
        super(containerClassName);
    }
    
    /**
     * INTERNAL:
     * Add element into a container which implements the List interface.
     * Add at a particular index.
     */
    protected void addIntoAtIndex(Integer index, Object object, Object container, AbstractSession session) {
        if (hasElementDescriptor()) {
            object = getElementDescriptor().getObjectBuilder().wrapObject(object, session);
        }
        
        try {
            if (index == null || (index.intValue() > sizeFor(container))) {
                // The index can be larger than the size on a merge,
                // so should be added to the end, it may also be null if the 
                // index was unknown, such as an event using the add API.
                ((List)container).add(object);
            } else {
                ((List)container).add(index.intValue(), object);
            }
        } catch (ClassCastException ex1) {
            throw QueryException.cannotAddElement(object, container, ex1);
        } catch (IllegalArgumentException ex2) {
            throw QueryException.cannotAddElement(object, container, ex2);
        } catch (UnsupportedOperationException ex3) {
            throw QueryException.cannotAddElement(object, container, ex3);
        }
    }
    
    /**
     * INTERNAL:
     * This method is used to calculate the differences between two collections.
     * This algorithm is a work in progress. It works great and all, but like 
     * anything, you can always make it better.
     */
    public void compareCollectionsForChange(Object oldList, Object newList, CollectionChangeRecord changeRecord, AbstractSession session, ClassDescriptor referenceDescriptor) {    
        Vector orderedObjectsToAdd = new Vector();
        Hashtable indicesToRemove = new Hashtable();
        Hashtable oldListIndexValue = new Hashtable();
        IdentityHashMap oldListValueIndex = new IdentityHashMap();
        IdentityHashMap objectsToAdd = new IdentityHashMap();
        IdentityHashtable newListValueIndex = new IdentityHashtable();
        
        // Step 1 - Go through the old list.
        if (oldList != null) {
            ListIterator iterator = (ListIterator)iteratorFor(oldList);
        
            while (iterator.hasNext()) {
                Integer index = new Integer(iterator.nextIndex());
                Object value = iterator.next();
                oldListValueIndex.put(value, index);
                oldListIndexValue.put(index, value);
                indicesToRemove.put(index, index);
            }
        }
            
        // Step 2 - Go though the new list.
        if (newList != null) {
            // Step i - Gather the list info.
            ListIterator iterator = (ListIterator)iteratorFor(newList);
            while (iterator.hasNext()) {
                newListValueIndex.put(iterator.next(), new Integer(iterator.previousIndex()));
            }
        
            // Step ii - Go through the new list again.        
            int index = 0;
            int offset = 0;
            iterator = (ListIterator)iteratorFor(newList);
            while (iterator.hasNext()) {
                index = iterator.nextIndex();
                Object currentObject = iterator.next();
            
                // If value is null then nothing can be done with it.
                if (currentObject != null) {
                    if (oldListValueIndex.containsKey(currentObject)) {
                        int oldIndex = ((Integer) oldListValueIndex.get(currentObject)).intValue();
                        oldListValueIndex.remove(currentObject);
                    
                        if (index == oldIndex) {
                            indicesToRemove.remove(new Integer(oldIndex));
                            offset = 0; // Reset the offset, assume we're back on track.
                        } else if (index == (oldIndex + offset)) {
                            // We're in the right spot according to the offset.
                            indicesToRemove.remove(new Integer(oldIndex));
                        } else {
                            // Time to be clever and figure out why we're not in the right spot!
                            int movedObjects = 0;
                            int deletedObjects = 0;
                            boolean moved = true;
                        
                            if (oldIndex < index) {
                                ++offset;    
                            } else {
                                for (int i = oldIndex - 1; i >= index; i--) {
                                    Object oldObject = oldListIndexValue.get(new Integer(i));
                                    if (newListValueIndex.containsKey(oldObject)) {
                                        ++movedObjects;
                                    } else {
                                        ++deletedObjects;
                                    }
                                }
                            
                                if (index == ((oldIndex + offset) - deletedObjects)) {
                                    // We fell into place because of deleted objects.
                                    offset = offset - deletedObjects;
                                    moved = false;
                                } else if (movedObjects > 1) {
                                    // Assume we moved down, bumping everyone by one. 
                                    ++offset;
                                } else {
                                    // Assume we moved down unless the object that was
                                    // here before is directly beside us.
                                    Object oldObject = oldListIndexValue.get(new Integer(index));
                                
                                    if (newListValueIndex.containsKey(oldObject)) {
                                        if ((((Integer) newListValueIndex.get(oldObject)).intValue() - index) > 1) {
                                            moved = false; // Assume the old object moved up.
                                            --offset; 
                                        }
                                    }
                                }
                            }
                        
                            if (moved) {
                                // Add ourselves to the ordered add list.
                                orderedObjectsToAdd.add(currentObject);
                            } else {
                                // Take us off the removed list.
                                indicesToRemove.remove(new Integer(oldIndex));    
                            }
                        }
                    } else {
                        ++offset;
                        objectsToAdd.put(currentObject, currentObject);
                        orderedObjectsToAdd.add(currentObject);
                    }
                } else {
                    // If we find nulls we need decrease our offset.
                    offset--;
                }
            }
        }
        
        // Sort the remove indices that are left and set the data on the collection change 
        // record to be processed on the merge.
        Vector orderedIndicesToRemove = new Vector(indicesToRemove.values());
        Collections.sort(orderedIndicesToRemove);
        changeRecord.addAdditionChange(objectsToAdd, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
        changeRecord.addRemoveChange(oldListValueIndex, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
        changeRecord.addOrderedAdditionChange(orderedObjectsToAdd, newListValueIndex, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);
        changeRecord.addOrderedRemoveChange(orderedIndicesToRemove, oldListIndexValue, (UnitOfWorkChangeSet) changeRecord.getOwner().getUOWChangeSet(), session);                
    }
    
    /**
     * INTERNAL:
     * Return an list iterator for the given container.
     */
    public Object iteratorFor(Object container) {
        return ((List)container).listIterator();
    }
    
    /**
     * INTERNAL:
     * Merge changes from the source to the target object. Because this is a 
     * collection mapping, values are added to or removed from the collection 
     * based on the change set.
     */
    public void mergeChanges(CollectionChangeRecord changeRecord, Object valueOfTarget, boolean shouldMergeCascadeParts, MergeManager mergeManager, AbstractSession parentSession) {
        ObjectChangeSet objectChanges;
        // Ensure the collection is synchronized while changes are being made,
        // clone also synchronizes on collection (does not have cache key read-lock for indirection).
        // Must synchronize of the real collection as the clone does so.
        Object synchronizedValueOfTarget = valueOfTarget;
        if (valueOfTarget instanceof IndirectCollection) {
            synchronizedValueOfTarget = ((IndirectCollection)valueOfTarget).getDelegateObject();
        }
        synchronized (synchronizedValueOfTarget) {
            // Step 1 - iterate over the removed changes and remove them from the container.
            Vector removedIndices = changeRecord.getOrderedRemoveObjectIndices();

            if (removedIndices.isEmpty()) {
                // Check if we have removed objects via a 
                // simpleRemoveFromCollectionChangeRecord API call.
                Enumeration removedObjects = changeRecord.getRemoveObjectList().keys();
            
                while (removedObjects.hasMoreElements()) {
                    objectChanges = (ObjectChangeSet) removedObjects.nextElement();
                    removeFrom(objectChanges.getOldKey(), objectChanges.getTargetVersionOfSourceObject(mergeManager.getSession()), valueOfTarget, parentSession);
                    registerRemoveNewObjectIfRequired(objectChanges, mergeManager);
                }
            } else {
                for (int i = removedIndices.size() - 1; i >= 0; i--) {
                    Integer index = ((Integer) removedIndices.elementAt(i)).intValue();
                    objectChanges = (ObjectChangeSet) changeRecord.getOrderedRemoveObject(index);;
                    removeFromAtIndex(index, valueOfTarget);
                
                    // The object was actually removed and not moved.
                    if (changeRecord.getRemoveObjectList().containsKey(objectChanges)) {
                        registerRemoveNewObjectIfRequired(objectChanges, mergeManager);
                    }
                }
            }
            
            // Step 2 - iterate over the added changes and add them to the container.
            Enumeration addObjects = changeRecord.getOrderedAddObjects().elements();
            while (addObjects.hasMoreElements()) {
                objectChanges =  (ObjectChangeSet) addObjects.nextElement();
                boolean objectAdded = changeRecord.getAddObjectList().containsKey(objectChanges);
                Object object = null;
                
                // The object was actually added and not moved.
                if (objectAdded && shouldMergeCascadeParts) {
                    object = mergeCascadeParts(objectChanges, mergeManager, parentSession);
                }
                
                if (object == null) {
                    // Retrieve the object to be added to the collection.
                    object = objectChanges.getTargetVersionOfSourceObject(mergeManager.getSession());
                }

                // Assume at this point the above merge will have created a new 
                // object if required and that the object was actually added and 
                // not moved.
                if (objectAdded && mergeManager.shouldMergeChangesIntoDistributedCache()) {
                    // Bugs 4458089 & 4454532 - check if collection contains new item before adding 
                    // during merge into distributed cache					
                    if (! contains(object, valueOfTarget, mergeManager.getSession())) {
                        addIntoAtIndex(changeRecord.getOrderedAddObjectIndex(objectChanges), object, valueOfTarget, mergeManager.getSession());                                
                    }
                } else {
                    addIntoAtIndex(changeRecord.getOrderedAddObjectIndex(objectChanges), object, valueOfTarget, mergeManager.getSession());
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void registerRemoveNewObjectIfRequired(ObjectChangeSet objectChanges, MergeManager mergeManager) {
        if (! mergeManager.shouldMergeChangesIntoDistributedCache()) {
            mergeManager.registerRemovedNewObjectIfRequired(objectChanges.getUnitOfWorkClone());
        }
    }
    
    /**
     * INTERNAL:
     * Remove the element at the specified index.
     */
    protected void removeFromAtIndex(int index, Object container) {
        try {
            ((List) container).remove(index);
        } catch (ClassCastException ex1) {
            throw QueryException.cannotRemoveFromContainer(new Integer(index), container, this);
        } catch (IllegalArgumentException ex2) {
            throw QueryException.cannotRemoveFromContainer(new Integer(index), container, this);
        } catch (UnsupportedOperationException ex3) {
            throw QueryException.cannotRemoveFromContainer(new Integer(index), container, this);
        }
    }
}