/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import java.util.Calendar;
import java.util.Date;

public class DataHelperToTimeTest extends DataHelperTestCases {
    public DataHelperToTimeTest(String name) {
        super(name);
    }

    public void testToTimeWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.HOUR, 12);
        controlCalendar.set(Calendar.MINUTE, 23);
        controlCalendar.set(Calendar.SECOND, 11);
        controlCalendar.set(Calendar.MILLISECOND, 1);
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toTime(controlDate);
        this.assertEquals("12:23:11.001", tm);
    }

    public void testToTimeWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toTime(controlDate);
        this.assertEquals("00:00:00.0", tm);
    }

    public void testToTimeWithNullInput() {
        Date controlDate = null;
        String tm = dataHelper.toTime(controlDate);
        this.assertNull(tm);
    }
}