/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.employee.interfaces;

import java.sql.Timestamp;

/**
 * <b>Purpose</b>: Larger scale projects within the Employee Demo
 * <p><b>Description</b>: LargeProject is a concrete subclass of Project. It is instantiated for Projects with type = 'L'. The additional
 * information (budget, & milestoneVersion) are mapped from the LPROJECT table.
 */
public interface LargeProject extends Project {
    double getBudget();

    Timestamp getMilestoneVersion();

    void setBudget(double budget);

    void setMilestoneVersion(Timestamp milestoneVersion);
}