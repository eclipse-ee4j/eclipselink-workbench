/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.*;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.sessions.Project;

/**
 * <p><b>Purpose</b> SDOSchemaGenerator generates an XSD (returned as a String)
 * from a list of SDO Type objects. Populates an org.eclipse.persistence.internal.oxm.schema.model.Schema
 * object and makes use of org.eclipse.persistence.internal.oxm.schema.SchemaModelProject to marshal
 * the Schema Object to XML.
 * @see commonj.sdo.XSDHelper
 */
public class SDOSchemaGenerator {
    private Map namespaceToSchemaLocation;
    private SchemaLocationResolver schemaLocationResolver;
    private List allTypes;
    private Schema generatedSchema;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOSchemaGenerator(HelperContext aContext) {
        aHelperContext = aContext;
    }

    /**
     * <p>Method to generate an XSD. Note the following:<ul>
     * <li> All types must have same URI
     * <li> Referenced types in same URI will also be generated in schema
     * <li> Includes will never be generated
     * <li> Imports will be generated for referenced types in other URIs
     * </ul>
     * @param types The list of commonj.sdo.Type objects to generate the XSD from
     * @param aSchemaLocationResolver implementation of the org.eclipse.persistence.sdo.helper.SchemaLocationResolver interface
     * used for getting the value of the schemaLocation attribute of generated imports and includes
     * @return String The generated XSD.
     */
    public String generate(List types, SchemaLocationResolver aSchemaLocationResolver) {
        schemaLocationResolver = aSchemaLocationResolver;
        if ((types == null) || (types.size() == 0)) {
            //TODO: throw exception no types given to generate schema
            throw new IllegalArgumentException("No Schema was generated from null or empty list of types.");
        }

        String uri = null;
        Type firstType = (Type)types.get(0);
        if (firstType == null) {
            //TODO: throw exception
        } else {
            uri = firstType.getURI();
        }
        allTypes = types;
        generateSchema(uri, types);

        //TODO: generating xmiversion is optional
        //Now we have a built schema model        
        Project p = new SchemaModelProject();
        Vector generatedNamespaces = generatedSchema.getNamespaceResolver().getNamespaces();
        XMLDescriptor desc = ((XMLDescriptor)p.getDescriptor(Schema.class));
        for (int i = 0; i < generatedNamespaces.size(); i++) {
            Namespace next = (Namespace)generatedNamespaces.get(i);
            desc.getNamespaceResolver().put(next.getPrefix(), next.getNamespaceURI());

            if (next.getNamespaceURI().equals(SDOConstants.SDO_URL) || next.getNamespaceURI().equals(SDOConstants.SDOXML_URL) || next.getNamespaceURI().equals(SDOConstants.SDOJAVA_URL)) {
                if (generatedSchema.getImports().get(next.getNamespaceURI()) == null) {
                    Import theImport = new Import();
                    theImport.setNamespace(next.getNamespaceURI());
                    String schemaLocation = "classpath:/xml/";
                    if (next.getNamespaceURI().equals(SDOConstants.SDO_URL)) {
                        schemaLocation += "sdoModel.xsd";
                    } else if (next.getNamespaceURI().equals(SDOConstants.SDOXML_URL)) {
                        schemaLocation += "sdoXML.xsd";
                    } else if (next.getNamespaceURI().equals(SDOConstants.SDOJAVA_URL)) {
                        schemaLocation += "sdoJava.xsd";
                    }
                    try {
                        new URL(schemaLocation);
                        theImport.setSchemaLocation(schemaLocation);
                    } catch (MalformedURLException e) {
                        // DO NOTHING - fix for bug 6054754 to add custom schemalocation if possible
                    }
                    generatedSchema.getImports().put(next.getNamespaceURI(), theImport);
                }
            }
        }

        XMLLogin login = new XMLLogin();
        login.setDatasourcePlatform(new DOMPlatform());
        p.setDatasourceLogin(login);

        XMLContext context = new XMLContext(p);

        XMLMarshaller marshaller = context.createMarshaller();

        StringWriter generatedSchemaWriter = new StringWriter();
        marshaller.marshal(generatedSchema, generatedSchemaWriter);
        return generatedSchemaWriter.toString();
    }

    /**
     * <p>Method to generate an XSD. Note the following:<ul>
     * <li> All types must have same URI
     * <li> Referenced types in same URI will also be generated in schema
     * <li> Includes will never be generated
     * <li> Imports will be generated for referenced types in other URIs
     * </ul>
     * @param types The list of commonj.sdo.Type objects to generate the XSD from
     * @param aNamespaceToSchemaLocation map of namespaces to schemaLocations
     * used for getting the value of the schemaLocation attribute of generated imports and includes
     * @return String The generated XSD.
    */
    public String generate(List types, Map aNamespaceToSchemaLocation) {
        if ((types == null) || (types.size() == 0)) {
            //TODO: throw exception no types given to generate schema
            throw new IllegalArgumentException("No Schema was generated from null or empty list of types.");
        }

        String uri = null;
        namespaceToSchemaLocation = aNamespaceToSchemaLocation;

        //TODO: write sdo:java 
        Type firstType = (Type)types.get(0);
        if (firstType == null) {
            //TODO: throw exception (case types.size > 0 but each type=null)
            throw new IllegalArgumentException("No Schema was generated from a list of types containing null elements");
        } else {
            uri = firstType.getURI();
        }
        allTypes = types;
        generateSchema(uri, types);

        //TODO: generating xmiversion is optional
        //Now we have a built schema model						      
        Project p = new SchemaModelProject();
        Vector namespaces = generatedSchema.getNamespaceResolver().getNamespaces();
        for (int i = 0; i < namespaces.size(); i++) {
            Namespace next = (Namespace)namespaces.get(i);
            ((XMLDescriptor)p.getDescriptor(Schema.class)).getNamespaceResolver().put(next.getPrefix(), next.getNamespaceURI());
        }

        XMLLogin login = new XMLLogin();
        login.setDatasourcePlatform(new DOMPlatform());
        p.setDatasourceLogin(login);
        XMLContext context = new XMLContext(p);
        XMLMarshaller marshaller = context.createMarshaller();

        StringWriter generatedSchemaWriter = new StringWriter();
        marshaller.marshal(generatedSchema, generatedSchemaWriter);
        return generatedSchemaWriter.toString();
    }

    private void generateSchema(String uri, List typesWithSameUri) {
        //TODO: SDO specific need to handles setting the sdo:javaPackage on the schema 
        //TODO: SDO specific Ensure all types have same javaPackage? Type.getInstanceClass().getPackage().toString()
        generatedSchema = new Schema();
        generatedSchema.setTargetNamespace(uri);
        generatedSchema.setDefaultNamespace(uri);
        //TODO: qualify elements and attributes appropriately based on these settings
        generatedSchema.setAttributeFormDefault(false);
        generatedSchema.setElementFormDefault(true);
        String javaPackage = null;

        for (int i = 0; i < typesWithSameUri.size(); i++) {
            Type nextType = (Type)typesWithSameUri.get(i);
            if ((nextType.getBaseTypes() != null) && (nextType.getBaseTypes().size() > 1)) {
                //TODO: throw an error
                //A schema can not be generated because the following type has more than 1 base type + type
            }
            String nextUri = nextType.getURI();
            if (nextUri != uri) {
                //TODO: throw exception  -- all types must have same uri
            }

            if (!nextType.isDataType()) {
                String fullName = ((SDOType)nextType).getInstanceClassName();
                if (fullName != null) {
                    String nextPackage = null;

                    int lastDot = fullName.lastIndexOf('.');
                    if (lastDot != -1) {
                        nextPackage = fullName.substring(0, lastDot);
                    }

                    if (nextPackage != null) {
                        javaPackage = nextPackage;
                    }
                }
            }
            boolean validName = validateName(nextType.getName());
            if (!validName) {
                //TODO: throw Exception 
            }

            if (nextType.isDataType()) {
                //generate simple type
                SimpleType generatedType = generateSimpleType(nextType);
                generatedSchema.addTopLevelSimpleTypes(generatedType);
            } else {
                //generate complex type
                ComplexType generatedType = generateComplexType(nextType);
                generatedSchema.addTopLevelComplexTypes(generatedType);

                //generate global element for the complex type generated above
                Element element = buildElementForComplexType(generatedSchema, generatedType);
                if (element != null) {
                    generatedSchema.addTopLevelElement(element);
                }
            }
        }
        if (javaPackage != null) {
            getPrefixForURI(SDOConstants.SDOJAVA_URL);
            generatedSchema.getAttributesMap().put(SDOConstants.SDOJAVA_PACKAGE_QNAME, javaPackage);
        }
    }

    private SimpleType generateSimpleType(Type type) {
        SimpleType simpleType = new SimpleType();
        simpleType.setName(type.getName());
        if ((((SDOType)type).getAppInfoElements() != null) && (((SDOType)type).getAppInfoElements().size() > 0)) {
            Annotation annotation = new Annotation();

            annotation.setAppInfo(((SDOType)type).getAppInfoElements());
            simpleType.setAnnotation(annotation);
        }

        //TODO:  simpleType.setAbstractValue(type.isAbstract());
        //SDO specific or not?  spec says no but don't see it in the schema.xsd

        /*TODO: SDO specific generate aliasNames
                 * simpleType.setAliasNames(buildAliasNames(type.getAliasNames()));
        */
        if ((type.getAliasNames() != null) && (type.getAliasNames().size() > 0)) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            String aliasNamesString = buildAliasNameString(type.getAliasNames());
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_ALIASNAME, sdoXmlPrefix);
            simpleType.getAttributesMap().put(qname, aliasNamesString);
        }

        Object value = type.get(SDOConstants.JAVA_CLASS_PROPERTY);
        if ((value != null) && value instanceof String) {
            String sdoJavaPrefix = getPrefixForURI(SDOConstants.SDOJAVA_URL);
            QName qname = new QName(SDOConstants.SDOJAVA_URL, SDOConstants.SDOJAVA_INSTANCECLASS, sdoJavaPrefix);
            simpleType.getAttributesMap().put(qname, value);
        }

        SDOType baseType = null;
        if ((type.getBaseTypes() != null) && (type.getBaseTypes().size() > 0) && ((Type)type.getBaseTypes().get(0) != null)) {
            baseType = (SDOType)type.getBaseTypes().get(0);
            //TODO: need to add something on SimpleType to track referenced uris for includes/imports?
        } else if (type.getInstanceClass() != null) {
            //TODO: Mapping of SDO DataTypes to XSD Built in Data Types"
            //String javaInstanceClass = type.getInstanceClass().getName();
            //getBaseTypeFromTable(type.getInstanceClass());
            //table pg108
        }

        if (baseType != null) {
            Restriction restriction = new Restriction();
            addTypeToListIfNeeded(type, baseType);
            QName schemaType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getXSDTypeFromSDOType(baseType);

            if (schemaType != null) {
                String prefix = getPrefixStringForURI(schemaType.getNamespaceURI());
                restriction.setBaseType(prefix + schemaType.getLocalPart());
            } else {
                String prefix = getPrefixStringForURI(baseType.getURI());
                restriction.setBaseType(prefix + baseType.getName());
            }
            simpleType.setRestriction(restriction);
        }

        return simpleType;
    }

    private ComplexType generateComplexType(Type type) {
        ComplexType complexType = new ComplexType();
        complexType.setName(type.getName());
        complexType.setAbstractValue(type.isAbstract());
        if ((((SDOType)type).getAppInfoElements() != null) && (((SDOType)type).getAppInfoElements().size() > 0)) {
            Annotation annotation = new Annotation();
            annotation.setAppInfo(((SDOType)type).getAppInfoElements());
            complexType.setAnnotation(annotation);
        }

        if ((type.getAliasNames() != null) && (type.getAliasNames().size() > 0)) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            String aliasNamesString = buildAliasNameString(type.getAliasNames());

            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_ALIASNAME, sdoXmlPrefix);
            complexType.getAttributesMap().put(qname, aliasNamesString);
        }

        /* TODO:  SDO Specific
         * complexType.setAliasNames(buildAliasNames(type.getAliasNames()));
         */
        complexType.setMixed(type.isSequenced());
        Type baseType = null;
        if ((type.getBaseTypes() != null) && (type.getBaseTypes().size() > 0) && ((Type)type.getBaseTypes().get(0) != null)) {
            baseType = (Type)type.getBaseTypes().get(0);

            //baseName = base.getName();
            //String baseURI = ((Type)type.getBaseTypes().get(0)).getURI();
            //if (baseURI != type.getURI()) {
            //need to add something  to track referenced uris for includes/imports
            //}
        }

        //TODO: else { //what to do in the else case...spec seems different for simple vs. complex types
        if (baseType != null) {
            addTypeToListIfNeeded(type, baseType);
            Extension extension = new Extension();
            QName schemaType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getXSDTypeFromSDOType(baseType);

            //fixed below for bug5893546
            //TODO: get url for prefix in namespace resolver and map sure it is added to the schema if necessary
            if (schemaType != null) {
                extension.setBaseType(getPrefixStringForURI(schemaType.getNamespaceURI()) + schemaType.getLocalPart());
            } else if ((baseType.getURI() == null) || (baseType.getURI().equalsIgnoreCase(generatedSchema.getTargetNamespace()))) {
                extension.setBaseType(baseType.getName());
            } else {
                //TODO: not the sdo type name but the complex/simple type name corresponding to this sdo type
                extension.setBaseType(getPrefixStringForURI(baseType.getURI()) + baseType.getName());
            }

            buildElementsAndAttributes(extension, type);
            ComplexContent complexContent = new ComplexContent();
            complexContent.setExtension(extension);
            complexType.setComplexContent(complexContent);
            return complexType;
        }

        /*TODO: sequenced If true, the complex type declaration is mixed and the content of the element is placed in a <choice>. If false,
        the complex type contents are placed in a <sequence>. If no local elements are generated,
        the <choice> or <sequence> is suppressed.

        TODO: open:indicates if the type accepts open content, type.open. An <any> is placed in the
        content and <anyAttribute> is placed after the content.*/
        buildElementsAndAttributes(complexType, type);

        return complexType;
    }

    private void buildElementsAndAttributes(Object owner, Type type) {
        List properties = type.getDeclaredProperties();
        NestedParticle nestedParticle = null;

        if ((properties == null) || (properties.size() == 0)) {
            if (type.isOpen()) {
                nestedParticle = new Sequence();
            } else {
                return;
            }
        } else {
            if (type.isSequenced()) {
                nestedParticle = new Choice();
                nestedParticle.setMaxOccurs(Occurs.UNBOUNDED);
            } else {
                nestedParticle = new Sequence();
            }
        }
        for (int i = 0; i < properties.size(); i++) {
            Property nextProperty = (Property)properties.get(i);

            if (aHelperContext.getXSDHelper().isElement(nextProperty)) {
                Element elem = buildElement(nextProperty, nestedParticle);
                nestedParticle.addElement(elem);
            } else if (aHelperContext.getXSDHelper().isAttribute(nextProperty)) {
                Attribute attr = buildAttribute(nextProperty);
                if (owner instanceof ComplexType) {
                    ((ComplexType)owner).getOrderedAttributes().add(attr);
                } else if (owner instanceof Extension) {
                    ((Extension)owner).getOrderedAttributes().add(attr);
                }
            }
        }
        if (type.isOpen()) {
            Any any = new Any();
            any.setProcessContents(AnyAttribute.LAX);
            any.setMaxOccurs(Occurs.UNBOUNDED);
            nestedParticle.addAny(any);

            AnyAttribute anyAttribute = new AnyAttribute();
            anyAttribute.setProcessContents(AnyAttribute.LAX);
            if (owner instanceof ComplexType) {
                ((ComplexType)owner).setAnyAttribute(anyAttribute);
            }
        }

        if (!nestedParticle.isEmpty()) {
            if (owner instanceof ComplexType) {
                ((ComplexType)owner).setTypeDefParticle((TypeDefParticle)nestedParticle);
                //baseType.getAttributes().add(attr);
            } else if (owner instanceof Extension) {
                ((Extension)owner).setTypeDefParticle((TypeDefParticle)nestedParticle);
            }
        }
    }

    private void addSimpleComponentAnnotations(SimpleComponent sc, Property property, boolean element) {
        if (property.isReadOnly()) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_READONLY, sdoXmlPrefix);
            sc.getAttributesMap().put(qname, "true");
        }
        if ((property.getAliasNames() != null) && (property.getAliasNames().size() > 0)) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            String aliasNamesString = buildAliasNameString(property.getAliasNames());
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_ALIASNAME, sdoXmlPrefix);
            sc.getAttributesMap().put(qname, aliasNamesString);
        }
        if ((element && !property.isContainment() && !property.getType().isDataType()) || (!element && !property.getType().isDataType())) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            String uri = property.getType().getURI();
            String value = property.getType().getName();
            if (uri != null) {
                String typePrefix = getPrefixForURI(uri);
                if (typePrefix != null) {
                    value = typePrefix + ":" + value;
                }
            }
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_PROPERTYTYPE, sdoXmlPrefix);
            sc.getAttributesMap().put(qname, value);
        }

        if (property.getOpposite() != null) {
            String value = property.getOpposite().getName();
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_OPPOSITEPROPERTY, sdoXmlPrefix);
            sc.getAttributesMap().put(qname, value);
        }

        Type dataType = (Type)property.get(SDOConstants.XMLDATATYPE_PROPERTY);
        if (dataType == null) {
            dataType = getAutomaticDataTypeForType(property.getType());
        }
        if (dataType != null) {
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_DATATYPE, sdoXmlPrefix);
            String dataTypeString = dataType.getName();
            if (dataType.getURI() != null) {
                String dataTypePrefix = getPrefixForURI(dataType.getURI());
                if (dataTypePrefix != null) {
                    dataTypeString = dataTypePrefix + ":" + dataTypeString;
                }
            }
            sc.getAttributesMap().put(qname, dataTypeString);
        }

        if (element) {
            String mimeType = (String)property.get(SDOConstants.MIME_TYPE_PROPERTY);
            if (mimeType != null) {
                String prefix = getPrefixForURI(SDOConstants.MIMETYPE_URL);
                QName qname = new QName(SDOConstants.XML_MIME_TYPE_QNAME.getNamespaceURI(), SDOConstants.XML_MIME_TYPE_QNAME.getLocalPart(), prefix);
                sc.getAttributesMap().put(qname, mimeType);
            } else {
                mimeType = (String)property.get(SDOConstants.MIME_TYPE_PROPERTY_PROPERTY);
                if (mimeType != null) {
                    String prefix = getPrefixForURI(SDOConstants.ORACLE_SDO_URL);
                    QName qname = new QName(SDOConstants.XML_MIME_TYPE_PROPERTY_QNAME.getNamespaceURI(), SDOConstants.XML_MIME_TYPE_PROPERTY_QNAME.getLocalPart(), prefix);
                    sc.getAttributesMap().put(qname, mimeType);
                }
            }
        }
    }

    private String buildAliasNameString(List aliasNames) {
        String aliasNamesString = new String();
        int size = aliasNames.size();
        for (int i = 0; i < size; i++) {
            String nextName = (String)aliasNames.get(i);
            aliasNamesString += nextName;
            if (i < (size - 1)) {
                aliasNamesString += " ";
            }
        }
        return aliasNamesString;
    }

    private Element buildElement(Property property, NestedParticle nestedParticle) {
        Element elem = new Element();
        elem.setName(property.getName());
        elem.setMinOccurs(Occurs.ZERO);
        elem.setNillable(property.isNullable());
        if ((((SDOProperty)property).getAppInfoElements() != null) && (((SDOProperty)property).getAppInfoElements().size() > 0)) {
            Annotation annotation = new Annotation();
            annotation.setAppInfo(((SDOProperty)property).getAppInfoElements());
            elem.setAnnotation(annotation);
        }

        // process default values that are defined in the schema (not via primitive numeric Object wrapped pseudo defaults)
        if (((SDOProperty)property).isDefaultSet()) {
            if (!property.isMany() && property.getType().isDataType()) {
                XMLConversionManager xmlConversionManager = (XMLConversionManager)((SDOXMLHelper)aHelperContext.getXMLHelper()).getXmlContext().getSession(0).getDatasourcePlatform().getConversionManager();
                elem.setDefaultValue((String)xmlConversionManager.convertObject(property.getDefault(), ClassConstants.STRING, ((SDOProperty)property).getXsdType()));
            }

            /*TODO: is property.default and is produced if the default is not null and the default
            differs from the XSD default for that data type .
            */
        }

        /*TODO: SDO specific generate aliasNames
                * elem.setAliasNames(buildAliasNames(property.getAliasNames()));
        */
        //TODO: SDO Specific opposite setting property.getOpposite
        //TODO: SDO Specific readonly setting property.readOnly
        //TOD: update containment
        addSimpleComponentAnnotations(elem, property, true);

        /*
         When containment is true, then DataObjects of that Type will appear as nested elements in an XML document.
        When containment is false and the property's type is a DataObject, a URI reference
        to the element containing the DataObject is used and an sdo:propertyType
        declaration records the target type. Values in XML documents will be of the form
        "#xpath" where the xpath is an SDO DataObject XPath subset. It is typical to
        customize the declaration to IDREF if the target element has an attribute with type
        customized to ID.

        [TYPE.NAME] is the type of the element. If property.type.dataType is true,
        [TYPE.NAME] is the name of the XSD built in SimpleType corresponding to
        property.type, where the prefix is for the xsd namespace. Otherwise,
        [TYPE.NAME] is property.type.name where the tns: prefix is determined by the
        namespace declaration for the Type's URI.
         */
        Type schemaSDOType = null;
        QName schemaType = ((SDOProperty)property).getXsdType();
        if (schemaType != null) {
            schemaSDOType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getType(schemaType.getNamespaceURI(), schemaType.getLocalPart());
        }
        if (!property.isContainment() && !property.getType().isDataType()) {
            schemaType = SDOConstants.ANY_URI_QNAME;
        }

        Type propertyType = property.getType();

        //if (property.isContainment() && (propertyType != null)) {
        if (propertyType != null) {
            if (property.getContainingType() != null) {
                addTypeToListIfNeeded(property.getContainingType(), propertyType);
            }

            //TODO: prefix not currently set on type
            //elem.setType(property.getType().getName());
            //TODO: SDO Specific readonly setting property.readOnly
            if (schemaType == null) {
                schemaType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getXSDTypeFromSDOType(propertyType);
            }

            //get url for prefix in namespace resolver and map sure it is added to the schema if necessary
            //TODO: types
            if (schemaType != null) {
                elem.setType(getPrefixStringForURI(schemaType.getNamespaceURI()) + schemaType.getLocalPart());
                if (schemaSDOType != null) {
                    addTypeToListIfNeeded(property.getContainingType(), schemaSDOType);
                }
            } else if ((propertyType.getURI() == null) || (propertyType.getURI().equalsIgnoreCase(generatedSchema.getTargetNamespace()))) {
                elem.setType(propertyType.getName());
            } else {
                //TODO: not the sdo type name but the complex/simple type name corresponding to this sdo type
                elem.setType(getPrefixStringForURI(propertyType.getURI()) + propertyType.getName());
            }
        } else {
            elem.setType("anyURI");
        }
        if (property.isMany()) {
            elem.setMaxOccurs(Occurs.UNBOUNDED);
        } else if (nestedParticle.getMaxOccurs() == Occurs.UNBOUNDED) {
            //this means property.isMany==false and the owning sequence of choice is unbounded Jira SDO-3
            String sdoXmlPrefix = getPrefixForURI(SDOConstants.SDOXML_URL);
            QName qname = new QName(SDOConstants.SDOXML_URL, SDOConstants.SDOXML_MANY, sdoXmlPrefix);
            elem.getAttributesMap().put(qname, "false");
        }

        return elem;
    }

    private Attribute buildAttribute(Property property) {
        Attribute attr = new Attribute();
        attr.setName(property.getName());
        if ((((SDOProperty)property).getAppInfoElements() != null) && (((SDOProperty)property).getAppInfoElements().size() > 0)) {
            Annotation annotation = new Annotation();
            annotation.setAppInfo(((SDOProperty)property).getAppInfoElements());
            attr.setAnnotation(annotation);
        }

        // process default values that are defined in the schema (not via primitive numeric Object wrapped pseudo defaults)
        if (((SDOProperty)property).isDefaultSet()) {
            if (!property.isMany() && property.getType().isDataType()) {
                XMLConversionManager xmlConversionManager = (XMLConversionManager)((SDOXMLHelper)aHelperContext.getXMLHelper()).getXmlContext().getSession(0).getDatasourcePlatform().getConversionManager();
                attr.setDefaultValue((String)xmlConversionManager.convertObject(property.getDefault(), ClassConstants.STRING, ((SDOProperty)property).getXsdType()));
            }

            /*TODO: is property.default and is produced if the default is not null and the default
            differs from the XSD default for that data type .
            */
        }
        addSimpleComponentAnnotations(attr, property, false);

        //TODO: SDO specific read only setting
        //TODO: SDO specific opposite setting

        /*TODO: SDO specific generate aliasNames
        * attr.setAliasNames(buildAliasNames(property.getAliasNames()));
        */
        Type propertyType = property.getType();
        QName schemaType = ((SDOProperty)property).getXsdType();
        if (!propertyType.isDataType()) {
            schemaType = SDOConstants.ANY_URI_QNAME;
        }

        if (propertyType != null) {
            if (property.getContainingType() != null) {
                addTypeToListIfNeeded(property.getContainingType(), propertyType);
            }
            if (schemaType == null) {
                schemaType = ((SDOTypeHelper)aHelperContext.getTypeHelper()).getXSDTypeFromSDOType(propertyType);
            }
            if (schemaType != null) {
                attr.setType(getPrefixStringForURI(schemaType.getNamespaceURI()) + schemaType.getLocalPart());
            } else if ((propertyType.getURI() == null) || (propertyType.getURI().equalsIgnoreCase(generatedSchema.getTargetNamespace()))) {
                attr.setType(propertyType.getName());
            } else {
                //TODO: not the sdo type name but the complex/simple type name corresponding to this sdo type
                attr.setType(getPrefixStringForURI(propertyType.getURI()) + propertyType.getName());
            }

            //get url for prefix in namespace resolver and map sure it is added to the schema if necessary
            //TODO: types

            /*
            if (schemaType != null) {
            attr.setType(getPrefixStringForURI(schemaType.getNamespaceURI()) + schemaType.getLocalPart());
            } else {
            attr.setType(propertyType.getName());
            }*/
        }

        return attr;
    }

    private void addTypeToListIfNeeded(Type sourceType, Type targetType) {
        if ((targetType.getURI() != null) && !targetType.getURI().equals(SDOConstants.SDO_URL) && !targetType.getURI().equals(SDOConstants.SDOJAVA_URL) && !targetType.getURI().equals(SDOConstants.SDOXML_URL)) {
            boolean alreadyGenerated = allTypes.contains(targetType);
            String schemaLocation = null;
            if (namespaceToSchemaLocation != null) {
                schemaLocation = (String)namespaceToSchemaLocation.get(targetType.getURI());

                if (targetType.getURI().equals(generatedSchema.getTargetNamespace())) {
                    if (!alreadyGenerated) {
                        allTypes.add(targetType);
                    }
                } else {
                    Import theImport = (Import)generatedSchema.getImports().get(schemaLocation);
                    if (theImport == null) {
                        theImport = new Import();
                        theImport.setSchemaLocation(schemaLocation);
                        theImport.setNamespace(targetType.getURI());
                        generatedSchema.getImports().put(schemaLocation, theImport);
                    }
                }
            } else if (schemaLocationResolver != null) {
                schemaLocation = schemaLocationResolver.resolveSchemaLocation(sourceType, targetType);
                if (schemaLocation != null) {
                    if (targetType.getURI().equals(generatedSchema.getTargetNamespace())) {
                        Include include = (Include)generatedSchema.getIncludes().get(schemaLocation);
                        if (include == null) {
                            include = new Include();
                            include.setSchemaLocation(schemaLocation);
                            generatedSchema.getIncludes().put(schemaLocation, include);
                            // 20060713 remove type from List of types when adding an include
                            allTypes.remove(targetType);
                        }
                    } else {
                        Import theImport = (Import)generatedSchema.getImports().get(schemaLocation);
                        if (theImport == null) {
                            theImport = new Import();
                            theImport.setSchemaLocation(schemaLocation);
                            theImport.setNamespace(targetType.getURI());
                            generatedSchema.getImports().put(schemaLocation, theImport);
                        }
                    }
                } else {
                    if (!alreadyGenerated) {
                        //we can #1 add to list of allTypes or #2 make an appropriate include
                        if (targetType.getURI().equals(generatedSchema.getTargetNamespace())) {
                            allTypes.add(targetType);
                        }
                    }
                }
            }
        }
    }

    private boolean validateName(String nameToValidate) {
        //TODO: make sure nameToValidate is a valid xsd name
        return true;
    }

    private Element buildElementForComplexType(Schema schema, ComplexType type) {
        //TODO: conflict when complextype "Test" then complextype "test" generated
        Element elem = new Element();
        String name = type.getName();
        if (name == null) {
            return null;
        }
        String lowerName = Character.toLowerCase(name.charAt(0)) + name.substring(1, name.length());

        Object exists = schema.getTopLevelElements().get(lowerName);
        if (exists != null) {
            //if lower case name already exists then 
            //TODO: if my first letter was originally lowercase then my name should be lower name and
            //exists type should be modified to have uppercase name
            //TODO: else keep the uppercase name if
            elem.setName(name);
        } else {
            elem.setName(lowerName);
        }

        //TODO: probably need to generate prefix:type.getName() from qname
        elem.setType(type.getName());

        return elem;
    }

    private String getPrefixStringForURI(String uri) {
        String prefix = getPrefixForURI(uri);
        if (prefix == null) {
            return "";
        } else {
            return prefix + ":";
        }
    }

    private String getPrefixForURI(String uri) {
        String prefix = null;
        if (uri.equals(generatedSchema.getTargetNamespace())) {
            return null;
        } else if (uri.equals(XMLConstants.SCHEMA_URL)) {
            return XMLConstants.SCHEMA_PREFIX;
        } else if (uri.equals(SDOConstants.SDO_URL)) {
            prefix = generatedSchema.getNamespaceResolver().resolveNamespaceURI(uri);
            if (prefix == null) {
                prefix = generatedSchema.getNamespaceResolver().generatePrefix(SDOConstants.SDO_PREFIX);
                generatedSchema.getNamespaceResolver().put(prefix, uri);
            }
        } else if (uri.equals(SDOConstants.SDOJAVA_URL)) {
            prefix = generatedSchema.getNamespaceResolver().resolveNamespaceURI(uri);
            if (prefix == null) {
                prefix = generatedSchema.getNamespaceResolver().generatePrefix(SDOConstants.SDOJAVA_PREFIX);
                generatedSchema.getNamespaceResolver().put(prefix, uri);
            }
        } else if (uri.equals(SDOConstants.SDOXML_URL)) {
            prefix = generatedSchema.getNamespaceResolver().resolveNamespaceURI(uri);
            if (prefix == null) {
                prefix = generatedSchema.getNamespaceResolver().generatePrefix(SDOConstants.SDOXML_PREFIX);
                generatedSchema.getNamespaceResolver().put(prefix, uri);
            }
        }
        if (prefix == null) {
            prefix = generatedSchema.getNamespaceResolver().resolveNamespaceURI(uri);
        }
        if (prefix != null) {
            return prefix;
        } else {
            String generatedPrefix = generatedSchema.getNamespaceResolver().generatePrefix();
            generatedSchema.getNamespaceResolver().put(generatedPrefix, uri);
            return generatedPrefix;
        }
    }

    private Type getAutomaticDataTypeForType(Type theType) {
        // Section 10.1 of the spec 
        //For the SDO Java Types, the corresponding base SDO Type is used. For the SDO Java
        // Types, and for SDO Date, an sdo:dataType annotation is generated on the XML attribute
        // or element referring to the SDO Type.
        if (theType == SDOConstants.SDO_BOOLEANOBJECT) {
            return SDOConstants.SDO_BOOLEANOBJECT;
        } else if (theType == SDOConstants.SDO_BYTEOBJECT) {
            return SDOConstants.SDO_BYTEOBJECT;
        } else if (theType == SDOConstants.SDO_CHARACTEROBJECT) {
            return SDOConstants.SDO_CHARACTEROBJECT;
        } else if (theType == SDOConstants.SDO_DOUBLEOBJECT) {
            return SDOConstants.SDO_DOUBLEOBJECT;
        } else if (theType == SDOConstants.SDO_INTOBJECT) {
            return SDOConstants.SDO_INTOBJECT;
        } else if (theType == SDOConstants.SDO_FLOATOBJECT) {
            return SDOConstants.SDO_FLOATOBJECT;
        } else if (theType == SDOConstants.SDO_LONGOBJECT) {
            return SDOConstants.SDO_LONGOBJECT;
        } else if (theType == SDOConstants.SDO_SHORTOBJECT) {
            return SDOConstants.SDO_SHORTOBJECT;
        } else if (theType == SDOConstants.SDO_DATE) {
            return SDOConstants.SDO_DATE;
        } else if (theType == SDOConstants.SDO_DATETIME) {
            return SDOConstants.SDO_DATETIME;
        }
        return null;
    }
}