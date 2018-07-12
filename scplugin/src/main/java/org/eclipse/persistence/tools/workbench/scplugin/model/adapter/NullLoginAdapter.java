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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

/**
 * Session Configuration model adapter class for Null Login
 *
 * @author Tran Le
 */
public class NullLoginAdapter extends LoginAdapter {

    // singleton
    private static NullLoginAdapter INSTANCE;

    /**
     * Return the singleton.
     */
    public static synchronized LoginAdapter instance() {
        if( INSTANCE == null) {
            INSTANCE = new NullLoginAdapter();
        }
        return INSTANCE;
    }

    /**
     * Ensure non-instantiability.
     */
    private NullLoginAdapter() {
        super();
    }

    @Override
    protected Object buildModel() {

        return null;
    }
    /**
     * Returns the datasource platform class from user's preference.
     */
    @Override
    protected String getDefaultPlatformClassName() {

        return null;
    }
    /**
     * Returns true when this uses the default Platform Class.
     */
    @Override
    protected boolean platformClassIsDefault() {
        return true;
    }
    /**
     * Returns this config model property.
     */
    @Override
    public String getEncryptionClass() {

        return "";
    }
    /**
     * Returns this config model property.
     */
    @Override
    public String getPassword() {

        return "";
    }
    @Override
    public Boolean isSavePassword() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean isSaveUsername() {
        return Boolean.FALSE;
    }

    /**
     * Returns this config model property.
     */
    @Override
    public String getPlatformClass() {

        return "";
    }
    /**
     * Returns this config model property.
     */
    @Override
    public String getPlatformName() {

        return "";
    }
    /**
     * Returns this config model property.
     */
    @Override
    public String getTableQualifier() {

        return "";
    }
    /**
     * Returns this userName.
     */
    @Override
    public String getUserName() {

        return "";
    }

    /**
     * Sets this config model property.
     */
    @Override
    public void setEncryptionClass( String value) {
    }
    /**
    * Sets usesExternalConnectionPooling and the config model.
    */
    @Override
    public void setExternalConnectionPooling( boolean value) {
    }
    /**
     * Sets this config model property.
     */
    @Override
    public void setPassword( String value) {
    }

    @Override
    public void setSavePassword(Boolean savePassword) {
    }

    @Override
    public void setSaveUsername(Boolean saveUsername) {
    }

    /**
     * Sets this config model property.
     */
    @Override
    public void setPlatformClass( String value) {
    }
    /**
     * Sets this config model property.
     */
    @Override
    public void setTableQualifier( String value) {
    }
    /**
     * Sets this userName and the config model.
     */
    @Override
    public void setUserName( String name) {
    }
    /**
     * Sets this config model property.
     */
    @Override
    public void setUsesExternalTransactionController( boolean value) {
    }
    /**
     * Returns usesExternalConnectionPooling.
     */
    @Override
    public boolean usesExternalConnectionPooling() {

        return false;
    }
    /**
     * Returns this config model property..
     */
    @Override
    public boolean usesExternalTransactionController() {

        return false;
    }
}
