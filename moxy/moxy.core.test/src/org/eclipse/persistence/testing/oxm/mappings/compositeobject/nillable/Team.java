/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/* $Header: Team.java 15-nov-2006.13:22:16 bdoughan Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    bdoughan    11/14/06 - 
    mfobrien    10/26/06 - Creation
 */

/**
 *  @version $Header: Team.java 15-nov-2006.13:22:16 bdoughan Exp $
 *  @author  mfobrien
 *  @since   11.1
 */

package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nillable;

public class Team {
    public static final int DEFAULT_ID = 123;
    
    // Factory method
    public static Team getInstance() {
        return new Team(DEFAULT_ID, new Employee(), "Eng");
    }
    
    private int id;
    private Employee manager;
    private String name;

    private boolean isSetManager = false;

    public Team() {
        super();        
    }

    public Team(int id) {
        super();
        this.id = id;
    }

    public Team(int id, Employee manager, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    // override default equals
    public boolean equals(Object object) {
      try {
          Team team = (Team) object;
          if(this.getId() != team.getId()) {
              return false;
          }
          if(this.isSetManager() != team.isSetManager()) {
              return false;
          }
          if(this.getManager() != team.getManager()) {
            if(this.getManager() == null) {
                return false;
            }
            if(!this.getManager().equals(team.getManager())) {
                return false;
            }
          }
          if(this.getName() != team.getName()) {
            if(this.getName() == null) {
                return false;
            }
            if(!this.getName().equals(team.getName())) {
                return false;
            }
          }
          return true;
      } catch(ClassCastException e) {
          return false;
      }
    }

    public int getId() {
        return id;
    }

    public Employee getManager() {
		return manager;
	}

    public String getName() {
        return name;
    }

    public boolean isSetManager() {
        return isSetManager;
    }

    public void setId(int id) {
        this.id = id;
    } 

    public void setManager(Employee manager) {
	    // no unset for now
	    isSetManager = true;
		this.manager = manager;
	}

	public void setName(String name) {
        this.name = name;
    }

    public String toString() {
    	StringBuffer aBuffer = new StringBuffer();
    	aBuffer.append("Team(id=");
    	aBuffer.append(getId());
    	aBuffer.append(", manager=");
        aBuffer.append(getManager());
        aBuffer.append(", isSetManager=");
        aBuffer.append(isSetManager());
        aBuffer.append(", name=");
        aBuffer.append(getName());
        aBuffer.append(")");
        return aBuffer.toString();
    }

}
