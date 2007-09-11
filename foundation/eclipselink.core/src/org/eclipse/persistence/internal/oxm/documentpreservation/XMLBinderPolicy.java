/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.documentpreservation;

import java.util.WeakHashMap;

import org.w3c.dom.Node;

import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.documentpreservation.RelativePositionOrderingPolicy;
import org.eclipse.persistence.oxm.mappings.XMLMapping;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide an implementation of DocumentPreservationPolicy that maintains bidirectional
 * relationships between Java Objects and the XMLNodes they originated from.
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement abstract methods from DocumentPreservationPolicy</li>
 * <li>Maintain a map of objects to nodes</li>
 * <li>Maintain the reverse map of nodes to objects</li>
 *
 * @author mmacivor
 *
 */
public class XMLBinderPolicy extends DocumentPreservationPolicy {
    private WeakHashMap nodesToObjects;
    private WeakHashMap objectsToNodes;
    
    public XMLBinderPolicy() {
        nodesToObjects = new WeakHashMap();
        objectsToNodes = new WeakHashMap();
        setNodeOrderingPolicy(new RelativePositionOrderingPolicy());
    }
    
    public void addObjectToCache(Object obj, Node node) {
        addObjectToCache(obj, node, null);
    }
    
    public void addObjectToCache(Object obj, Node node, XMLMapping selfRecordMapping) {
        objectsToNodes.put(obj, node);
        if(selfRecordMapping != null) {
            XMLBinderCacheEntry entry = (XMLBinderCacheEntry)nodesToObjects.get(node);
            if(entry != null) {
                entry.addSelfMappingObject(selfRecordMapping, obj);
            }
        } else {
            XMLBinderCacheEntry entry = new XMLBinderCacheEntry(obj);
            nodesToObjects.put(node, entry);
        }
    }
    
    public Node getNodeForObject(Object obj) {
        return (Node)objectsToNodes.get(obj);
    }
    
    public Object getObjectForNode(Node node) {
        return getObjectForNode(node, null);
    }    
    
    public Object getObjectForNode(Node node, XMLMapping selfRecordMapping) {
        XMLBinderCacheEntry entry = (XMLBinderCacheEntry)nodesToObjects.get(node);
        if(entry != null) {
            if(selfRecordMapping != null) {
                return entry.getSelfMappingObject(selfRecordMapping);
            } else {
                return entry.getRootObject();
            }
        }
        return null;
    }
    public boolean shouldPreserveDocument() {
        return true;
    }
}