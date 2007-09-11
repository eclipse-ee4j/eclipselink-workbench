/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/


package org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners;

/**
 * A listener for the SportsCar entity.
 * 
 * It implements the following annotations:
 * - PreRemove
 * - PostRemove
 * - PreUpdate
 * - PostUpdate
 * 
 * It overrides the following annotations:
 * - None
 * 
 * It inherits the following annotations:
 * - PostLoad from Vehicle.
 * - PrePersist from ListenerSuperclass
 * - PostPersist from FueledVehicleListener
 */
public class SportsCarListener extends ListenerSuperclass {
    public static int PRE_REMOVE_COUNT = 0;
    public static int POST_REMOVE_COUNT = 0;
    public static int PRE_UPDATE_COUNT = 0;
    public static int POST_UPDATE_COUNT = 0;
    
	public void preRemove(Object sportsCar) {
        PRE_REMOVE_COUNT++;
	}

	public void postRemove(Object sportsCar) {
        POST_REMOVE_COUNT++;
	}

	public void preUpdate(Object sportsCar) {
        PRE_UPDATE_COUNT++;
	}

	public void postUpdate(Object sportsCar) {
        POST_UPDATE_COUNT++;
	}
}