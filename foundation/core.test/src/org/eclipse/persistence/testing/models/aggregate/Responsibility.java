/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.indirection.*;

public class Responsibility implements Serializable {
    public Number id;
    public String responsibility;
    public ValueHolderInterface employee;

    public Responsibility() {
        employee = new ValueHolder();
    }

    public static Responsibility example1(Employee anEmployee) {
        Responsibility example = new Responsibility();

        example.setResponsibility("Project Leader");
        example.getEmployee().setValue(anEmployee);
        return example;
    }

    public static Responsibility example2(Employee anEmployee) {
        Responsibility example = new Responsibility();

        example.setResponsibility("Team Leader");
        example.getEmployee().setValue(anEmployee);
        return example;
    }

    public static Responsibility example3(Employee anEmployee) {
        Responsibility example = new Responsibility();

        example.setResponsibility("Module Leader");
        example.getEmployee().setValue(anEmployee);
        return example;
    }

    public static Responsibility example4(Employee anEmployee) {
        Responsibility example = new Responsibility();

        example.setResponsibility("Developer");
        example.getEmployee().setValue(anEmployee);
        return example;
    }

    public static Responsibility example5(Employee anEmployee) {
        Responsibility example = new Responsibility();

        example.setResponsibility("Network Administrator");
        example.getEmployee().setValue(anEmployee);
        return example;
    }

    public static Responsibility example6(Employee anEmployee) {
        Responsibility example = new Responsibility();

        example.setResponsibility("Guy in change of things");
        example.getEmployee().setValue(anEmployee);
        return example;
    }

    public ValueHolderInterface getEmployee() {
        return employee;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String aString) {
        responsibility = aString;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("AGG_RES");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("DUTY", String.class, 30);
        definition.addField("EMP_ID", java.math.BigDecimal.class, 15);
        return definition;
    }
}