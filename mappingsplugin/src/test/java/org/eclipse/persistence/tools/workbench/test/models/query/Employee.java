/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.test.models.query;

import java.math.BigDecimal;
import java.util.Vector;

import org.eclipse.persistence.indirection.ValueHolderInterface;

public class Employee implements EmployeeInterface
{

    public BigDecimal id;
    /** Direct-to-field mapping, String -> VARCHAR. */
    public String firstName;
    /** Direct-to-field mapping, String -> VARCHAR. */
    public String lastName;

    public ValueHolderInterface manager;
    public ValueHolderInterface phoneNumbers;
    public EmploymentPeriod period;

    public Employee()
    {
        this.firstName = "";
        this.lastName = "";
    }


    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     */

    @Override
    public void addPhoneNumber(PhoneNumber phoneNumber)
    {
        getPhoneNumbers().addElement(phoneNumber);
        phoneNumber.setOwner(this);
    }

    @Override
    public String getFirstName()
    {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */

    @Override
    public EmployeeInterface getManager()
    {
        return (EmployeeInterface) manager.getValue();
    }

    @Override
    public EmploymentPeriod getPeriod()
    {
        return period;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */

    @Override
    public Vector getPhoneNumbers()
    {
        return (Vector) phoneNumbers.getValue();
    }

    /**
     * Remove the phone number.
     * The phone number's owner must not be set to null as it is part of it primary key,
     * and you can never change the primary key of an existing object.
     * Only in independent relationships should you null out the back reference.
     */

    @Override
    public void removePhoneNumber(PhoneNumber phoneNumber)
    {
        getPhoneNumbers().removeElement(phoneNumber);
    }

    @Override
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * For bi-directional relationships, it is important to maintain both sides of the relationship when changing it.
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */

    @Override
    public void setManager(EmployeeInterface manager)
    {
        this.manager.setValue(manager);
    }

    @Override
    public void setPeriod(EmploymentPeriod period)
    {
        this.period = period;
    }

    /**
     * Notice that the usage of value holders does not effect the public interface or usage of the class.
     * The get/set methods must however be changed to wrap/unwrap the value holder.
     */

    @Override
    public void setPhoneNumbers(Vector phoneNumbers)
    {
        this.phoneNumbers.setValue(phoneNumbers);
    }

}
