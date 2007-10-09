/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import org.eclipse.persistence.jpa.config.TargetDatabase;
import org.eclipse.persistence.jpa.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

/**
 * This is a helper/impl class for the TopLink EJB 3.0 provider
 * The default constructor can be used to build the provider by reflection, after which it can
 * be used to create EntityManagerFactories
 */
public class EntityManagerFactoryProvider { 
    public static final HashMap<String, EntityManagerSetupImpl> emSetupImpls = new HashMap<String, EntityManagerSetupImpl>();
    
    // TEMPORARY - WILL BE REMOVED.
    // Used to warn users about deprecated property name and suggest the valid name.
    // TEMPORARY the old property names will be translated to the new ones and processed.
    protected static final String oldPropertyNames[][] = {
        {PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MAX, "eclipselink.max-write-connections"},
        {PersistenceUnitProperties.JDBC_WRITE_CONNECTIONS_MIN, "eclipselink.min-write-connections"},
        {PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MAX, "eclipselink.max-read-connections"},
        {PersistenceUnitProperties.JDBC_READ_CONNECTIONS_MIN, "eclipselink.min-read-connections"},
        {PersistenceUnitProperties.JDBC_BIND_PARAMETERS, "eclipselink.bind-all-parameters"},
        {PersistenceUnitProperties.TARGET_DATABASE, "eclipselink.platform.class.name"},
        {PersistenceUnitProperties.TARGET_SERVER, "eclipselink.server.platform.class.name"},
        {PersistenceUnitProperties.CACHE_SIZE_DEFAULT, "eclipselink.cache.default-size"}
    };

    /**
     * Add an EntityManagerSetupImpl to the cached list
     * These are used to ensure all persistence units that are the same get the same underlying session
     * @param name
     * @param setup
     */
    public static void addEntityManagerSetupImpl(String name, EntityManagerSetupImpl setup){
    	if (name == null){
    		emSetupImpls.put("", setup);
    	}
        emSetupImpls.put(name, setup);
    }

    protected static String addFileSeperator(String appLocation) {
        int strLength = appLocation.length();
        if (appLocation.substring(strLength -1, strLength).equals(File.separator)) {
            return appLocation;
        } else {
            return appLocation + File.separator;
        }
    }
    
    protected static void createOrReplaceDefaultTables(SchemaManager mgr, boolean shouldDropFirst) {          
    	if (shouldDropFirst){
    		mgr.replaceDefaultTables(true, true); 
    	} else { 
    		mgr.createDefaultTables(true); 
    	}
    }

    protected static void generateDDLFiles(ServerSession session, Map props, boolean inSEmode) {
        boolean createTables = false, shouldDropFirst = false;
        String appLocation; 
        String createDDLJdbc;
        String dropDDLJdbc;
        String ddlGeneration = PersistenceUnitProperties.NONE;
        
        if(null == props){
            return;
        }

        ddlGeneration = (String)getConfigPropertyAsString(PersistenceUnitProperties.DDL_GENERATION, props, PersistenceUnitProperties.NONE);
        ddlGeneration = ddlGeneration.toLowerCase();
        if(ddlGeneration.equals(PersistenceUnitProperties.NONE)) {
            return;
        }

        if(ddlGeneration.equals(PersistenceUnitProperties.CREATE_ONLY) || 
            ddlGeneration.equals(PersistenceUnitProperties.DROP_AND_CREATE)) {
            createTables = true;
            if(ddlGeneration.equals(PersistenceUnitProperties.DROP_AND_CREATE)) {
                shouldDropFirst = true;
            }
        } 
        
        if (createTables) {
            String ddlGenerationMode = (String) getConfigPropertyAsString(PersistenceUnitProperties.DDL_GENERATION_MODE, props, PersistenceUnitProperties.DEFAULT_DDL_GENERATION_MODE);
            // Optimize for cases where the value is explicitly set to NONE 
            if (ddlGenerationMode.equals(PersistenceUnitProperties.NONE)) {                
                return;
            }
            
            appLocation = (String)getConfigPropertyAsString(PersistenceUnitProperties.APP_LOCATION, props, PersistenceUnitProperties.DEFAULT_APP_LOCATION);
            createDDLJdbc = (String)getConfigPropertyAsString(PersistenceUnitProperties.CREATE_JDBC_DDL_FILE, props, PersistenceUnitProperties.DEFAULT_CREATE_JDBC_FILE_NAME);
            dropDDLJdbc = (String)getConfigPropertyAsString(PersistenceUnitProperties.DROP_JDBC_DDL_FILE, props,  PersistenceUnitProperties.DEFAULT_DROP_JDBC_FILE_NAME);
            
            SchemaManager mgr = new SchemaManager(session);
            
            // The inSEmode checks here are only temporary to ensure we still 
            // play nicely with Glassfish.
            if (ddlGenerationMode.equals(PersistenceUnitProperties.DDL_DATABASE_GENERATION) || inSEmode) {
                runInSEMode(mgr, shouldDropFirst);                
                
                if (inSEmode) {
                    writeDDLsToFiles(mgr, appLocation,  createDDLJdbc,  dropDDLJdbc);      
                }
            } else if (ddlGenerationMode.equals(PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION)) {
                writeDDLsToFiles(mgr, appLocation,  createDDLJdbc,  dropDDLJdbc);                
            } else if (ddlGenerationMode.equals(PersistenceUnitProperties.DDL_BOTH_GENERATION)) {
                runInSEMode(mgr, shouldDropFirst);
                writeDDLsToFiles(mgr, appLocation,  createDDLJdbc,  dropDDLJdbc);
            }
        }
    }
    
    protected static String getConfigPropertyAsString(String propertyKey, Map overrides){
        String value = null;
        if (overrides != null){
            value = (String)overrides.get(propertyKey);
        }
        if (value == null){
            value = System.getProperty(propertyKey);
        }
        
        return value;
    }
    
    /**
     * Check the provided map for an object with the given key.  If that object is not available, check the
     * System properties.  If it is not available from either location, return the default value.
     * @param propertyKey 
     * @param map 
     * @param defaultValue 
     * @return 
     */
    protected static String getConfigPropertyAsString(String propertyKey, Map overrides, String defaultValue){
    	String value = getConfigPropertyAsString(propertyKey, overrides);
        if (value == null){
            value = defaultValue;
        }
        return value;
    }

    protected static String getConfigPropertyAsStringLogDebug(String propertyKey, Map overrides, AbstractSession session){
        String value = null;
        if (overrides != null){
            value = (String)overrides.get(propertyKey);
        }
        if (value == null){
            value = System.getProperty(propertyKey);
        }
        if(value != null && session !=  null) {
            String overrideValue = PersistenceUnitProperties.getOverriddenLogStringForProperty(propertyKey);;           
            String logValue = (overrideValue == null) ? value : overrideValue;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_specified", new Object[]{propertyKey, logValue});
        }
        
        return value;
    }
    
    protected static String getConfigPropertyAsStringLogDebug(String propertyKey, Map overrides, String defaultValue, AbstractSession session){
        String value = getConfigPropertyAsStringLogDebug(propertyKey, overrides, session);
        if (value == null){
            value = defaultValue;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_default", new Object[]{propertyKey, value});
        }
        return value;
    }
    
    protected static Object getConfigPropertyLogDebug(String propertyKey, Map overrides, AbstractSession session){
        Object value = null;
        if (overrides != null){
            value = overrides.get(propertyKey);
        }
        if (value == null){
            value = System.getProperty(propertyKey);
        }
        if(value != null && session !=  null) {
            String overrideValue = PersistenceUnitProperties.getOverriddenLogStringForProperty(propertyKey);;           
            Object logValue = (overrideValue == null) ? value : overrideValue;
            session.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_specified", new Object[]{propertyKey, logValue});
        }
        
        return value;
    }
    
    /**
     * Return the setup class for a given entity manager name 
     * @param emName 
     */
    public static EntityManagerSetupImpl getEntityManagerSetupImpl(String emName){
    	if (emName == null){
    		return (EntityManagerSetupImpl)emSetupImpls.get("");
    	}
        return (EntityManagerSetupImpl)emSetupImpls.get(emName);
    }

    /**
     * Logs in to given session. If user has not specified  <codeTARGET_DATABASE</code>
     * the plaform would be auto detected
     * @param session The session to login to.
     * @param properties User specified properties for the persistence unit
     */
    protected static void login(ServerSession session, Map properties) {
        String toplinkPlatform = (String)properties.get(PersistenceUnitProperties.TARGET_DATABASE);
        if (!session.isConnected()) {
            if (toplinkPlatform == null || toplinkPlatform.equals(TargetDatabase.Auto)) {
                // if user has not specified a database platform, try to detect
                session.loginAndDetectDatasource();
            } else {
                session.login();
            }
        }
    }
    
    /**
     * Merge the properties from the source object into the target object.  If the property
     * exists in both objects, use the one from the target
     * @param target 
     * @param source 
     * @return the target object
     */
    protected static Map mergeMaps(Map target, Map source){
        Map map = new HashMap();
        if (source != null){
            map.putAll(source);
        }

        if (target != null){
            map.putAll(target);
        }
        return map;
    }
    
    protected static void runInSEMode(SchemaManager mgr, boolean shouldDropFirst) {
        String str = getConfigPropertyAsString(PersistenceUnitProperties.JAVASE_DB_INTERACTION, null ,"true");
        boolean interactWithDB = Boolean.valueOf(str.toLowerCase()).booleanValue();
        if (!interactWithDB){
            return;
        }
        createOrReplaceDefaultTables(mgr, shouldDropFirst);
    }
    
    /**
     * This is a TEMPORARY method that will be removed.
     * DON'T USE THIS METHOD - for internal use only.
     * @param Map m
     * @param AbstractSession session
     */
    protected static void translateOldProperties(Map m, AbstractSession session) {
        for(int i=0; i < oldPropertyNames.length; i++) {
            Object value = getConfigPropertyAsString(oldPropertyNames[i][1], m);
            if(value != null) {
                if(session != null){
                    session.log(SessionLog.INFO, SessionLog.TRANSACTION, "deprecated_property", oldPropertyNames[i]);
                }
                m.put(oldPropertyNames[i][0], value);
            }
        }
    }
    
    protected static void warnOldProperties(Map m, AbstractSession session) {
    	for(int i=0; i < oldPropertyNames.length; i++) {
    		Object value = m.get(oldPropertyNames[i][1]);
            if(value != null) {
                session.log(SessionLog.INFO, SessionLog.TRANSACTION, "deprecated_property", oldPropertyNames[i]);
            }
        }
    }

    protected static void writeDDLsToFiles(SchemaManager mgr,  String appLocation, String createDDLJdbc, String dropDDLJdbc) {
    	// Ensure that the appLocation string ends with  File.seperator 
        appLocation = addFileSeperator(appLocation);
        if (null != createDDLJdbc) {
        	String createJdbcFileName = appLocation + createDDLJdbc;
            mgr.outputCreateDDLToFile(createJdbcFileName);
        }

        if (null != dropDDLJdbc) {
        	String dropJdbcFileName = appLocation + dropDDLJdbc;              
            mgr.outputDropDDLToFile(dropJdbcFileName);
        }

        mgr.setCreateSQLFiles(false);
        // When running in the application server environment always ensure that
        // we write out both the drop and create table files.
        createOrReplaceDefaultTables(mgr, true);
        mgr.closeDDLWriter();
    }    
}