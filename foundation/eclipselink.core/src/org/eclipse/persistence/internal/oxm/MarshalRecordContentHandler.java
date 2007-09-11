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

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This class is used to tranfer SAX events to a TopLink OXM 
 * Marshal Record. Used by XMLFragment mappings to write peices of XML to a 
 * marshal record.</p>
 */

public class MarshalRecordContentHandler implements ContentHandler {
    private MarshalRecord marshalRecord;
    private NamespaceResolver resolver;

    public MarshalRecordContentHandler(MarshalRecord record, NamespaceResolver resolver) {
        marshalRecord = record;        
    }

    public void setMarshalRecord(MarshalRecord record) {
        this.marshalRecord = record;
    }

    public MarshalRecord getMarshalRecord() {
        return marshalRecord;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        XPathFragment frag = new XPathFragment(qName);
        marshalRecord.openStartElement(frag, resolver);
        for (int i = 0; i < atts.getLength(); i++) {
            marshalRecord.attribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getValue(i));
        }
        marshalRecord.closeStartElement();
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        XPathFragment frag = new XPathFragment(qName);
        marshalRecord.endElement(frag, resolver);
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        marshalRecord.startPrefixMapping(prefix, uri);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        //refContentHandler.processingInstruction(target, data);
    }

    public void setDocumentLocator(Locator locator) {
        //refContentHandler.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
        //do nothing.  startDocument() on the content handler is not triggered when fragment on XMLMarshaller is true
    }

    public void endDocument() throws SAXException {
        //do nothing.  startDocument() on the content handler is not triggered when fragment on XMLMarshaller is true
    }

    public void skippedEntity(String name) throws SAXException {
        //refContentHandler.skippedEntity(name);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        marshalRecord.endPrefixMapping(prefix);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        //refContentHandler.ignorableWhitespace(ch, start, length);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        String characters = new String(ch, start, length);
        if (characters.trim().length() > 0) {
            marshalRecord.characters(characters);
        }
    }
}