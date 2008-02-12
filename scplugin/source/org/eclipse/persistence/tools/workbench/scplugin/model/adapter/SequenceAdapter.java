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

import org.eclipse.persistence.internal.sessions.factories.model.sequencing.SequenceConfig;

/**
 * Session Configuration model adapter class for the 
 * TopLink Foudation Library class SequenceConfig
 * 
 * @see SequenceConfig
 * 
 * @author Tran Le
 */
public abstract class SequenceAdapter extends SCAdapter implements Nominative {
	/**
	 * Creates a new SequenceAdapter for the specified model object.
	 */
	SequenceAdapter( SCAdapter parent, SequenceConfig scConfig) {
			
		super( parent, scConfig);
	}
	/**
	 * Creates a new SequenceAdapter.
	 */
	protected SequenceAdapter( SCAdapter parent, String name, int preallocationSize) {
			
		super( parent);	
		
		this.setName( name);	
		this.setPreallocationSize( preallocationSize);
	}
	/**
	 * Returns this Config Model Object.
	 */
	private final SequenceConfig sequence() {
			
		return ( SequenceConfig)this.getModel();
	}
	/**
	 * Factory method for building this model.
	 */
	protected Object buildModel() {
	
		return new SequenceConfig();
	}
	/**
	 * Returns this config model property.
	 */
	public String getName() {
			
		return this.sequence().getName();
	}
	/**
	 * Sets this config model property.
	 */
	void setName( String name) {

		this.sequence().setName( name);
	}
	/**
	 * Returns this config model property.
	 */
	int getPreallocationSize() {
			
		Integer size = this.sequence().getPreallocationSize();
		return (size != null) ? size.intValue() : 0;
	}
	/**
	 * Sets this config model property.
	 */
	void setPreallocationSize( int size) {
			
		this.sequence().setPreallocationSize( new Integer( size));
	}
		
	public String displayString() {
			
		return this.getName();
	}
	
	public void toString( StringBuffer sb) {
			
		sb.append( this.getName());
	}
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean isDefault() {
			
		return false;
	}
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean isNative() {
			
		return false;
	}	
	/**
	 * Temporary support for Multiple Sequencing schema.
	 */
	public boolean isCustom() {
			
		return false;
	}
}