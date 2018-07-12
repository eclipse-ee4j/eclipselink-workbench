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
package org.eclipse.persistence.tools.workbench.test.utility.diff.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class InheritanceEmployee extends SimpleEmployee {
    private Address address;
    private Collection dependents;
    private List cars;
    private Map phoneNumbers;    // keyed by description
    private Collection underlings;
    private List vacationBackups;
    private Map eatingPartners;    // keyed by meal


    public InheritanceEmployee(int id, String name) {
        super(id, name);
        this.dependents = new ArrayList();
        this.cars = new ArrayList();
        this.phoneNumbers = new HashMap();
        this.underlings = new ArrayList();
        this.vacationBackups = new ArrayList();
        this.eatingPartners = new HashMap();
    }

    @Override
    public Address getAddress() {
        return this.address;
    }
    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public Iterator dependents() {
        return this.dependents.iterator();
    }
    @Override
    public Dependent addDependent(String depName, String depDescription) {
        Dependent dependent = new Dependent(depName, depDescription);
        this.dependents.add(dependent);
        return dependent;
    }
    @Override
    public void clearDependents() {
        this.dependents.clear();
    }

    @Override
    public ListIterator cars() {
        return this.cars.listIterator();
    }
    @Override
    public Car addCar(String carName, String carDescription) {
        Car car = new Car(carName, carDescription);
        this.cars.add(car);
        return car;
    }
    @Override
    public void clearCars() {
        this.cars.clear();
    }

    @Override
    public Iterator phoneNumbers() {
        return this.phoneNumbers.entrySet().iterator();
    }
    @Override
    public PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number, String extension) {
        PhoneNumber phone = new PhoneNumber(areaCode, exchange, number, extension);
        this.phoneNumbers.put(phoneDescription, phone);
        return phone;
    }
    @Override
    public PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number) {
        PhoneNumber phone = new PhoneNumber(areaCode, exchange, number);
        this.phoneNumbers.put(phoneDescription, phone);
        return phone;
    }
    @Override
    public void clearPhoneNumbers() {
        this.phoneNumbers.clear();
    }
    @Override
    public PhoneNumber getPhoneNumber(String phoneDescription) {
        return (PhoneNumber) this.phoneNumbers.get(phoneDescription);
    }

    @Override
    public Iterator underlings() {
        return this.underlings.iterator();
    }
    @Override
    public void addUnderling(Employee underling) {
        this.underlings.add(underling);
    }
    @Override
    public void clearUnderlings() {
        this.underlings.clear();
    }

    @Override
    public Iterator vacationBackups() {
        return this.vacationBackups.iterator();
    }
    @Override
    public void addVacationBackup(Employee vacationBackup) {
        this.vacationBackups.add(vacationBackup);
    }
    @Override
    public void clearVacationBackups() {
        this.vacationBackups.clear();
    }

    @Override
    public Iterator eatingPartners() {
        return this.eatingPartners.entrySet().iterator();
    }
    @Override
    public void setEatingPartner(String meal, Employee partner) {
        this.eatingPartners.put(meal, partner);
    }
    @Override
    public void clearEatingPartners() {
        this.eatingPartners.clear();
    }
    @Override
    public Employee getEatingPartner(String meal) {
        return (Employee) this.eatingPartners.get(meal);
    }

}
