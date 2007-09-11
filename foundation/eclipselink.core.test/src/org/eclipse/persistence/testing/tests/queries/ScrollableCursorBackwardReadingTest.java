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

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 *  CR#4139
 *  Test use of next and previous with ScrollableCursors
 */
public class ScrollableCursorBackwardReadingTest extends TestCase {
    protected boolean useConforming = false;
    ConformingTestConfiguration configuration = null;

    protected boolean cursorSuccess = false;
    protected Exception caughtException = null;
    Vector readWithNext = null;
    Vector readWithPrevious = null;

    public ScrollableCursorBackwardReadingTest() {
        setDescription("This test verifies that the number of objects read in from the end of a scrollable cursor to the start" + 
                       " matches the number of object read in using a normal query");
    }

    public ScrollableCursorBackwardReadingTest(boolean useConforming) {
        this();
        if (useConforming) {
            setName("ScrollableCursorBackwardReadingConformingTest");
            this.useConforming = useConforming;
            this.configuration = new ConformingTestConfiguration();
        }
    }

    protected void setup() {
        if (getSession().getPlatform().isDB2() || getSession().getPlatform().isAccess() || 
            getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("ScrollableCursor is not supported on this platform");
        }
        readWithNext = new Vector();
        readWithPrevious = new Vector();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        if (configuration != null) {
            configuration.setup(getSession());
            getExecutor().setSession(configuration.getUnitOfWork());
        }
    }

    public void test() {

        ReadAllQuery query = new ReadAllQuery();
        ScrollableCursor cursor = null;

        try {
            query.setReferenceClass(Employee.class);
            query.useScrollableCursor(2);
            //
            if (configuration != null) {
                ExpressionBuilder builder = new ExpressionBuilder();
                Expression exp = builder.get("salary").greaterThan(50000);
                query.setSelectionCriteria(exp);
                query.conformResultsInUnitOfWork();
            }
            cursor = (ScrollableCursor)getSession().executeQuery(query);

            try {
                // test to see if we can iterate through a list and then iterate
                // in reverse through the same list.
                int totalItems = 0;
                while (cursor.hasNext()) {
                    readWithNext.addElement(cursor.next());
                    totalItems++;
                }
                while (cursor.hasPrevious()) {
                    readWithPrevious.addElement(cursor.previous());
                    totalItems--;
                }

                cursorSuccess = (totalItems == 0);

                int size = readWithPrevious.size();
                for (int i = 0; i < readWithNext.size(); i++) {
                    cursorSuccess = 
                            (cursorSuccess && (readWithNext.elementAt(i) == readWithPrevious.elementAt((size - 1) - 
                                                                                                       i)));
                }

            } catch (org.eclipse.persistence.exceptions.QueryException ex) {
                caughtException = ex;
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void reset() {

        if (configuration != null) {
            getExecutor().setSession(configuration.getUnitOfWork().getParent());
            configuration.reset();
        }
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    protected void verify() {
        if (caughtException != null) {
            throw new TestErrorException("Cursor navigation caused a QueryException.", caughtException);
        }
        if (!cursorSuccess) {
            throw new TestErrorException("Cursor navigation failed.  Either next() or previous is not " + 
                                         "returning the correct result.");
        }
    }
}