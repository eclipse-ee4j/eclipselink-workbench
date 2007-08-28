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

import java.util.StringTokenizer;
import javax.xml.namespace.QName;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:  Represents a token from an XPath statement.</p>
 * <p>For example the following XPath statment a/b[2]/text() corresponds to three
 * XPathFragments:  "a", "b[2]", and "text()".</p>
 * <p><b>Responsibilities</b>:<ul>
 * <li>Maintain name, namespace, and prefix information.</li>
 * <li>Maintain information about the corresponding node type.</li>
 * <li>Maintain index information if any.  The XPathFragment corresponding to
 * b[2] would have an index value of 2.</li>
 * </ul>
 */
public class XPathFragment {
    public static final XPathFragment TEXT_FRAGMENT = new XPathFragment("text()");
    public static final XPathFragment SELF_FRAGMENT = new XPathFragment(".");
    public static final XPathFragment ANY_FRAGMENT = null;
    private XPathFragment nextFragment;
    private String xpath;
    private boolean hasAttribute = false;
    private boolean hasText = false;
    private boolean hasNamespace = false;
    private boolean containsIndex = false;
    private int indexValue = -1;//if containsIndex, then this is the value of the index.
    private boolean shouldExecuteSelectNodes = false;
    private String shortName;
    private String prefix;
    private String localName;
    private String namespaceURI;
    private QName qname;
    protected boolean nameIsText = false;
    protected boolean isSelfFragment = false;
    private QName leafElementType;
    private boolean generatedPrefix = false;

    public XPathFragment(String xpathString) {
        setXPath(xpathString);
    }

    public XPathFragment() {
        super();
    }

    public XPathFragment getNextFragment() {
        return nextFragment;
    }

    public void setNextFragment(XPathFragment nextFragment) {
        this.nextFragment = nextFragment;
    }

    public void setXPath(String xpathString) {
        xpath = xpathString;

        // handle case:  company[name/text()="Oracle"]
        if ((xpath.indexOf("[") != -1) && (xpath.indexOf("]") == -1)) {
            setShouldExecuteSelectNodes(true);
            return;
        }

        // handle case:  ancestor::*/jaxb:class/@name
        if (xpath.indexOf("::") != -1) {
            setShouldExecuteSelectNodes(true);
            return;
        }

        shortName = xpathString;
        int attrindex = xpathString.indexOf('@');
        if (attrindex == 0) {
            hasAttribute = true;
            shortName = xpathString.substring(attrindex + 1);
            indexValue = hasIndex(xpathString);
            setupNamespaceInformation(shortName);
            return;
        }
        if (xpathString.startsWith("/")) {
            setShouldExecuteSelectNodes(true);
            shortName = xpathString;
            indexValue = hasIndex(xpathString);
            setupNamespaceInformation(shortName);
            return;
        }
        if (xpathString.equals("text()")) {
            nameIsText = true;
            shortName = xpathString;
            return;
        }

        // handle "self" xpath
        if (xpathString.equals(".")) {
            isSelfFragment = true;
            shortName = xpathString;
            return;
        }

        indexValue = hasIndex(xpathString);
        setupNamespaceInformation(shortName);
    }

    private void setupNamespaceInformation(String xpathString) {
        int nsindex = xpathString.indexOf(':');
        if (nsindex != -1) {
            hasNamespace = true;
            localName = xpathString.substring(nsindex + 1);
            prefix = xpathString.substring(0, nsindex);
        } else {
            localName = xpathString;
        }
    }

    public boolean isAttribute() {
        return hasAttribute;
    }

    public String getShortName() {
        return shortName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        if ("".equals(namespaceURI)) {
            this.namespaceURI = null;
        } else {
            this.namespaceURI = namespaceURI;
        }
    }

    public QName getQName() {
        return qname;
    }

    public void setQName(QName q) {
        qname = q;
    }

    private int hasIndex(String xpathString) {
        int index;
        int startindex = xpathString.lastIndexOf('[');
        if ((startindex != -1) && (xpathString.lastIndexOf(']') != -1)) {
            setContainsIndex(true);
            StringTokenizer st = new StringTokenizer(xpathString, "[]");
            String element = st.nextToken();
            String indexString = st.nextToken();

            try {
                index = Integer.valueOf(indexString).intValue();
            } catch (NumberFormatException e) {
                setShouldExecuteSelectNodes(true);
                index = -1;
            }
            shortName = element;

        } else {
            index = -1;
        }
        return index;
    }

    public int getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(int indexValue) {
        this.indexValue = indexValue;
    }

    public String getXPath() {
        return xpath;
    }

    public boolean hasNamespace() {
        return hasNamespace;
    }

    /**
     * INTERNAL:
     * Indicates if the xpath is "."
     *
     * @return true if the xpath is ".", false otherwise
     */
    public boolean isSelfFragment() {
        return isSelfFragment;
    }

    public boolean nameIsText() {
        return nameIsText;
    }

    public void setHasText(boolean hasText) {
        this.hasText = hasText;
    }

    public boolean getHasText() {
        return hasText;
    }

    public void setContainsIndex(boolean containsIndex) {
        this.containsIndex = containsIndex;
    }

    public boolean containsIndex() {
        return containsIndex;
    }

    public void setShouldExecuteSelectNodes(boolean newShouldExecuteSelectNodes) {
        this.shouldExecuteSelectNodes = newShouldExecuteSelectNodes;
    }

    public boolean shouldExecuteSelectNodes() {
        return shouldExecuteSelectNodes;
    }

    public boolean equals(Object object) {
        try {
            if (this == object) {
                return true;
            }
            XPathFragment xPathFragment = (XPathFragment)object;
            return ((localName == xPathFragment.getLocalName()) || ((localName != null) && localName.equals(xPathFragment.getLocalName()))) && ((namespaceURI == xPathFragment.getNamespaceURI()) || ((namespaceURI != null) && namespaceURI.equals(xPathFragment.getNamespaceURI()))) && (this.indexValue == xPathFragment.getIndexValue()) && (nameIsText == xPathFragment.nameIsText());
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean qNameEquals(Object object) {
        try {
            if (this == object) {
                return true;
            }
            XPathFragment xPathFragment = (XPathFragment)object;
            return ((localName == xPathFragment.getLocalName()) || ((localName != null) && localName.equals(xPathFragment.getLocalName()))) && ((namespaceURI == xPathFragment.getNamespaceURI()) || ((namespaceURI != null) && namespaceURI.equals(xPathFragment.getNamespaceURI()))) && (nameIsText == xPathFragment.nameIsText());
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        int hash = 7;
        hash = (31 * hash) + ((null == localName) ? 0 : localName.hashCode());
        hash = (31 * hash) + ((null == namespaceURI) ? 0 : namespaceURI.hashCode());
        return hash;
    }

    public QName getLeafElementType() {
        return leafElementType;
    }

    public boolean hasLeafElementType() {
        return getLeafElementType() != null;
    }

    public void setLeafElementType(QName type) {
        leafElementType = type;
    }

    public void setGeneratedPrefix(boolean isGenerated) {
        generatedPrefix = isGenerated;
    }

    public boolean isGeneratedPrefix() {
        return generatedPrefix;
    }
}