/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * Test the scrollable cursor feature for jdk1.1 by performing a cursor read on the database
 * and iterating through the elements.
 */
public class Jdk12ScrollableCursorTest extends TestCase {
    protected Vector cursoredQueryObjects;

    public Jdk12ScrollableCursorTest() {

        setDescription("This test tests ScrollableCursor in jdk1.1");
    }

    protected void setup() {
        if (getSession().getPlatform().isAccess() || getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("ScrollableCursor is not supported on this platform");
        }
        if (getSession().getPlatform().isDB2()) {
            throw new TestWarningException("java.sql.SQLException: [IBM][JDBC Driver] CLI0626E" + Helper.cr() + 
                                           "Updatable result set is not supported in this version of DB2 JDBC 2.0 driver.");
        }

        this.cursoredQueryObjects = new Vector();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {

        ReadAllQuery query = new ReadAllQuery();

        query.setReferenceClass(Employee.class);
        query.useScrollableCursor();
        ScrollableCursor cursor = (ScrollableCursor)getSession().executeQuery(query);

        while (cursor.hasMoreElements()) {
            this.cursoredQueryObjects.addElement(cursor.nextElement());
        }
    }
}