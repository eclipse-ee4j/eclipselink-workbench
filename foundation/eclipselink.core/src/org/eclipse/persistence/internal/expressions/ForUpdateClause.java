/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.HashSet;
import java.util.Collection;

import org.eclipse.persistence.queries.ObjectBuildingQuery;

/**
 * <b>Purpose:</b>Represents The FOR UPDATE pessimistically locking clause.
 *  @author  Stephen McRitchie
 *  @since   Oracle Toplink 10g AS
 */
public class ForUpdateClause implements Serializable, Cloneable {
    protected static final ForUpdateClause NO_LOCK_CLAUSE = new ForUpdateClause();
    short lockMode;

    public ForUpdateClause() {
        this.lockMode = ObjectBuildingQuery.NO_LOCK;
    }

    public ForUpdateClause(short lockMode) {
        this.lockMode = lockMode;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException never) {
            return null;
        }
    }

    public static ForUpdateClause newInstance(short lockMode) {
        if (lockMode == ObjectBuildingQuery.NO_LOCK) {
            return NO_LOCK_CLAUSE;
        } else {
            return new ForUpdateClause(lockMode);
        }
    }

    public boolean isForUpdateOfClause() {
        return false;
    }

    public boolean isReferenceClassLocked() {
        return true;
    }

    public short getLockMode() {
        return lockMode;
    }

    /**
     * INTERNAL:
     * Prints the as of clause for an expression inside of the FROM clause.
     */
    public void printSQL(ExpressionSQLPrinter printer, SQLSelectStatement statement) {
        // Append lock strings
        if (getLockMode() == ObjectBuildingQuery.LOCK) {
            printer.printString(printer.getSession().getPlatform().getSelectForUpdateString());
        } else if (lockMode == ObjectBuildingQuery.LOCK_NOWAIT) {
            printer.printString(printer.getSession().getPlatform().getSelectForUpdateNoWaitString());
        }
    }
    
    /**
     * INTERNAL:
     * Returns collection of aliases of the tables to be locked.
     * Only used by platforms that lock tables individually in FROM clause
     * (platform.shouldPrintLockingClauseAfterWhereClause()==false)
     * like SQLServer
     */
    public Collection getAliasesOfTablesToBeLocked(SQLSelectStatement statement) {
        return new HashSet(statement.getTableAliases().keySet());
    }
}