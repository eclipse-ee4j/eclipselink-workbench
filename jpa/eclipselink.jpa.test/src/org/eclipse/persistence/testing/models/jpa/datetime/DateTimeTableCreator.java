/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.datetime;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class DateTimeTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator {
    public DateTimeTableCreator() {
        setName("EJB3DateTimeProject");

        addTableDefinition(buildDateTimeTable());
    }

    public static TableDefinition buildDateTimeTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_DATE_TIME");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("DT_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldSTREET = new FieldDefinition();
        fieldSTREET.setName("SQL_DATE");
        fieldSTREET.setTypeName("DATE");
        fieldSTREET.setIsPrimaryKey(false);
        fieldSTREET.setIsIdentity(false);
        fieldSTREET.setUnique(false);
        fieldSTREET.setShouldAllowNull(true);
        table.addField(fieldSTREET);

        FieldDefinition fieldCITY = new FieldDefinition();
        fieldCITY.setName("SQL_TIME");
        fieldCITY.setTypeName("TIME");
        fieldCITY.setIsPrimaryKey(false);
        fieldCITY.setIsIdentity(false);
        fieldCITY.setUnique(false);
        fieldCITY.setShouldAllowNull(true);
        table.addField(fieldCITY);

        FieldDefinition fieldPROVINCE = new FieldDefinition();
        fieldPROVINCE.setName("SQL_TS");
        fieldPROVINCE.setTypeName("TIMESTAMP");
        fieldPROVINCE.setIsPrimaryKey(false);
        fieldPROVINCE.setIsIdentity(false);
        fieldPROVINCE.setUnique(false);
        fieldPROVINCE.setShouldAllowNull(true);
        table.addField(fieldPROVINCE);

        FieldDefinition fieldPOSTALCODE = new FieldDefinition();
        fieldPOSTALCODE.setName("UTIL_DATE");
        fieldPOSTALCODE.setTypeName("TIMESTAMP");
        fieldPOSTALCODE.setIsPrimaryKey(false);
        fieldPOSTALCODE.setIsIdentity(false);
        fieldPOSTALCODE.setUnique(false);
        fieldPOSTALCODE.setShouldAllowNull(true);
        table.addField(fieldPOSTALCODE);

        FieldDefinition fieldCalToCal = new FieldDefinition();
        fieldCalToCal.setName("CAL");
        fieldCalToCal.setTypeName("TIMESTAMP");
        fieldCalToCal.setIsPrimaryKey(false);
        fieldCalToCal.setIsIdentity(false);
        fieldCalToCal.setUnique(false);
        fieldCalToCal.setShouldAllowNull(true);
        table.addField(fieldCalToCal);

        return table;
    }

}