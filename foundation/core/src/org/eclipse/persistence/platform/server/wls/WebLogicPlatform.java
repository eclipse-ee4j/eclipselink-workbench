/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.wls;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.transaction.wls.WebLogicTransactionController;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WebLogic-specific server behaviour.
 *
 * This platform overrides:
 *
 * getExternalTransactionControllerClass(): to use the WebLogic-specific controller class
 * getServerNameAndVersion(): to call the WebLogic library for this information
 */
public class WebLogicPlatform extends ServerPlatformBase {

    /**
     * INTERNAL:
     * Default Constructor: All behaviour for the default constructor is inherited
     */
    public WebLogicPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.disableRuntimeServices();
    }

    /**
     * PUBLIC: getServerNameAndVersion(): Talk to the WebLogic class library, and get the server name
     * and version
     *
     * @return String serverNameAndVersion
     */
    public String getServerNameAndVersion() {
    	// TODO: Replace with reflection
    	throw new RuntimeException("TODO: Replace with reflective access");
        //return weblogic.version.getBuildVersion();
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction controller to use
     * for WebLogic. This is read-only.
     *
     * @return Class externalTransactionControllerClass
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see ServerPlatformBase.isJTAEnabled()
     * @see ServerPlatformBase.disableJTA()
     * @see ServerPlatformBase.initializeExternalTransactionController()
     */
    public Class getExternalTransactionControllerClass() {
    	if (externalTransactionControllerClass == null){
    		externalTransactionControllerClass = WebLogicTransactionController.class;
    	}
        return externalTransactionControllerClass;
    }
}