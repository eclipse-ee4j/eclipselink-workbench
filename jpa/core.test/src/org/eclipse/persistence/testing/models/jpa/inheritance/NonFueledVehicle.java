/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.*;
import static javax.persistence.InheritanceType.*;

@Entity
@Table(name="CMP3_NONFUEL_VEH")
@DiscriminatorValue("NF")
public class NonFueledVehicle extends Vehicle {
    private String color;

    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
}