/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.datatypes.arraypks;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

public class PrimitiveArraysAsPrimaryKeyTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator{
    public PrimitiveArraysAsPrimaryKeyTableCreator() {
        setName("EJB3PrimitiveArrayPrimaryKeyProject");

        addTableDefinition(PrimitiveArraysAsPrimaryKeyTableCreator.buildPrimitiveByteArrayTable());
    }

    /**This is Oracle specific
     * Oracle does not allow blobs/longs to be primary keys, so the RAW type needs to be used
     */
    public static TableDefinition buildPrimitiveByteArrayTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PBYTEARRAYPK_TYPE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("RAW");
        fieldID.setSize(16);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        return table;
    }
}