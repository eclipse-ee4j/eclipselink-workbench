/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;

import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.StoredProcedureParameter;
import static org.eclipse.persistence.annotations.Direction.OUT;
import static org.eclipse.persistence.annotations.Direction.IN_OUT;

/**
 * <p><b>Purpose</b>: Represents the mailing address on an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */
@Entity
@Table(name="CMP3_ADDRESS")
@NamedNativeQueries({
    @NamedNativeQuery(
        name="findAllSQLAddresses", 
        query="select * from CMP3_ADDRESS",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllSQLAddressesByCity_QuestionMark_Number", 
        query="select * from CMP3_ADDRESS where city=?1",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllSQLAddressesByCity_QuestionMark", 
        query="select * from CMP3_ADDRESS where city=?",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllSQLAddressesByCityAndCountry_QuestionMark_Number", 
        query="select * from CMP3_ADDRESS where city=?1 and country=?2",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    ),
    @NamedNativeQuery(
        name="findAllSQLAddressesByCityAndCountry_QuestionMark", 
        query="select * from CMP3_ADDRESS where city=? and country=?",
        resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class
    )}
)
@NamedQuery(
    name="findAllAddressesByPostalCode", 
    query="SELECT OBJECT(address) FROM Address address WHERE address.postalCode = :postalcode"
)
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
    name="SProcAddress",
    resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class,
    procedureName="SProc_Read_Address",
    procedureParameters={@StoredProcedureParameter(procedureParameterDirection=IN_OUT, name="address_id_v", queryParameter="ADDRESS_ID", type=Integer.class),
                         @StoredProcedureParameter(procedureParameterDirection=OUT, name="street_v", queryParameter="STREET", type=String.class),
                         @StoredProcedureParameter(procedureParameterDirection=OUT, name="city_v", queryParameter="CITY", type=String.class),
                         @StoredProcedureParameter(procedureParameterDirection=OUT, name="country_v", queryParameter="COUNTRY", type=String.class),
                         @StoredProcedureParameter(procedureParameterDirection=OUT, name="province_v", queryParameter="PROVINCE", type=String.class),
                         @StoredProcedureParameter(procedureParameterDirection=OUT, name="p_code_v", queryParameter="P_CODE", type=String.class)}),
    @NamedStoredProcedureQuery(
    name="SProcInOut",
    resultClass=org.eclipse.persistence.testing.models.jpa.advanced.Address.class,
    procedureName="SProc_Read_InOut",
    procedureParameters={@StoredProcedureParameter(procedureParameterDirection=IN_OUT, name="address_id_v", queryParameter="ADDRESS_ID", type=Long.class),
                         @StoredProcedureParameter(procedureParameterDirection=OUT, name="street_v", queryParameter="STREET", type=String.class)})
})
public class Address implements Serializable {
	private int id;
	private String street;
	private String city;
    private String province;
    private String postalCode;
    private String country;
	private Collection<Employee> employees;

    public Address() {
        city = "";
        province = "";
        postalCode = "";
        street = "";
        country = "";
        this.employees = new Vector<Employee>();
    }

    public Address(String street, String city, String province, String country, String postalCode) {
        this.street = street;
        this.city = city;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.employees = new Vector<Employee>();
    }

	@Id
    // BUG 5079973 - this should be a valid specification, that is the generator
    // trumps the AUTO strategy defaulting.
    // BUG 6273529 - SHOULD BE ABLE TO SKIP SEQUENCENAME IN SEQUENCEGENERATOR
    // In case sequenceName is not specified generator's name used instead.
    @GeneratedValue(generator="ADDRESS_SEQ")
    @SequenceGenerator(name="ADDRESS_SEQ", allocationSize=25)
	@Column(name="ADDRESS_ID")
	public int getId() { 
        return id; 
    }
    
	public void setId(int id) { 
        this.id = id; 
    }

	public String getStreet() { 
        return street; 
    }
    
	public void setStreet(String street) { 
        this.street = street; 
    }

	public String getCity() { 
        return city; 
    }
    
	public void setCity(String city) { 
        this.city = city; 
    }

	public String getProvince() { 
        return province; 
    }
        
	public void setProvince(String province) { 
        this.province = province; 
    }

	@Column(name="P_CODE")
	public String getPostalCode() { 
        return postalCode; 
    }
    
	public void setPostalCode(String postalCode) { 
        this.postalCode = postalCode; 
    }

	public String getCountry() { 
        return country; 
    }
    
	public void setCountry(String country) { 
        this.country = country;
    }
    
	@OneToMany(cascade=ALL, mappedBy="address")
	public Collection<Employee> getEmployees() { 
        return employees; 
    }
    
    public void setEmployees(Collection<Employee> employees) {
		this.employees = employees;
	}
}