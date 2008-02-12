/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.transport.naming.RMIRegistryNamingServiceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class RMIRegistryNamingServiceConfig
 * 
 * @see RMIRegistryNamingServiceConfig
 * 
 * @author Tran Le
 */
public final class RMIRegistryNamingServiceAdapter extends SCAdapter {

	public final static String URL_PROPERTY = "urlConfig";

	/**
	 * Creates a new Discovery for the specified model object.
	 */
	RMIRegistryNamingServiceAdapter( SCAdapter parent, RMIRegistryNamingServiceConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new Discovery.
	 */
	protected RMIRegistryNamingServiceAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final RMIRegistryNamingServiceConfig namingService() {
		
		return ( RMIRegistryNamingServiceConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new RMIRegistryNamingServiceConfig();
	}
	/**
	 * Returns this config model property.
	 */
	public String getURL() {
		
		return this.namingService().getURL();
	}
	/**
	 * Sets this config model property.
	 */
	public void setURL( String url) {
		
		Object old = this.namingService().getURL();
		
		this.namingService().setURL( url);
		this.firePropertyChanged( URL_PROPERTY, old, url);
	}
}