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

import org.eclipse.persistence.internal.sessions.factories.model.clustering.SunCORBAJNDIClusteringConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class SunCORBAJNDIClusteringConfig
 * 
 * @see SunCORBAJNDIClusteringConfig
 * 
 * @author Tran Le
 */
public final class SunCORBAJNDIClusteringAdapter extends JNDIClusteringServiceAdapter {
	/**
	 * Creates a new SunCORBAJNDIClusteringAdapter for the specified model object.
	 */
    SunCORBAJNDIClusteringAdapter( SCAdapter parent, SunCORBAJNDIClusteringConfig scConfig) {
		
		super( parent, scConfig);
	}
	/**
	 * Creates a new SunCORBAJNDIClusteringAdapter.
	 */
	protected SunCORBAJNDIClusteringAdapter( SCAdapter parent) {
		
		super( parent);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final SunCORBAJNDIClusteringConfig config() {
		
		return ( SunCORBAJNDIClusteringConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
		return new SunCORBAJNDIClusteringConfig();
	}
}