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
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.UnsupportedOperationException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.localization.ToStringLocalization;
import org.eclipse.persistence.internal.oxm.schema.model.*;
import org.eclipse.persistence.logging.AbstractSessionLog;

/**
 * <p><b>Purpose<>/b: Generate Java interfaces and implementation classes from a list of SDO Type objects.
 * <ul><li>This class can be run standalone - logging will default to level FINER when run from the command line.</li>
 * </ul>
 */
public class SDOClassGenerator {
    // Source Gen Stuff
    private String indent = SDOConstants.EMPTY_STRING;
    private static int INDENT_TAB = 3;
    private static final String lsep = System.getProperty("line.separator");
    private static final String lsep2 = lsep + lsep;
    private static final String START_PROPERTY_INDEX = "START_PROPERTY_INDEX";
    private Map generatedBuffers;
    private boolean generateInterfaces = true;
    private CodeWriter codeWriter;
    private SDOClassGeneratorListener sdoClassGeneratorListener;

    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDOClassGenerator() {
        this(HelperProvider.getDefaultContext());
    }

    public SDOClassGenerator(HelperContext aContext) {
        aHelperContext = aContext;
        generatedBuffers = new HashMap();
    }

    public static void main(String[] args) {
        // default to dynamic context
        SDOClassGenerator generator = new SDOClassGenerator(new SDOHelperContext());
        String sourceDir = null;
        String sourceFile = null;
        int argsLength = args.length;

        // default to FINEST subject to parameter override
        AbstractSessionLog.getLog().setLevel(AbstractSessionLog.FINER);

        for (int i = 0; i < argsLength; i++) {
            if (args[i].equals("-help")) {
                generator.printUsage(null);
                System.exit(0);
            }
            if (args[i].equals("-sourceFile")) {
                if (i == (argsLength - 1)) {
                    generator.printUsage("sdo_classgenerator_usage_missing_sourcefile_value");
                    System.exit(0);
                }
                sourceFile = args[++i];
            }
            if (args[i].equals("-targetDirectory")) {
                if (i == (argsLength - 1)) {
                    generator.printUsage("sdo_classgenerator_usage_missing_targetdir");
                    System.exit(0);
                }
                sourceDir = args[++i];
            }

            // log level is optional and will default to INFO
            if (args[i].equals("-logLevel")) {
                if (i != (argsLength - 1)) {
                    AbstractSessionLog.getLog().setLevel(Integer.parseInt(args[++i]));
                }
            }
        }
        if (null == sourceFile) {
            generator.printUsage("sdo_classgenerator_usage_missing_sourcefile");
            System.exit(0);
        }

        try {
            FileReader reader = new FileReader(sourceFile);
            FileCodeWriter fileCodeWriter = new FileCodeWriter();
            fileCodeWriter.setSourceDir(sourceDir);
            generator.generate(reader, fileCodeWriter, new DefaultSchemaResolver());

        } catch (IOException e) {
            AbstractSessionLog.getLog().log(AbstractSessionLog.SEVERE, "sdo_classgenerator_exception",//
                                            e.getClass().getName(), e.getLocalizedMessage(), generator.getClass());
            AbstractSessionLog.getLog().logThrowable(AbstractSessionLog.SEVERE, e);
        }
    }

    /**
     * INTERNAL:
     * Print out what usage is missing as well as an example.
     * The messageID parameter must have a corresponding entry in the LoggingLocalizationResource Map
     * @param messageID
     */
    private void printUsage(String messageID) {
        if (null != messageID) {
            System.out.println(ToStringLocalization.buildMessage(messageID, new Object[] { Helper.getShortClassName(getClass()) }));
        }
        System.out.println(ToStringLocalization.buildMessage("sdo_classgenerator_usage_help",//
                                                             new Object[] { Helper.getShortClassName(getClass()) }));
    }

    public Map generate(Reader xsdReader) {
        FileCodeWriter fileCodeWriter = new FileCodeWriter();
        return generate(new StreamSource(xsdReader), fileCodeWriter, new DefaultSchemaResolver());
    }

    public Map generate(Reader xsdReader, String sourceDir) {
        FileCodeWriter fileCodeWriter = new FileCodeWriter();
        fileCodeWriter.setSourceDir(sourceDir);
        return generate(new StreamSource(xsdReader), fileCodeWriter, new DefaultSchemaResolver());
    }

    public Map generate(Reader xsdReader, String sourceDir, SchemaResolver schemaResolver) {
        FileCodeWriter fileCodeWriter = new FileCodeWriter();
        fileCodeWriter.setSourceDir(sourceDir);
        return generate(new StreamSource(xsdReader), fileCodeWriter, schemaResolver);
    }

    public Map generate(Reader xsdReader, SchemaResolver schemaResolver) {
        return generate(new StreamSource(xsdReader), new FileCodeWriter(), schemaResolver);
    }

    public Map generate(Reader xsdReader, CodeWriter aCodeWriter) {
        return generate(new StreamSource(xsdReader), aCodeWriter, new DefaultSchemaResolver());
    }

    public Map generate(Reader xsdReader, CodeWriter aCodeWriter, SchemaResolver schemaResolver) {
        return generate(new StreamSource(xsdReader), aCodeWriter, schemaResolver, true);
    }

    public Map generate(Reader xsdReader, CodeWriter aCodeWriter, SchemaResolver schemaResolver, boolean bProcessImports) {
        return generate(new StreamSource(xsdReader), aCodeWriter, schemaResolver, bProcessImports);
    }

    public Map generate(Source xsdSource, String sourceDir) {
        FileCodeWriter fileCodeWriter = new FileCodeWriter();
        fileCodeWriter.setSourceDir(sourceDir);
        return generate(xsdSource, fileCodeWriter, new DefaultSchemaResolver());
    }

    public Map generate(Source xsdSource, String sourceDir, SchemaResolver schemaResolver) {
        FileCodeWriter fileCodeWriter = new FileCodeWriter();
        fileCodeWriter.setSourceDir(sourceDir);
        return generate(xsdSource, fileCodeWriter, schemaResolver);
    }

    public Map generate(Source xsdSource, SchemaResolver schemaResolver) {
        return generate(xsdSource, new FileCodeWriter(), schemaResolver, true);
    }

    public Map generate(Source xsdSource, CodeWriter aCodeWriter) {
        return generate(xsdSource, aCodeWriter, new DefaultSchemaResolver(), true);
    }

    public Map generate(Source xsdSource, CodeWriter aCodeWriter, SchemaResolver schemaResolver) {
        return generate(xsdSource, aCodeWriter, schemaResolver, true);
    }

    /**
     * @deprecated December 1st, 2006
     */
    public Map generate(Schema schema, CodeWriter aCodeWriter) {
        throw new UnsupportedOperationException();
    }

    public Map generate(Source xsdSource, CodeWriter aCodeWriter, SchemaResolver schemaResolver, boolean bProcessImports) {
        SDOTypesGenerator gen = new SDOTypesGenerator(aHelperContext);
        java.util.List types = gen.define(xsdSource, schemaResolver, true, bProcessImports);
        return generate(aCodeWriter, types);
    }

    public Map generate(CodeWriter aCodeWriter, java.util.List types) {
        generatedBuffers = new HashMap();
        generateInterfaces = true;
        codeWriter = aCodeWriter;

        for (int i = 0; i < types.size(); i++) {
            SDOType theType = (SDOType)types.get(i);
            if (!theType.isDataType()) {
                ClassBuffer nextBuffer = buildClassForType(theType);

                //write interface to file
                String packageDir = nextBuffer.getPackageName();
                packageDir = packageDir.replace('.', '/');

                getCodeWriter().writeInterface(packageDir, nextBuffer.getInterfaceName() + ".java", nextBuffer.getInterfaceBuffer());
                getCodeWriter().writeImpl(packageDir, nextBuffer.getClassName() + ".java", nextBuffer.getClassBuffer());
            }
        }
        return generatedBuffers;

    }

    private ClassBuffer buildClassForType(SDOType sdoType) {
        ClassBuffer classBuffer = new ClassBuffer(sdoClassGeneratorListener);
        classBuffer.setSdoType(sdoType);
        classBuffer.setGenerateInterface(generateInterfaces);
        classBuffer.setSdoTypeName(sdoType.getName());
        StringBuffer currentClassBuffer = new StringBuffer();
        if (sdoClassGeneratorListener != null) {
            sdoClassGeneratorListener.preImplPackage(currentClassBuffer);
        }

        String packageName = null;
        int lastDotIndex = sdoType.getInstanceClassName().lastIndexOf('.');
        if (lastDotIndex > -1) {
            packageName = sdoType.getInstanceClassName().substring(0, lastDotIndex);
        }

        currentClassBuffer.append(indent);
        currentClassBuffer.append("package ");
        if ((sdoType.getURI() != null) && (!sdoType.getURI().equals(SDOConstants.EMPTY_STRING))) {
            classBuffer.setUri(sdoType.getURI());
        }
        currentClassBuffer.append(packageName);
        classBuffer.setPackageName(packageName);

        currentClassBuffer.append(";").append(lsep2);

        if (sdoClassGeneratorListener != null) {
            sdoClassGeneratorListener.preImplImports(currentClassBuffer);
        }

        currentClassBuffer.append("import ").append(//
        SDOConstants.SDO_DATA_OBJECT_IMPL_CLASS_NAME).append(";").append(lsep2);

        java.util.List documentation = (java.util.List)sdoType.get(SDOConstants.DOCUMENTATION_PROPERTY);

        if ((documentation != null) && (documentation.size() > 0)) {
            currentClassBuffer.append(indent);
            currentClassBuffer.append(buildJavaDoc(documentation));
        }
        if (sdoClassGeneratorListener != null) {
            sdoClassGeneratorListener.preImplClass(currentClassBuffer);
        }
        currentClassBuffer.append(indent);
        currentClassBuffer.append("public class ");

        // get the normalized name of the class without the package prefix
        String upperClassName = Helper.getShortClassName(sdoType.getInstanceClassName());
        String interfaceName = upperClassName;
        classBuffer.setInterfaceName(interfaceName);

        String fullClassName = upperClassName + SDOConstants.SDO_IMPL_NAME;
        currentClassBuffer.append(fullClassName + " ");
        classBuffer.setClassName(fullClassName);

        String implExtends = null;
        String interfaceExtends = null;
        if ((sdoType.getBaseTypes() != null) && (sdoType.getBaseTypes().size() > 0)) {
            SDOType baseType = ((SDOType)sdoType.getBaseTypes().get(0));
            if (!baseType.isDataType()) {
                interfaceExtends = baseType.getInstanceClassName();
                implExtends = baseType.getImplClassName();
            }
        } else {
            implExtends = "SDODataObject";
        }

        currentClassBuffer.append("extends ").append(implExtends);
        if (generateInterfaces) {
            currentClassBuffer.append(" implements ")//
            .append(interfaceName);
        }

        classBuffer.getAttributeBuffer().append(buildStartAndEndPropAttribute(sdoType));
        classBuffer.getMethodBuffer().append(buildNoArgCtor(fullClassName));
        currentClassBuffer.append(indent).append(" {").append(lsep2);

        if (generateInterfaces) {
            StringBuffer currentInterfaceBuffer = new StringBuffer();
            if (sdoClassGeneratorListener != null) {
                sdoClassGeneratorListener.preInterfacePackage(currentInterfaceBuffer);
            }
            currentInterfaceBuffer.append(indent);
            currentInterfaceBuffer.append("package ");
            currentInterfaceBuffer.append(packageName);
            currentInterfaceBuffer.append(";").append(lsep2);
            if (sdoClassGeneratorListener != null) {
                sdoClassGeneratorListener.preInterfaceImports(currentInterfaceBuffer);
            }
            if (sdoClassGeneratorListener != null) {
                sdoClassGeneratorListener.preInterfaceClass(currentInterfaceBuffer);
            }
            currentInterfaceBuffer.append(indent);
            currentInterfaceBuffer.append("public interface ");
            currentInterfaceBuffer.append(interfaceName);

            if (interfaceExtends != null) {
                currentInterfaceBuffer.append(" extends " + interfaceExtends);
            }

            currentInterfaceBuffer.append(indent).append(" {").append(lsep2);
            classBuffer.setInterfaceBuffer(currentInterfaceBuffer);
        }
        classBuffer.setClassBuffer(currentClassBuffer);
        getGeneratedBuffers().put(new QName(sdoType.getURI(), sdoType.getName()), classBuffer);

        java.util.List props = sdoType.getDeclaredProperties();
        int propsSize = props.size();
        for (int i = 0; i < propsSize; i++) {
            SDOProperty nextProp = (SDOProperty)props.get(i);
            buildGetterAndSetter(classBuffer, nextProp);
        }
        classBuffer.close();
        return classBuffer;
    }

    private void addJavaDocLinesToBuffer(StringBuffer javaDocBuffer, java.util.List documentationList, boolean getterSetter) {
        for (int i = 0; i < documentationList.size(); i++) {
            String documentation = (String)documentationList.get(i);

            String[] documentationLines = documentation.split(Helper.cr());

            for (int j = 0; j < documentationLines.length; j++) {
                String nextLine = documentationLines[j].trim();
                if (nextLine.length() > 0) {
                    if ((j > 0) || ((j == 0) && !getterSetter)) {
                        javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_LINE);
                    }
                    javaDocBuffer.append(nextLine).append(lsep);
                }
            }
            if (i < (documentationList.size() - 1)) {
                javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_LINE);
            }
        }
    }

    private StringBuffer buildJavaDoc(java.util.List documentation) {
        StringBuffer javaDocBuffer = new StringBuffer();
        javaDocBuffer.append(SDOConstants.JAVADOC_START).append(lsep);
        addJavaDocLinesToBuffer(javaDocBuffer, documentation, false);
        javaDocBuffer.append(SDOConstants.JAVADOC_END).append(lsep);
        return javaDocBuffer;
    }

    private StringBuffer buildGetterJavaDoc(java.util.List documentation, String name) {
        StringBuffer javaDocBuffer = new StringBuffer();
        javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_START).append(lsep);
        javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_LINE).append("Gets ").append(name).append(".").append(lsep);
        javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_LINE).append("return ");
        addJavaDocLinesToBuffer(javaDocBuffer, documentation, true);
        javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_END).append(lsep);
        return javaDocBuffer;
    }

    private StringBuffer buildSetterJavaDoc(java.util.List documentation, String name) {
        StringBuffer javaDocBuffer = new StringBuffer();
        javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_START).append(lsep);
        javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_LINE).append("Sets ").append(name).append(".").append(lsep);
        javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_LINE).append("param value ");
        addJavaDocLinesToBuffer(javaDocBuffer, documentation, true);
        javaDocBuffer.append(indent).append(SDOConstants.JAVADOC_END).append(lsep);
        return javaDocBuffer;
    }

    private StringBuffer buildStartAndEndPropAttribute(SDOType sdoType) {
        StringBuffer attrBuffer = new StringBuffer();
        pushIndent();
        int declPropsSize = sdoType.getDeclaredProperties().size();
        attrBuffer.append(indent).append("public static final int START_PROPERTY_INDEX = ");

        if (sdoType.getBaseTypes().size() > 0) {
            String baseClassName = ((SDOType)sdoType.getBaseTypes().get(0)).getImplClassName();
            attrBuffer.append(baseClassName).append(".END_PROPERTY_INDEX");
            if (declPropsSize > 0) {
                attrBuffer.append(" + 1");
            }
            attrBuffer.append(";");
        } else {
            if (declPropsSize > 0) {
                attrBuffer.append("0;");
            } else {
                attrBuffer.append("-1;");
            }
        }
        attrBuffer.append(lsep2);

        int end = 0;
        if (declPropsSize > 0) {
            end = declPropsSize - 1;
        } else {
            end = 0;
        }
        attrBuffer.append(indent).append("public static final int END_PROPERTY_INDEX = START_PROPERTY_INDEX + ").append(end).append(";").append(lsep2);

        popIndent();
        return attrBuffer;
    }

    private StringBuffer buildNoArgCtor(String className) {
        StringBuffer ctorBuffer = new StringBuffer();
        pushIndent();
        ctorBuffer.append(indent).append("public ").append(className).append("() {}").append(lsep2);
        popIndent();
        return ctorBuffer;
    }

    /**
     * INTERNAL:
     * @param uri
     * @param ownerName
     * @param name
     * @param javaType (always represents the processed javaClass name)
     * @param many
     * @param annotation
     */
    private void buildGetterAndSetter(ClassBuffer classBuffer, SDOProperty property) {
        pushIndent();

        java.util.List documentation = (java.util.List)property.get(SDOConstants.DOCUMENTATION_PROPERTY);

        buildGetMethodBuffer(classBuffer, property, documentation);
        buildSetMethodBuffer(classBuffer, property, documentation);

        popIndent();
    }

    /**
     * INTERNAL:
     * @param classBuffer
     * @param name
     * @param javaType (always represents the processed javaClass name)
     * @param annotation
     * @param className
     */
    private void buildGetMethodBuffer(ClassBuffer classBuffer, SDOProperty property, java.util.List documentation) {
        String returnType = getJavaTypeForProperty(property);

        String methodName = SDOUtil.getMethodName(property.getName());

        if (!((property.getType() == SDOConstants.SDO_CHANGESUMMARY) && methodName.equals("getChangeSummary"))) {
            if ((documentation != null) && (documentation.size() > 0)) {
                classBuffer.getMethodBuffer().append(buildGetterJavaDoc(documentation, property.getName()));
            }
            classBuffer.getMethodBuffer().append(indent);
            classBuffer.getMethodBuffer().append("public ");
            classBuffer.getMethodBuffer().append(returnType).append(" ");

            classBuffer.getMethodBuffer().append(methodName);
            classBuffer.getMethodBuffer().append("() {").append(lsep);
            pushIndent();
            classBuffer.getMethodBuffer().append(indent).append("return ");
            //cast return value        
            String builtIn = getBuiltInType(returnType);

            //TODO: if simple type instead of get() then do getString()
            if (builtIn != null) {
                String wrapperCall = getWrapperCall(returnType);
                if (wrapperCall != null) {
                    classBuffer.getMethodBuffer().append(wrapperCall);
                }

                classBuffer.getMethodBuffer().append("get").append(builtIn).append("(");
                classBuffer.getMethodBuffer().append(START_PROPERTY_INDEX).append(" + ").append(property.getIndexInDeclaredProperties());

                classBuffer.getMethodBuffer().append(")");

                if (wrapperCall != null) {
                    classBuffer.getMethodBuffer().append(")");
                }
            } else {
                if (!returnType.equals(ClassConstants.OBJECT.getName())) {
                    classBuffer.getMethodBuffer().append("(");
                    classBuffer.getMethodBuffer().append(returnType).append(")");
                }
                classBuffer.getMethodBuffer().append("get(");
                classBuffer.getMethodBuffer().append(START_PROPERTY_INDEX).append(" + ").append(property.getIndexInDeclaredProperties());
                classBuffer.getMethodBuffer().append(")");
            }
            classBuffer.getMethodBuffer().append(";").append(lsep);
            popIndent();
            classBuffer.getMethodBuffer().append(indent).append("}").append(lsep2);
        }
        if (generateInterfaces) {
            classBuffer.getInterfaceBuffer().append(indent);
            classBuffer.getInterfaceBuffer().append("public ");
            classBuffer.getInterfaceBuffer().append(returnType).append(" ");
            classBuffer.getInterfaceBuffer().append(methodName);
            classBuffer.getInterfaceBuffer().append("();").append(lsep2);
        }
    }

    /**
     * INTERNAL:
     * @param classBuffer
     * @param name
     * @param javaType (always represents the processed javaClass name)
     * @param annotation
     * @param className
     */
    private void buildSetMethodBuffer(ClassBuffer classBuffer, SDOProperty property, java.util.List documentation) {
        if (property.getType() == SDOConstants.SDO_CHANGESUMMARY) {
            return;
        }

        if ((documentation != null) && (documentation.size() > 0)) {
            classBuffer.getMethodBuffer().append(buildSetterJavaDoc(documentation, property.getName()));
        }

        classBuffer.getMethodBuffer().append(indent);
        classBuffer.getMethodBuffer().append("public void ");
        String methodName = SDOUtil.setMethodName(property.getName());

        classBuffer.getMethodBuffer().append(methodName);
        classBuffer.getMethodBuffer().append("(");

        String paramType = getJavaTypeForProperty(property);

        classBuffer.getMethodBuffer().append(paramType).append(" value");

        classBuffer.getMethodBuffer().append(")").append(" {").append(lsep);
        pushIndent();

        classBuffer.getMethodBuffer().append(indent).append("set(");
        classBuffer.getMethodBuffer().append(START_PROPERTY_INDEX).append(" + ").append(property.getIndexInDeclaredProperties());

        classBuffer.getMethodBuffer().append(" , value)");

        classBuffer.getMethodBuffer().append(";").append(lsep);
        popIndent();
        classBuffer.getMethodBuffer().append(indent).append("}");
        classBuffer.getMethodBuffer().append(lsep2);

        if (generateInterfaces) {
            classBuffer.getInterfaceBuffer().append(indent);
            classBuffer.getInterfaceBuffer().append("public void ");
            classBuffer.getInterfaceBuffer().append(methodName);
            classBuffer.getInterfaceBuffer().append("(");
            classBuffer.getInterfaceBuffer().append(paramType).append(" value");
            classBuffer.getInterfaceBuffer().append(");").append(lsep2);
        }
    }

    private void pushIndent() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < INDENT_TAB; i++) {
            buf.append(" ");
        }
        indent += buf.toString();
    }

    private void popIndent() {
        StringBuffer buf = new StringBuffer();
        int size = indent.length() - INDENT_TAB;
        for (int i = 0; i < size; i++) {
            buf.append(" ");
        }
        indent = buf.toString();
    }

    /**
     * INTERNAL:
     * @param qualifiedName
     * @param targetNamespace
     * @return
     */
    private String getBuiltInType(String typeName) {
        //TODO: check URi
        if ((typeName.equals(ClassConstants.PBOOLEAN.getName())) || (typeName.equals(ClassConstants.BOOLEAN.getName()))) {
            return "Boolean";
        } else if ((typeName.equals(ClassConstants.PBYTE.getName())) || (typeName.equals(ClassConstants.BYTE.getName()))) {
            return "Byte";
        } else if ((typeName.equals(ClassConstants.APBYTE.getName())) || (typeName.equals(ClassConstants.ABYTE.getName()))) {
            return "Bytes";
        } else if ((typeName.equals(ClassConstants.PCHAR.getName())) || (typeName.equals(ClassConstants.CHAR.getName()))) {
            return "Char";
        } else if ((typeName.equals(ClassConstants.PDOUBLE.getName())) || (typeName.equals(ClassConstants.DOUBLE.getName()))) {
            return "Double";
        } else if ((typeName.equals(ClassConstants.PFLOAT.getName())) || (typeName.equals(ClassConstants.FLOAT.getName()))) {
            return "Float";
        } else if ((typeName.equals(ClassConstants.PLONG.getName())) || (typeName.equals(ClassConstants.LONG.getName()))) {
            return "Long";
        } else if ((typeName.equals(ClassConstants.PSHORT.getName())) || (typeName.equals(ClassConstants.SHORT.getName()))) {
            return "Short";
        } else if ((typeName.equals(ClassConstants.PINT.getName())) || (typeName.equals(ClassConstants.INTEGER.getName()))) {
            return "Int";
        } else if (typeName.equals(ClassConstants.STRING.getName())) {
            return "String";
        } else if (typeName.equals(ClassConstants.BIGINTEGER.getName())) {
            return "BigInteger";
        } else if (typeName.equals(ClassConstants.BIGDECIMAL.getName())) {
            return "BigDecimal";
        } else if (typeName.equals(ClassConstants.UTILDATE.getName())) {
            return "Date";
        } else if (typeName.equals("java.util.List")) {
            return "List";
        }

        return null;
    }

    public void setGeneratedBuffers(Map generatedBuffersMap) {
        generatedBuffers = generatedBuffersMap;
    }

    public Map getGeneratedBuffers() {
        return generatedBuffers;
    }

    private String getWrapperCall(String javaType) {
        if (javaType.equals("java.lang.Integer")) {
            return "new Integer(";
        } else if (javaType.equals("java.lang.Boolean")) {
            return "new Boolean(";
        } else if (javaType.equals("java.lang.Short")) {
            return "new Short(";
        } else if (javaType.equals("java.lang.Float")) {
            return "new Float(";
        } else if (javaType.equals("java.lang.Double")) {
            return "new Double(";
        } else if (javaType.equals("java.lang.Byte")) {
            return "new Byte(";
        }
        return null;
    }

    private String getJavaTypeForProperty(Property property) {
        if (property.isMany() || ((SDOType)property.getType()).isXsdList()) {
            return "java.util.List";
        } else {
            Class instanceClass = property.getType().getInstanceClass();
            if (instanceClass.equals(ClassConstants.ABYTE)) {
                return "Byte[]";
            } else if (instanceClass.equals(ClassConstants.APBYTE)) {
                return "byte[]";
            }
            return instanceClass.getName();
        }
    }

    public void setCodeWriter(CodeWriter theCodeWriter) {
        codeWriter = theCodeWriter;
    }

    public CodeWriter getCodeWriter() {
        return codeWriter;
    }

    public void setSDOClassGeneratorListener(SDOClassGeneratorListener listener) {
        sdoClassGeneratorListener = listener;
    }

    public SDOClassGeneratorListener getSDOClassGeneratorListener() {
        return sdoClassGeneratorListener;
    }
}