/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta;

/**
 * Thrown when there are problems loading an ExternalClass.
 */
public class ExternalClassNotFoundException extends Exception {

	/**
	 * Constructs an ExternalClassNotFoundException with no detail message.
	 */
	public ExternalClassNotFoundException() {
		super();
	}
	
	/**
	 * Constructs an ExternalClassNotFoundException with the
	 * specified detail message.
	 * 
	 * @param message
	 */
	public ExternalClassNotFoundException(String message) {
		super(message);
	}
	
	/**
	 * Constructs an ExternalClassNotFoundException with the
	 * specified detail message and optional exception that was raised
	 * while loading the external class.
	 * 
	 * @param message
	 * @param cause
	 */
	public ExternalClassNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructs an ExternalClassNotFoundException with the
	 * optional exception that was raised while loading the external class.
	 * 
	 * @param cause
	 */
	public ExternalClassNotFoundException(Throwable cause) {
		super(cause);
	}

}