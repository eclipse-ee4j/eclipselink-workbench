/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;

public class Schema {
    private String name;//non-persistant, used to give a schema an identifier
    private Map imports;
    private Map includes;
    private String targetNamespace;
    private String defaultNamespace;

    //private String token;
    private boolean elementFormDefault;//error mapping in mw
    private boolean attributeFormDefault;//error mapping in mw
    private Map topLevelSimpleTypes;
    private Map topLevelComplexTypes;
    private Map topLevelElements;
    private Map topLevelAttributes;
    private NamespaceResolver namespaceResolver;
    private Map attributesMap;
    private Map attributeGroups;
    private Map groups;
    private Annotation annotation;

    public Schema() {
        namespaceResolver = new NamespaceResolver();
        imports = new HashMap();
        includes = new HashMap();
        topLevelSimpleTypes = new HashMap();
        topLevelComplexTypes = new HashMap();
        topLevelElements = new HashMap();
        topLevelAttributes = new HashMap();
        attributesMap = new HashMap();
        attributeGroups = new HashMap();
        groups = new HashMap();
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
        //setDefaultNamespace(targetNamespace);//temp until full NS support is added
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public String getDefaultNamespace() {
        return this.defaultNamespace;
    }

    /*
        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
        */
    public void setTopLevelSimpleTypes(Map topLevelSimpleTypes) {
        this.topLevelSimpleTypes = topLevelSimpleTypes;
    }

    public Map getTopLevelSimpleTypes() {
        return topLevelSimpleTypes;
    }

    public void addTopLevelSimpleTypes(SimpleType simpleType) {
        topLevelSimpleTypes.put(simpleType.getName(), simpleType);
    }

    public void setTopLevelComplexTypes(Map topLevelComplexTypes) {
        this.topLevelComplexTypes = topLevelComplexTypes;
    }

    public Map getTopLevelComplexTypes() {
        return topLevelComplexTypes;
    }

    public void addTopLevelComplexTypes(ComplexType complexType) {
        topLevelComplexTypes.put(complexType.getName(), complexType);
    }

    public void setTopLevelElements(Map topLevelElements) {
        this.topLevelElements = topLevelElements;
    }

    public Map getTopLevelElements() {
        return topLevelElements;
    }

    public void addTopLevelElement(Element element) {
        topLevelElements.put(element.getName(), element);
    }

    public void setElementFormDefault(boolean elementFormDefault) {
        this.elementFormDefault = elementFormDefault;
    }

    public boolean isElementFormDefault() {
        return elementFormDefault;
    }

    public void setAttributeFormDefault(boolean attributeFormDefault) {
        this.attributeFormDefault = attributeFormDefault;
    }

    public boolean isAttributeFormDefault() {
        return attributeFormDefault;
    }

    public void setTopLevelAttributes(Map topLevelAttributes) {
        this.topLevelAttributes = topLevelAttributes;
    }

    public Map getTopLevelAttributes() {
        return topLevelAttributes;
    }

    public void setNamespaceResolver(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    public void setImports(Map imports) {
        this.imports = imports;
    }

    public Map getImports() {
        return imports;
    }

    public void setIncludes(Map includes) {
        this.includes = includes;
    }

    public Map getIncludes() {
        return includes;
    }

    public void setAttributesMap(Map attributesMap) {
        this.attributesMap = attributesMap;
        Iterator iter = attributesMap.keySet().iterator();
        while (iter.hasNext()) {
            QName key = (QName)iter.next();
            if (key.getNamespaceURI().equals(XMLConstants.XMLNS_URL)) {
                String value = (String)attributesMap.get(key);
                String prefix = key.getLocalPart();
                int index = prefix.indexOf(':');
                if (index > -1) {
                    prefix = prefix.substring(index + 1, prefix.length());
                }
                namespaceResolver.put(prefix, value);
                //TODO: remove from attributes map?
            }
        }
    }

    public Map getAttributesMap() {
        return attributesMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAttributeGroups(Map attributeGroups) {
        this.attributeGroups = attributeGroups;
    }

    public Map getAttributeGroups() {
        return attributeGroups;
    }

    public AttributeGroup getAttributeGroup(String uri, String localName) {
        AttributeGroup globalAttributeGroup = null;
        if (uri.equals(targetNamespace)) {
            globalAttributeGroup = (AttributeGroup)getAttributeGroups().get(localName);
            if (globalAttributeGroup != null) {
                return globalAttributeGroup;
            }
        }
        globalAttributeGroup = getAttributeGroupFromReferencedSchemas(uri, localName);

        return globalAttributeGroup;
    }

    protected AttributeGroup getAttributeGroupFromReferencedSchemas(String uri, String localName) {
        AttributeGroup globalAttributeGroup = null;
        Iterator iter = getIncludes().values().iterator();
        while (iter.hasNext() && (globalAttributeGroup == null)) {
            Schema includedSchema = ((Include)iter.next()).getSchema();
            globalAttributeGroup = includedSchema.getAttributeGroup(uri, localName);
        }
        if (globalAttributeGroup == null) {
            iter = getImports().values().iterator();
            while (iter.hasNext() && (globalAttributeGroup == null)) {
                Schema importedSchema = ((Import)iter.next()).getSchema();
                globalAttributeGroup = importedSchema.getAttributeGroup(uri, localName);
            }
        }
        return globalAttributeGroup;
    }

    public void setGroups(Map groups) {
        this.groups = groups;
    }

    public Map getGroups() {
        return groups;
    }

    public Group getGroup(String uri, String localName) {
        Group globalGroup = null;
        if (uri.equals(targetNamespace)) {
            globalGroup = (Group)getGroups().get(localName);
            if (globalGroup != null) {
                return globalGroup;
            }
        }
        globalGroup = getGroupFromReferencedSchemas(uri, localName);

        return globalGroup;
    }

    protected Group getGroupFromReferencedSchemas(String uri, String localName) {
        Group globalGroup = null;
        Iterator iter = getIncludes().values().iterator();
        while (iter.hasNext() && (globalGroup == null)) {
            Schema includedSchema = ((Include)iter.next()).getSchema();
            globalGroup = includedSchema.getGroup(uri, localName);
        }
        if (globalGroup == null) {
            iter = getImports().values().iterator();
            while (iter.hasNext() && (globalGroup == null)) {
                Schema importedSchema = ((Import)iter.next()).getSchema();
                globalGroup = importedSchema.getGroup(uri, localName);
            }
        }
        return globalGroup;
    }
}