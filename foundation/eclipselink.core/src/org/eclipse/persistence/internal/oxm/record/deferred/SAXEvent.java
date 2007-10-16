/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.internal.oxm.record.deferred;

import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.xml.sax.SAXException;

/**
 * <p><b>Purpose</b>: Abstract class to represent an event from a ContentHandler or LexicalHandler 
 * <p><b>Responsibilities</b>:<ul>
 * <li> Give an unmarshalRecord the processEvent excecuts the appropriate method no the unmarshalRecord.
 * </ul>
 */
public abstract class SAXEvent {
    public abstract void processEvent(UnmarshalRecord unmarshalRecord) throws SAXException;
}