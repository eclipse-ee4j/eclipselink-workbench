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

/**
 *  Any class that calls the initialize method on the JavaSECMPInitializer should implement this interface
 *  Implementers of this interface can restrict the provider that the initializer will initialize with.
 */
public interface PersistenceInitializationActivator {

    /**
     * Return whether the given class name identifies a persistence provider that is supported by
     * this PersistenceInitializationActivator
     */
    public boolean isPersistenceProviderSupported(String providerClassName);
}