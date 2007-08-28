/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance.typetests.any;

public class Customer {
    private Object contact;

    public Customer() {
    }

    public void setContact(Object theContact) {
        this.contact = theContact;
    }

    public Object getContact() {
        return contact;
    }

    public boolean equals(Object theCustomer) {
        if (theCustomer instanceof Customer) {
            return contact.equals(((Customer)theCustomer).getContact());
        }
        return false;
    }

    public String toString() {
        return "Customer: " + contact.toString();
    }
}