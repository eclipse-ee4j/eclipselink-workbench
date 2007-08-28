/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.record;

import java.util.Stack;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

/**
 *  @version $Header: XMLTransformationRecord.java 09-aug-2007.15:35:19 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class XMLTransformationRecord extends DOMRecord {
    private UnmarshalRecord owningRecord;
    private NamespaceResolver resolver;
    public XMLTransformationRecord(UnmarshalRecord owner) {
        super();
        owningRecord = owner;
        initializeNamespaceMaps();
    }
    
    public XMLTransformationRecord(String rootName, UnmarshalRecord owner) {
        super(rootName);
        owningRecord = owner;
        resolver = new NamespaceResolver();
        initializeNamespaceMaps();
    }
    public String resolveNamespacePrefix(String prefix) {
        return resolver.resolveNamespacePrefix(prefix);
    }    
    
    public void initializeNamespaceMaps() {
        //When the transformation record is created, initialize the namespace resolver 
        //to contain the namespaces from the current state of the owning record.
        //Start at the root and work down.
        Stack records = new Stack();
        UnmarshalRecord next = owningRecord;
        while(next != null) {
            records.push(next);
            next = next.getParentRecord();
        }
        for(int i = 0; i < records.size(); i++) {
            next = (UnmarshalRecord)records.pop();
            if(next.getNamespaceMap() != null) {
                java.util.Iterator prefixes = next.getNamespaceMap().keySet().iterator();
                while(prefixes.hasNext()) {
                    String prefix = (String)prefixes.next();
                    this.resolver.put(prefix, (String)next.getNamespaceMap().get(prefix));
                }
            }
        }
        
    }
}