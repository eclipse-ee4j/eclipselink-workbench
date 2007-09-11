/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.descriptor.rootelement;

public class MailingAddress {
    private String street;

    public MailingAddress() {
        super();
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public boolean equals(Object theObject) {
        if (theObject instanceof MailingAddress) {
            if (((street == null) && (((MailingAddress)theObject).getStreet() == null)) || (street.equals(((MailingAddress)theObject).getStreet()))) {
                return true;
            }
        }
        return false;
    }
}