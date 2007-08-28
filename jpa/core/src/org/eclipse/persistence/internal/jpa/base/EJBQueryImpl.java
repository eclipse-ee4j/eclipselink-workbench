/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.base;

import java.util.*;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.BasicTypeHelperImpl;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.jpa.parsing.JPQLParseTree;
import org.eclipse.persistence.internal.jpa.parsing.jpql.*;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.queries.JPQLCallQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * Concrete EJB 3.0 query class.
 */
public abstract class EJBQueryImpl {
    protected DatabaseQuery databaseQuery = null;
    protected EntityManagerImpl entityManager = null;
    protected String queryName = null;
    protected Map parameters = null;
    protected int firstResultIndex = -1; // -1 indicates undefined
    protected int maxResults = -1; // -1 indicates undefined
    protected int maxRows = -1; // -1 indicates undefined

    abstract protected void throwNoResultException(String message);    
    abstract protected void throwNonUniqueResultException(String message);    

    /**
     * Base constructor for EJBQueryImpl.  Initializes basic variables.
     */
    protected EJBQueryImpl(EntityManagerImpl entityManager) {
        parameters = new HashMap();
        this.entityManager = entityManager;
    }

    /**
     * Create an EJBQueryImpl with a TopLink query.
     * @param query
     */
    public EJBQueryImpl(DatabaseQuery query, EntityManagerImpl entityManager) {
        this(entityManager);
        this.databaseQuery = query;
    }

    /**
     * Build an EJBQueryImpl based on the given ejbql string
     * @param ejbql
     * @param entityManager
     */
    public EJBQueryImpl(String ejbql, EntityManagerImpl entityManager) {
        this(ejbql, entityManager, false);
    }

    /**
     * Create an EJBQueryImpl with either a query name or an ejbql string
     * @param queryDescription
     * @param entityManager
     * @param isNamedQuery determines whether to treat the query description as ejbql or a query name
     */
    public EJBQueryImpl(String queryDescription, EntityManagerImpl entityManager, boolean isNamedQuery) {
        this(entityManager);
        if (isNamedQuery) {
            this.queryName = queryDescription;
        } else {
            if (databaseQuery == null) {
                databaseQuery = buildEJBQLDatabaseQuery(queryDescription, getActiveSession());
            }
        }
    }

    /**
     * Internal method to change the wrapped query to a DataModifyQuery if neccessary
     */
    protected void setAsSQLModifyQuery(){
        if (getDatabaseQuery().isDataReadQuery()){
            DataModifyQuery query = new DataModifyQuery();
            query.setSQLString(databaseQuery.getSQLString());
            query.setIsUserDefined(databaseQuery.isUserDefined());
            query.setFlushOnExecute(databaseQuery.getFlushOnExecute());
            databaseQuery = query;
        }
    }

    /**
     * Internal method to change the wrapped query to a DataReadQuery if neccessary
     */
    protected void setAsSQLReadQuery(){
        if(getDatabaseQuery().isDataModifyQuery()){
            DataReadQuery query = new DataReadQuery();
            query.setUseAbstractRecord(false);
            query.setSQLString(databaseQuery.getSQLString());
            query.setIsUserDefined(databaseQuery.isUserDefined());
            query.setFlushOnExecute(databaseQuery.getFlushOnExecute());
            databaseQuery = query;
        }
    }

    /**
     * Build a DatabaseQuery from an EJBQL string.
     * @param ejbql
     * @param session the session to get the descriptors for this query for.
     * @return a DatabaseQuery representing the given ejbql
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String ejbql, Session session) {
        return buildEJBQLDatabaseQuery(ejbql, null, session);
    }
    
    /**
     * Build a DatabaseQuery from an EJBQL string.
     * 
     * @param ejbql
     * @param session the session to get the descriptors for this query for.
     * @param hints a list of hints to be applied to the query
     * @return a DatabaseQuery representing the given ejbql
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String ejbql, Session session, HashMap hints) {
        return buildEJBQLDatabaseQuery(null, ejbql, null, session, hints, null);
    }
    
    /**
     * Build a DatabaseQuery from an EJBQL string.
     * 
     * @param ejbql
     * @param session the session to get the descriptors for this query for.
     * @param hints a list of hints to be applied to the query
     * @param classLoader the class loader to build the query with
     * @return a DatabaseQuery representing the given ejbql
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String ejbql, Session session, HashMap hints, ClassLoader classLoader) {
        return buildEJBQLDatabaseQuery(null, ejbql, null, session, hints, classLoader);
    }
    
    /**
     * Build a DatabaseQuery from an EJBQL string.
     * @param ejbql
     * @parem flushOnExecute
     * @param session the session to get the descriptors for this query for.
     * @return a DatabaseQuery representing the given ejbql
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String ejbql,  Boolean flushOnExecute, Session session) {
        return buildEJBQLDatabaseQuery(null, ejbql, flushOnExecute, session, null, null);
    }
    
    /**
     * Build a DatabaseQuery from an EJBQL string.
     * @param ejbql
     * @parem flushOnExecute
     * @param session the session to get the descriptors for this query for.
     * @return a DatabaseQuery representing the given ejbql
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String ejbql,  Boolean flushOnExecute, Session session, ClassLoader classLoader) {
        return buildEJBQLDatabaseQuery(null, ejbql, flushOnExecute, session, null, classLoader);
    }
    
    /**
     * Build a DatabaseQuery from an JPQL string.
     * 
     * @param jpql the JPQL string.
     * @param flushOnExecute flush the unit of work before executing the query.
     * @param session the session to get the descriptors for this query for.
     * @param hints a list of hints to be applied to the query.
     * @return a DatabaseQuery representing the given jpql.
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String queryName, String jpql, 
            Boolean flushOnExecute, Session session, HashMap hints, ClassLoader classLoader) {            
        // PERF: Check if the JPQL has already been parsed.
        // Only allow queries with default properties to be parse cached.
        boolean isCacheable = (queryName == null) && (hints == null) && (flushOnExecute == null);
        DatabaseQuery databaseQuery = null;
        if (isCacheable) {
            databaseQuery = (DatabaseQuery)session.getProject().getJPQLParseCache().get(jpql);            
        }
        if ((databaseQuery == null) || (!databaseQuery.isPrepared())) {
            JPQLParseTree parseTree = JPQLParser.buildParseTree(queryName, jpql);
            parseTree.setClassLoader(classLoader);
            databaseQuery = parseTree.createDatabaseQuery();
            databaseQuery.setJPQLString(jpql);
            parseTree.populateQuery(databaseQuery, (AbstractSession)session);            
            // If the query uses fetch joins, need to use JPA default of not filtering duplicates.
            if (databaseQuery.isReadAllQuery()) {
                ReadAllQuery readAllQuery = (ReadAllQuery)databaseQuery;
                if (readAllQuery.hasJoining() && (readAllQuery.getDistinctState() == ReadAllQuery.DONT_USE_DISTINCT)) {
                    readAllQuery.setShouldFilterDuplicates(false);
                }
            }
            // Bug#4646580  Add arguments to query.
            parseTree.addParametersToQuery(databaseQuery);
            ((JPQLCallQueryMechanism)databaseQuery.getQueryMechanism()).getJPQLCall().setIsParsed(true);
            databaseQuery.setFlushOnExecute(flushOnExecute);            

            //GF#1324 eclipselink.refresh query hint does not cascade
            //cascade by mapping as default for read query
            if(databaseQuery.isReadQuery ()) {
                databaseQuery.cascadeByMapping();
            }

            // Apply any query hints.
            databaseQuery = applyHints(hints, databaseQuery);
            if (isCacheable) {
                // Prepare query as hint may cause cloning (but not un-prepare as in read-only).
                databaseQuery.prepareCall(session, new DatabaseRecord());
                databaseQuery.setIsFromParseCache(true);
                session.getProject().getJPQLParseCache().put(jpql, databaseQuery);
            }
        }
        
        return databaseQuery;
    }
    
    /**
     * Build a ReadAllQuery from a class and sql string.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(Class resultClass, String sqlString) {
        return buildSQLDatabaseQuery(resultClass, sqlString, null);
    }
    
    /**
     * Build a ReadAllQuery for class and sql string.
     * 
     * @param hints a list of hints to be applied to the query.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(Class resultClass, String sqlString, HashMap hints) {
        ReadAllQuery query = new ReadAllQuery(resultClass);
        query.setSQLString(sqlString);
        query.setIsUserDefined(true);
        
        // apply any query hints
        return applyHints(hints, query);
    }
    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and sql string.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(String sqlResultSetMappingName, String sqlString) {
        return buildSQLDatabaseQuery(sqlResultSetMappingName, sqlString, null);
    }
    
    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and sql string.
     * @param hints a list of hints to be applied to the query.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(String sqlResultSetMappingName, String sqlString, HashMap hints) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.setSQLResultSetMappingName(sqlResultSetMappingName);
        query.setSQLString(sqlString);
        query.setIsUserDefined(true);
        
        // apply any query hints
        return applyHints(hints, query);
    }
    
    /**
     * Build a ReadAllQuery from a class and stored procedure call.
     */
    public static DatabaseQuery buildStoredProcedureQuery(Class resultClass, StoredProcedureCall call, List<String> arguments, HashMap hints) {
        DatabaseQuery query = new ReadAllQuery(resultClass);
        query.setCall(call);
        query.setIsUserDefined(true);
        
        // apply any query hints
        query = applyHints(hints, query);
        
        // apply any query arguments
        applyArguments(arguments, query);

        return query;
    }

    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and a 
     * stored procedure call.
     */
    public static DatabaseQuery buildStoredProcedureQuery(StoredProcedureCall call, List<String> arguments, HashMap hints) {
        DatabaseQuery query = new ResultSetMappingQuery();
        query.setCall(call);
        query.setIsUserDefined(true);
        
        // apply any query hints
        query = applyHints(hints, query);
        
        // apply any query arguments
        applyArguments(arguments, query);

        return query;
    }
    
    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and a
     * stored procedure call.
     */
    public static DatabaseQuery buildStoredProcedureQuery(String sqlResultSetMappingName, StoredProcedureCall call, List<String> arguments, HashMap hints) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.setSQLResultSetMappingName(sqlResultSetMappingName);
        query.setCall(call);
        query.setIsUserDefined(true);
        
        // apply any query hints
        DatabaseQuery hintQuery = applyHints(hints, query);
        
        // apply any query arguments
        applyArguments(arguments, hintQuery);

        return hintQuery;
    }

    /**
     * Build a DataReadQuery from a sql string.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(String sqlString, Boolean flushOnExecute) {
        return buildSQLDatabaseQuery(sqlString, new HashMap());
    }
    
    /**
     * Build a DataReadQuery from a sql string.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(String sqlString, HashMap hints) {
        DataReadQuery query = new DataReadQuery();
        query.setUseAbstractRecord(false);
        query.setSQLString(sqlString);
        query.setIsUserDefined(true);

        // apply any query hints
        return applyHints(hints, query);
    }

    /**
     * Execute a ReadQuery by assigning the stored parameter values and running it
     * in the database
     * @return the results of the query execution
     */
    protected Object executeReadQuery() {
        Vector parameterValues = processParameters();
        //TODO: the following performFlush() call is a temporary workaround for bug 4752493:
        // CTS: INMEMORY QUERYING IN EJBQUERY BROKEN DUE TO CHANGE TO USE REPORTQUERY.
        // Ideally we should only flush in case the selectionExpression can't be conformed in memory.
        // There are two alternative ways to implement that:
        // 1. Try running the query with conformInUOW flag first - if it fails with 
        //    QueryException.cannotConformExpression then flush and run the query again -
        //    now without conforming.
        // 2. Implement a new isComformable method on Expression which would determine whether the expression
        //    could be conformed in memory, flush only in case it returns false.
        //    Note that doesConform method currently implemented on Expression
        //    requires object(s) to be confirmed as parameter(s).
        //    The new isComformable method should not take any objects as parameters,
        //    it should return false if there could be such an object that
        //    passed to doesConform causes it to throw QueryException.cannotConformExpression -
        //    and true otherwise.
        boolean shouldResetConformResultsInUnitOfWork = false;
        if (isFlushModeAUTO()) {
            performPreQueryFlush();
            if (getDatabaseQuery().isObjectLevelReadQuery()) {
                if (((ObjectLevelReadQuery)getDatabaseQuery()).shouldConformResultsInUnitOfWork()) {
                    cloneIfParseCachedQuery();
                    ((ObjectLevelReadQuery)getDatabaseQuery()).setCacheUsage(ObjectLevelReadQuery.UseDescriptorSetting);
                    shouldResetConformResultsInUnitOfWork = true;
                }
            }
        }
        try {
            // in case it's a user-defined query
            if (getDatabaseQuery().isUserDefined()) {
                // and there is an active transaction
                if (this.entityManager.checkForTransaction(false) != null) {
                    // verify whether uow has begun early transaction
                    if (!((org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)getActiveSession()).wasTransactionBegunPrematurely()) {
                        // uow begins early transaction in case it hasn't already begun.
                        ((org.eclipse.persistence.internal.sessions.UnitOfWorkImpl)getActiveSession()).beginEarlyTransaction();
                    }
                }
            }
            return getActiveSession().executeQuery(getDatabaseQuery(), parameterValues);
        } finally {
            if (shouldResetConformResultsInUnitOfWork) {
                ((ObjectLevelReadQuery)getDatabaseQuery()).conformResultsInUnitOfWork();
            }
        }
    }

    /**
     * Execute an update or delete statement.
     * @return the number of entities updated or deleted
     */
    public int executeUpdate() {
        try {
            //bug51411440: need to throw IllegalStateException if query executed on closed em
            entityManager.verifyOpen();
            setAsSQLModifyQuery();
            //bug:4294241, only allow modify queries - UpdateAllQuery prefered
            if (!(getDatabaseQuery() instanceof ModifyQuery)){
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_execute_update"));
            }
            //fix for bug:4288845, did not add the parameters to the query
            Vector parameterValues = processParameters();
            if(isFlushModeAUTO()) {
                performPreQueryFlush();
            }
            Integer changedRows = (Integer)getActiveSession().executeQuery(databaseQuery, parameterValues);
            return changedRows.intValue();
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Return the cached database query for this EJBQueryImpl.  If the query is
     * a named query and it has not yet been looked up, the query will be looked up
     * and stored as the cached query.
     */
    public DatabaseQuery getDatabaseQuery() {
        if ((queryName != null) && (databaseQuery == null)) {
            // need error checking and appropriate exception for non-existing query
            databaseQuery = getActiveSession().getQuery(queryName);
            if (databaseQuery != null) {
                if (!databaseQuery.isPrepared()){
                    //prepare the query before cloning, this ensures we do not have to continually prepare on each usage
                    databaseQuery.prepareCall(getActiveSession(), new DatabaseRecord());
                }
                //Bug5040609  Make a clone of the original DatabaseQuery for this EJBQuery
                databaseQuery = (DatabaseQuery)databaseQuery.clone();
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unable_to_find_named_query", new Object[] {queryName}));
            }
            
        }
        return databaseQuery;
    }

    /**
     * Non-standard method to return results of a ReadQuery that has a containerPoliry
     * that returns objects as a collection rather than a List
     * @return Collection of results
     */
    public Collection getResultCollection() {
        //bug51411440: need to throw IllegalStateException if query executed on closed em
        entityManager.verifyOpen();
        setAsSQLReadQuery();
        propagateResultProperties();
        //bug:4297903, check container policy class and throw exception if its not the right type 
        if (getDatabaseQuery() instanceof ReadAllQuery){
          Class containerClass = ((ReadAllQuery)getDatabaseQuery()).getContainerPolicy().getContainerClass();
          if (! Helper.classImplementsInterface(containerClass, ClassConstants.Collection_Class)){
            throw QueryException.invalidContainerClass( containerClass, ClassConstants.Collection_Class );
          }
        } else if (getDatabaseQuery() instanceof ReadObjectQuery){
            //bug:4300879, no support for ReadObjectQuery if a collection is required
            throw QueryException.incorrectQueryObjectFound( getDatabaseQuery(), ReadAllQuery.class );
        } else if (!(getDatabaseQuery() instanceof ReadQuery)){
            throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_result_collection"));
        }
        Object result = executeReadQuery();
        return (Collection)result;
    }

    /**
    * Execute the query and return the query results
    * as a List.
    * @return a list of the results
    */
    public List getResultList() {
        try {
            //bug51411440: need to throw IllegalStateException if query executed on closed em
            entityManager.verifyOpen();
            setAsSQLReadQuery();
            propagateResultProperties();
            //bug:4297903, check container policy class and throw exception if its not the right type 
            if (getDatabaseQuery() instanceof ReadAllQuery){
              Class containerClass = ((ReadAllQuery)getDatabaseQuery()).getContainerPolicy().getContainerClass();
              if (! Helper.classImplementsInterface(containerClass, ClassConstants.List_Class)){
                throw QueryException.invalidContainerClass( containerClass, ClassConstants.List_Class );
              }
            } else if (getDatabaseQuery() instanceof ReadObjectQuery){
                //bug:4300879, handle ReadObjectQuery returning null
                throw QueryException.incorrectQueryObjectFound( getDatabaseQuery(), ReadAllQuery.class );
            } else if (!(getDatabaseQuery() instanceof ReadQuery)){
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_result_list"));
            }
            Object result = executeReadQuery();
            return (List)result;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
    * Execute a query that returns a single result.
    * @return the result
    * @throws javax.persistence.EntityNotFoundException if there is no result
    * @throws javax.persistence.NonUniqueResultException if more than one result
    */
    public Object getSingleResult() {
        boolean rollbackOnException = true;
        try {
            //bug51411440: need to throw IllegalStateException if query executed on closed em
            entityManager.verifyOpen();
            setAsSQLReadQuery();
            propagateResultProperties();
            //bug:4301674, requires lists to be returned from ReadAllQuery objects
            if (getDatabaseQuery() instanceof ReadAllQuery){
              Class containerClass = ((ReadAllQuery)getDatabaseQuery()).getContainerPolicy().getContainerClass();
              if (! Helper.classImplementsInterface(containerClass, ClassConstants.List_Class)){
                throw QueryException.invalidContainerClass( containerClass, ClassConstants.List_Class );
              }
            } else if (!(getDatabaseQuery() instanceof ReadQuery)){
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_single_result"));
            }
            Object result = executeReadQuery();
            if (result instanceof List){
                List results = (List)result;
                if (results.isEmpty()) {
                    rollbackOnException = false;
                    throwNoResultException(ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_single_result", (Object[])null));                
                } else if (results.size() > 1) {
                    rollbackOnException = false;
                    throwNonUniqueResultException(ExceptionLocalization.buildMessage("too_many_results_for_get_single_result", (Object[])null));
                }
                return results.get(0);
            }else{
                if (result == null) {
                    rollbackOnException = false;
                    throwNoResultException(ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_single_result", (Object[])null));
                }
                return result;
            }
        } catch (RuntimeException e) {
            if(rollbackOnException) {
                setRollbackOnly();
            }
            throw e;
        }
    }

    /**
     * Internal method to add the parameters values to the query prior to execution. 
     * Returns a list of parameter values in the order the parameters are
     * defined for the databaseQuery.
     */
    protected Vector processParameters() {
        if (databaseQuery == null) {
            getDatabaseQuery();
        }
        List arguments = databaseQuery.getArguments();
        if (arguments.isEmpty()) {
            Iterator params = parameters.keySet().iterator();
            while (params.hasNext()) {
                databaseQuery.addArgument((String)params.next());
            }
            arguments = databaseQuery.getArguments();
        }
        // now create parameterValues in the same order as the argument list
        Vector parameterValues = new Vector(arguments.size());
        for (Iterator i = arguments.iterator(); i.hasNext();) {
            String name = (String)i.next();
            if (parameters.containsKey(name)) {
                parameterValues.add(parameters.get(name));
            } else {
                // Error: missing actual parameter value
                throw new IllegalStateException(ExceptionLocalization.buildMessage("missing_parameter_value", new Object[]{name}));
            }
        }
        return parameterValues;
    }

    /**
     * Replace the cached query with the given query.
     */
    public void setDatabaseQuery(DatabaseQuery query) {
        databaseQuery = query;
    }

    /**
     * Set the position of the first result to retrieve.
     * @param startPosition position of the first result, numbered from 0.
     */
    protected void setFirstResultInternal(int startPosition) {
        if (startPosition < 0) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("negative_start_position", (Object[])null));
        }
        firstResultIndex = startPosition;
    }

    /**
     * Set implementation-specific query arguments.
     */
    protected static void applyArguments(List<String> arguments, DatabaseQuery query) {
        for (String argument : arguments) {
            query.addArgument(argument);
        }
    }
    
    /**
     * Set implementation-specific hints.
     * 
     * @param hints a list of hints to be applied to the query
     * @param query the query to apply the hints to
     */
    protected static DatabaseQuery applyHints(HashMap hints, DatabaseQuery query) {
        return QueryHintsHandler.apply(hints, query);
    }

    /**
     * Spec. 3.5.2:
     * "FlushMode.AUTO is set on the Query object, or if
     * the flush mode setting for the persistence context is AUTO (the default) 
     * and a flush mode setting has not been specified for the Query object,
     * the persistence provider is responsible for ensuring that all updates
     * to the state of all entities in the persistence context which could potentially 
     * affect the result of the query are visible to the processing of the query."
     */
    protected boolean isFlushModeAUTO() {
        if(getDatabaseQuery().getFlushOnExecute() != null) {
            return getDatabaseQuery().getFlushOnExecute().booleanValue();
        } else {
            return entityManager.isFlushModeAUTO();
        }
    }
    
    /**
     * Set an implementation-specific hint.
     * If the hint name is not recognized, it is silently ignored.
     * @throws IllegalArgumentException if the second argument is not
     * valid for the implementation.
     */
    protected void setHintInternal(String hintName, Object value) {
        cloneIfParseCachedQuery();
        DatabaseQuery hintQuery = QueryHintsHandler.apply(hintName, value, getDatabaseQuery());
        if (hintQuery != null) {
            setDatabaseQuery(hintQuery);
        }
    }
    
    /**
     * If the query was from the jpql parse cache it must be cloned before being modified.
     */
    protected void cloneIfParseCachedQuery() {
        DatabaseQuery query = getDatabaseQuery();
        if (query.isFromParseCache()) {
            // Clone to allow setting of hints or other properties without corrupting original query.
            query = (DatabaseQuery)databaseQuery.clone();
            query.setIsFromParseCache(false);
            setDatabaseQuery(query);
        }
    }

    /**
    * Set the maximum number of results to retrieve.
    * @param maxResult
    */
    public void setMaxResultsInternal(int maxResult) {
        if (maxResult < 0) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("negative_max_result", (Object[])null));
        }
        this.maxResults = maxResult;
    }

    /**
     * Configure the firstResult and maxRows in the TopLink ReadQuery.
     */
    protected void propagateResultProperties() {
        DatabaseQuery databaseQuery = getDatabaseQuery();
        if (databaseQuery.isReadQuery()) {
            ReadQuery readQuery = (ReadQuery)databaseQuery;
            if (maxResults >= 0) {
                cloneIfParseCachedQuery();
                readQuery = (ReadQuery)getDatabaseQuery();
                maxRows = maxResults + ((firstResultIndex >= 0) ? firstResultIndex : 0);
                readQuery.setMaxRows(maxRows);
                maxResults = -1;
            }
            if (firstResultIndex > -1) {
                cloneIfParseCachedQuery();
                readQuery = (ReadQuery)getDatabaseQuery();
                readQuery.setFirstResult(firstResultIndex);
                firstResultIndex = -1;
            }
        }
    }

    /**
    * Bind an argument to a named parameter.
    * @param name the parameter name
    * @param value
    */
    protected void setParameterInternal(String name, Object value) {
        int index  = getDatabaseQuery().getArguments().indexOf(name);
        if (getDatabaseQuery().getEJBQLString() != null){  //only non native queries
            if (index == -1){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-argument-name",new Object[]{name, getDatabaseQuery().getEJBQLString()}));
            }
            if (!isValidActualParameter(value, getDatabaseQuery().getArgumentTypes().get(index))) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-incorrect-parameter-type", new Object[] {name, value.getClass(), getDatabaseQuery().getArgumentTypes().get(index), getDatabaseQuery().getEJBQLString()}));
            }
        }
        parameters.put(name, value);
    }

    /**
    * Bind an argument to a positional parameter.
    * @param position
    * @param value
    */
    protected void setParameterInternal(int position, Object value) {
        String pos = (new Integer(position)).toString();
        int index = getDatabaseQuery().getArguments().indexOf(pos);
        if (getDatabaseQuery().getEJBQLString() != null){  //only non native queries
            if (index == -1) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-argument-index", new Object[]{position, getDatabaseQuery().getEJBQLString()}));
            }
            if (!isValidActualParameter(value, getDatabaseQuery().getArgumentTypes().get(index))) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-incorrect-parameter-type", new Object[] {position, value.getClass(), getDatabaseQuery().getArgumentTypes().get(index), getDatabaseQuery().getEJBQLString()}));
            }
        }
        parameters.put(pos, value);
    }

    protected boolean isValidActualParameter(Object value, Object parameterType) {
        if (value == null) {
            return true;
        } else {
            return BasicTypeHelperImpl.getInstance().isAssignableFrom(parameterType, value.getClass());
        }
    }

    protected Session getActiveSession() {
        return entityManager.getActiveSession();
    }    
    
    protected void performPreQueryFlush(){
        if (this.entityManager.shouldFlushBeforeQuery()){
            this.entityManager.flush();
        }
    }

    protected void setRollbackOnly() {
        entityManager.setRollbackOnly();
    }
}