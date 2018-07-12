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

import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

public class SimpleEmployee implements Employee {
    private int id;
    private String name;
    private float salary;
    private String position;
    private Set comments;

    public SimpleEmployee(int id, String name) {
        super();
        this.id = id;
        this.name = name;
        this.salary = 0;
        this.position = "";
        this.comments = new HashSet();
    }

    @Override
    public int getId() {
        return this.id;
    }
    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public void setName(String string) {
        this.name = string;
    }

    @Override
    public float getSalary() {
        return this.salary;
    }
    @Override
    public void setSalary(float f) {
        this.salary = f;
    }

    @Override
    public String getPosition() {
        return this.position;
    }
    @Override
    public void setPosition(String string) {
        this.position = string;
    }

    @Override
    public Iterator comments() {
        return this.comments.iterator();
    }
    @Override
    public void addComment(String comment) {
        this.comments.add(comment);
    }
    @Override
    public void clearComments() {
        this.comments.clear();
    }

    @Override
    public String toString() {
        return StringTools.buildToStringFor(this, this.name);
    }


    // ********** unsupported stuff **********

    @Override
    public Address getAddress() {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setAddress(Address address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator dependents() {
        throw new UnsupportedOperationException();
    }
    @Override
    public Dependent addDependent(String depName, String depDescription) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void clearDependents() {
        throw new UnsupportedOperationException();
    }
    @Override
    public Dependent dependentNamed(String depName) {
        for (Iterator stream = this.dependents(); stream.hasNext(); ) {
            Dependent dependent = (Dependent) stream.next();
            if (dependent.getName().equals(depName)) {
                return dependent;
            }
        }
        throw new IllegalArgumentException("dependent not found: " + depName);
    }

    @Override
    public ListIterator cars() {
        throw new UnsupportedOperationException();
    }
    @Override
    public Car addCar(String carName, String carDescription) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void clearCars() {
        throw new UnsupportedOperationException();
    }
    @Override
    public Car carNamed(String carName) {
        for (Iterator stream = this.cars(); stream.hasNext(); ) {
            Car car = (Car) stream.next();
            if (car.getName().equals(carName)) {
                return car;
            }
        }
        throw new IllegalArgumentException("car not found: " + carName);
    }

    @Override
    public PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number, String extension) {
        throw new UnsupportedOperationException();
    }
    @Override
    public PhoneNumber addPhoneNumber(String phoneDescription, String areaCode, String exchange, String number) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void clearPhoneNumbers() {
        throw new UnsupportedOperationException();
    }
    @Override
    public PhoneNumber getPhoneNumber(String phoneDescription) {
        throw new UnsupportedOperationException();
    }
    @Override
    public Iterator phoneNumbers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator underlings() {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addUnderling(Employee underling) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void clearUnderlings() {
        throw new UnsupportedOperationException();
    }
    @Override
    public Employee underlingNamed(String underlingName) {
        for (Iterator stream = this.underlings(); stream.hasNext(); ) {
            Employee underling = (Employee) stream.next();
            if (underling.getName().equals(underlingName)) {
                return underling;
            }
        }
        throw new IllegalArgumentException("underling not found: " + underlingName);
    }

    @Override
    public Iterator vacationBackups() {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addVacationBackup(Employee vacationBackup) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void clearVacationBackups() {
        throw new UnsupportedOperationException();
    }
    @Override
    public Employee vacationBackupNamed(String vacationBackupName) {
        for (Iterator stream = this.vacationBackups(); stream.hasNext(); ) {
            Employee vacationBackup = (Employee) stream.next();
            if (vacationBackup.getName().equals(vacationBackupName)) {
                return vacationBackup;
            }
        }
        throw new IllegalArgumentException("vacation backup not found: " + vacationBackupName);
    }

    @Override
    public Iterator eatingPartners() {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setEatingPartner(String meal, Employee partner) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void clearEatingPartners() {
        throw new UnsupportedOperationException();
    }
    @Override
    public Employee getEatingPartner(String meal) {
        throw new UnsupportedOperationException();
    }

}
