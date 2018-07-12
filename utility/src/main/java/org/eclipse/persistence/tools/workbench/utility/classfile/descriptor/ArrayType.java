/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.utility.classfile.descriptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import org.eclipse.persistence.tools.workbench.utility.classfile.Visitor;


/**
 * This class models a class file Descriptor Array Type.
 *
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class ArrayType extends FieldType {
    private FieldType componentType;


    // ********** construction/initialization **********

    ArrayType(Reader reader) throws IOException {
        super();
        this.componentType = FieldType.createFieldType(reader);
    }


    // ********** public API **********

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int arrayDepth() {
        return this.componentType.arrayDepth() + 1;
    }

    @Override
    public String elementTypeName() {
        return this.componentType.elementTypeName();
    }

    @Override
    public String javaName() {
        StringBuffer sb = new StringBuffer();
        this.appendArrayJavaNameTo(sb);
        return sb.toString();
    }

    @Override
    public Class javaClass() throws ClassNotFoundException {
        return Class.forName(this.javaName());
    }

    @Override
    public void appendDeclarationTo(StringBuffer sb) {
        this.componentType.appendDeclarationTo(sb);
        sb.append("[]");
    }

    @Override
    public void printDeclarationOn(PrintWriter writer) {
        this.componentType.printDeclarationOn(writer);
        writer.print("[]");
    }

    @Override
    public String internalName() {
        StringBuffer sb = new StringBuffer();
        this.appendArrayInternalNameTo(sb);
        return sb.toString();
    }


    // ********** internal API **********

    @Override
    void appendArrayJavaNameTo(StringBuffer sb) {
        sb.append('[');
        this.componentType.appendArrayJavaNameTo(sb);
    }

    @Override
    void appendArrayInternalNameTo(StringBuffer sb) {
        sb.append('[');
        this.componentType.appendArrayInternalNameTo(sb);
    }

}
