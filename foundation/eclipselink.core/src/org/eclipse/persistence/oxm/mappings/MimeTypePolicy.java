/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.oxm.mappings;

public interface MimeTypePolicy {
	/**
	 * return a MIME type string
	 * @param anObject - fixed non-dynamic implementors will ignore this parameter
	 * @return String
	 */
	String getMimeType(Object anObject);
	
}