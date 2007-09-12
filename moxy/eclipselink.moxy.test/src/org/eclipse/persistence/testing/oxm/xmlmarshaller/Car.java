/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

public class Car 
{
	private String license;

	public Car()
	{
	}

	public String getLicense()
	{
		return license;
	}

	public void setLicense(String license)
	{
		this.license = license;
	}
	public String toString()
	{
		return "Car: " + getLicense();
	}

	public boolean equals(Object car)
	{
		if(car instanceof Car)
			return getLicense().equals(((Car)car).getLicense());
		return false;
	}
}