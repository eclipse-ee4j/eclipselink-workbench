/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.xml.sax.Attributes;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the Optional Node Null Policy is handled when
 * used with the TreeObjectBuilder.</p>
 */

public class OptionalNodeValue extends NodeValue {

    private NullCapableValue nullCapableValue;
    
    public OptionalNodeValue(NullCapableValue aNullCapableValue) {
        super();
        this.nullCapableValue = aNullCapableValue;
    }

    public boolean isMarshalOnlyNodeValue() {
    	return true;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
    	// we are only supporting direct marshalling for now
   		return false;
    }
    
    /**
     * During an unmarshal event we output an empty array for a null value
     */
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
    	unmarshalRecord.getNullCapableValues().add(this.nullCapableValue);
    	return true;
    }
    
}