/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.TABLE;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CollectionTable;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.PrivateOwned;
import org.eclipse.persistence.annotations.TypeConverter;
import org.eclipse.persistence.annotations.OptimisticLocking;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * <p><b>Purpose</b>: Represents the department of an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */
@Entity(name="Department")
@Table(name="CMP3_FA_DEPT")
@NamedNativeQuery(
    name="findAllSQLDepartments", 
    query="select * from CMP3_FA_DEPT",
    resultClass=org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.Department.class
)
public class Department implements Serializable {
	@Id
    @GeneratedValue(strategy=TABLE, generator="FA_DEP_TABLE_GENERATOR")
	@TableGenerator(
        name="FA_DEP_TABLE_GENERATOR", 
        table="CMP3_FA_DEPARTMENT_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="DEPT_SEQ"
    )
	private Integer id;
    private String name;
    
	@OneToMany(fetch=EAGER, mappedBy="department")
	private Collection<Employee> employees;
	@OneToMany(fetch=EAGER, cascade=PERSIST)
    private Collection<Employee> managers;
    @OneToMany(mappedBy="department")
    @PrivateOwned
    private Map<Integer, Equipment> equipment;

    public Department() {
        this("");
    }

    public Department(String name) {
        this.name = name;
        this.managers = new Vector();
        this.equipment = new HashMap<Integer, Equipment>();
    }
    
    public void addEquipment(Equipment e) {
        this.equipment.put(e.getId(), e);
        e.setDepartment(this);
    }

    public void addManager(Employee employee) {
        if (employee != null && managers != null && !managers.contains(employee)) { 
            this.managers.add(employee); 
        }
    }    
	
	public Collection<Employee> getEmployees() { 
        return employees; 
    }
        
	public Map<Integer, Equipment> getEquipment() { 
        return equipment; 
    }    
	
	public Integer getId() { 
        return id; 
    }
    
    //To test default 1-M mapping    
    public Collection<Employee> getManagers() {
        return managers;
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setEmployees(Collection<Employee> employees) {
		this.employees = employees;
	}
    
    public void setEquipment(Map<Integer, Equipment> equipment) {
		this.equipment = equipment;
	}
    
	public void setId(Integer id) { 
        this.id = id; 
    }
    
    public void setManagers(Collection<Employee> managers) {
        this.managers = managers;
    }
    
	public void setName(String name) { 
        this.name = name; 
    }
}