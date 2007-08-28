/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions;

import java.io.File;
import java.net.URL;

import org.eclipse.persistence.exceptions.i18n.*;

public class PersistenceUnitLoadingException  extends EclipseLinkException {

    private String resourceName = null;
    
    public static final int EXCEPTION_LOADING_FROM_DIRECTORY = 30001;
    public static final int EXCEPTION_LOADING_FROM_JAR = 30002;
    public static final int EXCEPTION_PROCESSING_PERSISTENCE_UNIT = 30003;
    public static final int EXCEPTION_PROCESSING_PERSISTENCE_XML = 30004;
    public static final int EXCEPTION_SEARCHING_FOR_PERSISTENCE_RESOURCES = 30005;
    public static final int EXCEPTION_SEARCHING_FOR_ENTITIES = 30006;
    public static final int EXCEPTION_LOADING_CLASS = 30007;
    public static final int FILE_PATH_MISSING_EXCEPTION = 30008;
    public static final int EXCEPTION_LOADING_FROM_URL = 30009;
    public static final int EXCEPTION_OPENING_ORM_XML = 30010;
    public static final int COULD_NOT_GET_CLASS_NAMES_FROM_URL = 30011;
    public static final int COULD_NOT_GET_PERSISTENCE_UNIT_INFO_FROM_URL = 30012;

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    public PersistenceUnitLoadingException() {
        super();
    }

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected PersistenceUnitLoadingException(String message) {
        super(message);
    }

    /**
     * INTERNAL:
     * TopLink exceptions should only be thrown by TopLink.
     */
    protected PersistenceUnitLoadingException(String message, Throwable internalException) {
        super(message);
        setInternalException(internalException);
    }
    
    public static PersistenceUnitLoadingException exceptionLoadingFromDirectory(File directory, Exception cause) {
        Object[] args = { directory };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, EXCEPTION_LOADING_FROM_DIRECTORY, args), cause);
        loadingException.setResourceName(directory.toString());
        loadingException.setErrorCode(EXCEPTION_LOADING_FROM_DIRECTORY);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException filePathMissingException(String filePath) {
        Object[] args = { filePath };
        
        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, FILE_PATH_MISSING_EXCEPTION, args));
        loadingException.setResourceName(filePath);
        loadingException.setErrorCode(FILE_PATH_MISSING_EXCEPTION);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException exceptionLoadingFromJar(URL jarFile, Exception cause) {
        Object[] args = { jarFile };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, EXCEPTION_LOADING_FROM_JAR, args), cause);
        loadingException.setResourceName(jarFile.toString());
        loadingException.setErrorCode(EXCEPTION_LOADING_FROM_JAR);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException exceptionLoadingFromUrl(String url, Exception cause) {
        Object[] args = { url };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, EXCEPTION_LOADING_FROM_URL, args), cause);
        loadingException.setResourceName(url);
        loadingException.setErrorCode(EXCEPTION_LOADING_FROM_URL);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException exceptionProcessingPersistenceUnit(URL url, Exception cause) {
        Object[] args = { url };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, EXCEPTION_PROCESSING_PERSISTENCE_UNIT, args), cause);
        loadingException.setResourceName(url.toString());
        loadingException.setErrorCode(EXCEPTION_PROCESSING_PERSISTENCE_UNIT);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException exceptionProcessingPersistenceXML(URL url, Exception cause) {
        Object[] args = { url };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, EXCEPTION_PROCESSING_PERSISTENCE_XML, args), cause);
        loadingException.setResourceName(url.toString());
        loadingException.setErrorCode(EXCEPTION_PROCESSING_PERSISTENCE_XML);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException exceptionSearchingForPersistenceResources(ClassLoader loader, Exception cause) {
        Object[] args = { loader };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, EXCEPTION_SEARCHING_FOR_PERSISTENCE_RESOURCES, args), cause);
        loadingException.setErrorCode(EXCEPTION_SEARCHING_FOR_PERSISTENCE_RESOURCES);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException exceptionSearchingForEntities(URL url, Exception cause) {
        Object[] args = { url };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, EXCEPTION_SEARCHING_FOR_ENTITIES, args), cause);
        loadingException.setResourceName(url.toString());
        loadingException.setErrorCode(EXCEPTION_SEARCHING_FOR_ENTITIES);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException exceptionLoadingClassWhileLookingForAnnotations(String className, Exception cause) {
        Object[] args = { className };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, EXCEPTION_LOADING_CLASS, args), cause);
        loadingException.setErrorCode(EXCEPTION_LOADING_CLASS);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException exceptionLoadingORMXML(String fileName, Exception cause) {
        Object[] args = { fileName };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, EXCEPTION_OPENING_ORM_XML, args), cause);
        loadingException.setResourceName(fileName);
        loadingException.setErrorCode(EXCEPTION_OPENING_ORM_XML);
        return loadingException;
    }
    
    public static PersistenceUnitLoadingException couldNotGetClassNamesFromUrl(URL url) {
        Object[] args = { url };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, COULD_NOT_GET_CLASS_NAMES_FROM_URL, args));
        loadingException.setResourceName(url.toString());
        loadingException.setErrorCode(COULD_NOT_GET_CLASS_NAMES_FROM_URL);
        return loadingException;
    } 
    
    public static PersistenceUnitLoadingException couldNotGetUnitInfoFromUrl(URL url) {
        Object[] args = { url };

        PersistenceUnitLoadingException loadingException = new PersistenceUnitLoadingException(ExceptionMessageGenerator.buildMessage(PersistenceUnitLoadingException.class, COULD_NOT_GET_PERSISTENCE_UNIT_INFO_FROM_URL, args));
        loadingException.setResourceName(url.toString());
        loadingException.setErrorCode(COULD_NOT_GET_PERSISTENCE_UNIT_INFO_FROM_URL);
        return loadingException;
    } 
    public String getResourceName(){
        return resourceName;
    }
    
    public void setResourceName(String resourceName){
        this.resourceName = resourceName;        
    }
    
}