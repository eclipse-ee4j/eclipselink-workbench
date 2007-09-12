/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


 
package org.eclipse.persistence.testing.tests.ejb.ejbqltesting;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import javax.persistence.Query;


/**
 * <p>
 * <b>Purpose</b>: Test Unit EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for EJBQL functionality
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator
 * @see JUnitDomainObjectComparer
 */
 
 
//This test suite demonstrates the bug 4616218, waiting for bug fix
public class JUnitJPQLUnitTestSuite extends JUnitTestCase
{ 
  static JUnitDomainObjectComparer comparer; 
  
  public JUnitJPQLUnitTestSuite()
  {
      super();
  }

  public JUnitJPQLUnitTestSuite(String name)
  {
      super(name);
  }
  
  //This method is run at the end of EVERY test case method
  public void tearDown()
  {
      clearCache();
  }
  
  
  //This suite contains all tests contained in this class
  public static Test suite() 
  {
    TestSuite suite = new TestSuite();
    suite.setName("JUnitJPQLUnitTestSuite");
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectPhoneNumberAreaCode"));   
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectPhoneNumberAreaCodeWithEmployee"));   
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectPhoneNumberNumberWithEmployeeWithExplicitJoin"));   
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectPhoneNumberNumberWithEmployeeWithFirstNameFirst"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testSelectEmployeeWithSameParameterUsedMultipleTimes"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testOuterJoinOnOneToOne"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testOuterJoinPolymorphic"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testFirstResultOnNamedQuery"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testInvertedSelectionCriteriaNullPK"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testInvertedSelectionCriteriaInvalidQueryKey"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testMaxAndFirstResultsOnDataQuery"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testMaxAndFirstResultsOnDataQueryWithGroupBy"));
    suite.addTest(new JUnitJPQLUnitTestSuite("testDistinctSelectForEmployeeWithNullAddress"));

     return new TestSetup(suite) {
            
            //This method is run at the end of the SUITE only
            protected void tearDown() {
                clearCache();
            }
            
            //This method is run at the start of the SUITE only
            protected void setUp() {
            
                //get session to start setup
                DatabaseSession session = JUnitTestCase.getServerSession();
                
                //create a new EmployeePopulator
                EmployeePopulator employeePopulator = new EmployeePopulator();
                
                new AdvancedTableCreator().replaceTables(session);
                
                //initialize the global comparer object
                comparer = new JUnitDomainObjectComparer();
                
                //set the session for the comparer to use
                comparer.setSession((AbstractSession)session.getActiveSession());              
                
                //Populate the tables
                employeePopulator.buildExamples();
                
                //Persist the examples in the database
                employeePopulator.persistExample(session);               
            }            
        };
    
  }
    
    public Vector getAttributeFromAll(String attributeName, Vector objects, Class referenceClass){
	   
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        
        ClassDescriptor descriptor = em.getActiveSession().getClassDescriptor(referenceClass);
	    DirectToFieldMapping mapping = (DirectToFieldMapping)descriptor.getMappingForAttributeName(attributeName);
	    
	    Vector attributes = new Vector();
	    Object currentObject;
	    for(int i = 0; i < objects.size(); i++) {
            currentObject = objects.elementAt(i);
            if(currentObject.getClass() == ReportQueryResult.class) {
                attributes.addElement(
                    ((ReportQueryResult)currentObject).get(attributeName));
            } else {
                attributes.addElement(
                    mapping.getAttributeValueFromObject(currentObject));
            }
	    }
	    return attributes;
	}
    
    public void testFirstResultOnNamedQuery(){
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        clearCache();

        Query query = em.createNamedQuery("findAllEmployeesByFirstName");
        List initialList = query.setParameter("firstname", "Nancy").setFirstResult(0).getResultList();
        
        List secondList = query.setParameter("firstname", "Nancy").setFirstResult(1).getResultList();

        Iterator i = initialList.iterator();
        while (i.hasNext()){
        	assertTrue("Employee with incorrect name returned on first query.", ((Employee)i.next()).getFirstName().equals("Nancy"));
        }
        i = secondList.iterator();
        while (i.hasNext()){
        	assertTrue("Employee with incorrect name returned on second query.", ((Employee)i.next()).getFirstName().equals("Nancy"));
        }
    }
    
    public void testOuterJoinOnOneToOne(){
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        clearCache();
        em.getTransaction().begin();
        int initialSize = em.createQuery("SELECT e from Employee e JOIN e.address a").getResultList().size(); 
        Employee emp = new Employee();
        emp.setFirstName("Steve");
        emp.setLastName("Harp");
        em.persist(emp);
        em.flush();
        List result = em.createQuery("SELECT e from Employee e LEFT OUTER JOIN e.address a").getResultList();
        assertTrue("Outer join was not properly added to the query", initialSize + 1 == result.size());
        em.getTransaction().rollback();
    }

    public void testOuterJoinPolymorphic(){
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        clearCache();
        List resultList = null;
        try{
            resultList = em.createQuery("SELECT p FROM Project p").getResultList();
        } catch (Exception exception){
            fail("Exception caught while executing polymorphic query.  This may mean that outer join is not working correctly on your database platfrom: " + exception.toString());            
        }
        assertTrue("Incorrect number of projects returned.", resultList.size() == 15);
    }

  //This test case demonstrates the bug 4616218
  public void testSelectPhoneNumberAreaCode()
  {
        
        ExpressionBuilder employeeBuilder = new ExpressionBuilder();
		Expression phones = employeeBuilder.anyOf("phoneNumbers");
    	Expression whereClause = phones.get("areaCode").equal("613");
    	    
    	ReportQuery rq = new ReportQuery();
    	rq.setSelectionCriteria(whereClause);
		rq.addAttribute("areaCode", new ExpressionBuilder().anyOf("phoneNumbers").get("areaCode"));
    	rq.setReferenceClass(Employee.class);    
        rq.dontUseDistinct(); // distinct no longer used on joins in JPQL for gf bug 1395
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        Vector expectedResult = getAttributeFromAll("areaCode", (Vector)em.getActiveSession().executeQuery(rq),Employee.class);
        
        clearCache();
        
        List result = em.createQuery("SELECT phone.areaCode FROM Employee employee, IN (employee.phoneNumbers) phone " + 
		    "WHERE phone.areaCode = \"613\"").getResultList();                     
      
        Assert.assertTrue("SimpleSelectPhoneNumberAreaCode test failed !", comparer.compareObjects(result,expectedResult));    	
  }
  
  
    public void testSelectPhoneNumberAreaCodeWithEmployee()
    {
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal("Bob");
        exp = exp.and(employees.get("lastName").equal("Smith"));
        Employee emp = (Employee) em.getActiveSession().readAllObjects(Employee.class, exp).firstElement();
    
        PhoneNumber phone = (PhoneNumber) ((Vector)emp.getPhoneNumbers()).firstElement();
        String areaCode = phone.getAreaCode();
        String firstName = emp.getFirstName();

		ExpressionBuilder employeeBuilder = new ExpressionBuilder();
		Expression phones = employeeBuilder.anyOf("phoneNumbers");
    	Expression whereClause = phones.get("areaCode").equal(areaCode).and(
    	phones.get("owner").get("firstName").equal(firstName));
    	    
    	ReportQuery rq = new ReportQuery();
    	rq.setSelectionCriteria(whereClause);
    	rq.addAttribute("areaCode", phones.get("areaCode"));
		rq.setReferenceClass(Employee.class);
    	rq.dontMaintainCache();
    	
        Vector expectedResult = getAttributeFromAll("areaCode", (Vector)em.getActiveSession().executeQuery(rq),Employee.class);   	
        
    	clearCache();
    
    	String ejbqlString;
    	ejbqlString = "SELECT phone.areaCode FROM Employee employee, IN (employee.phoneNumbers) phone " + 
		    "WHERE phone.areaCode = \"" + areaCode + "\" AND phone.owner.firstName = \"" + firstName + "\"";    
        
        List result = em.createQuery(ejbqlString).getResultList();                     
        
        Assert.assertTrue("SimpleSelectPhoneNumberAreaCodeWithEmployee test failed !", comparer.compareObjects(result,expectedResult));
        
    }
    
    public void testSelectPhoneNumberNumberWithEmployeeWithExplicitJoin()
    {
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal("Bob");
        exp = exp.and(employees.get("lastName").equal("Smith"));
        Employee emp = (Employee) em.getActiveSession().readAllObjects(Employee.class, exp).firstElement();
    
        PhoneNumber phone = (PhoneNumber) ((Vector)emp.getPhoneNumbers()).firstElement();
        String areaCode = phone.getAreaCode();
        String firstName = emp.getFirstName();
        
        ExpressionBuilder employeeBuilder = new ExpressionBuilder(Employee.class);
		Expression phones = employeeBuilder.anyOf("phoneNumbers");
    	Expression whereClause = phones.get("areaCode").equal(areaCode).and(
    	    phones.get("owner").get("id").equal(employeeBuilder.get("id")).and(
    	        employeeBuilder.get("firstName").equal(firstName)));

    	
    	ReportQuery rq = new ReportQuery();
		rq.addAttribute("number", new ExpressionBuilder().anyOf("phoneNumbers").get("number"));
    	rq.setSelectionCriteria(whereClause);
		rq.setReferenceClass(Employee.class);
    	
        Vector expectedResult = getAttributeFromAll("number", (Vector)em.getActiveSession().executeQuery(rq),Employee.class);
        
    	clearCache();    	
    
    	String ejbqlString;
    	ejbqlString = "SELECT phone.number FROM Employee employee, IN (employee.phoneNumbers) phone " + 
    		"WHERE phone.areaCode = \"" + areaCode + "\" AND (phone.owner.id = employee.id AND employee.firstName = \"" + firstName + "\")";
    
        List result = em.createQuery(ejbqlString).getResultList();                     
        
        Assert.assertTrue("SimpleSelectPhoneNumberAreaCodeWithEmployee test failed !", comparer.compareObjects(result,expectedResult));
        
    }
    
    public void testSelectPhoneNumberNumberWithEmployeeWithFirstNameFirst()
    {
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal("Bob");
        exp = exp.and(employees.get("lastName").equal("Smith"));
        Employee emp = (Employee) em.getActiveSession().readAllObjects(Employee.class, exp).firstElement();
    
        PhoneNumber phone = (PhoneNumber) ((Vector)emp.getPhoneNumbers()).firstElement();
        String areaCode = phone.getAreaCode();
        String firstName = emp.getFirstName();
        
        ExpressionBuilder employeeBuilder = new ExpressionBuilder();
		Expression phones = employeeBuilder.anyOf("phoneNumbers");
    	Expression whereClause = phones.get("owner").get("firstName").equal(firstName).and(
            	phones.get("areaCode").equal(areaCode));
    	
    	ReportQuery rq = new ReportQuery();
    	rq.setSelectionCriteria(whereClause);
    	rq.addAttribute("number", phones.get("number"));
		rq.setReferenceClass(Employee.class);
		
        Vector expectedResult = getAttributeFromAll("number", (Vector)em.getActiveSession().executeQuery(rq),Employee.class);	
        
        clearCache();
        
    	String ejbqlString;
    	ejbqlString = "SELECT phone.number FROM Employee employee, IN(employee.phoneNumbers) phone " + 
		    "WHERE phone.owner.firstName = \"" + firstName + "\" AND phone.areaCode = \"" + areaCode + "\"";
            
        List result = em.createQuery(ejbqlString).getResultList();                     
        
        Assert.assertTrue("SimpleSelectPhoneNumberAreaCodeWithEmployee test failed !", comparer.compareObjects(result,expectedResult));
        
    }

    public void testSelectEmployeeWithSameParameterUsedMultipleTimes() {
        Exception exception = null;
        
        try {
            String ejbqlString = "SELECT emp FROM Employee emp WHERE emp.id > :param1 OR :param1 IS null";
            createEntityManager().createQuery(ejbqlString).setParameter("param1", new Integer(1)).getResultList();
        } catch (Exception e) {
            exception = e;
        }
        
        Assert.assertNull("Exception was caught.", exception);
    }
    
    /**
     * Prior to the fix for GF 2333, the query in this test would a Null PK exception
     *
     */
    public void testInvertedSelectionCriteriaNullPK(){
        Exception exception = null;
        try {
            String jpqlString = "SELECT e, p FROM Employee e, PhoneNumber p WHERE p.id = e.id AND e.firstName = 'Bob'";
            List resultList = createEntityManager().createQuery(jpqlString).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
        
        Assert.assertNull("Exception was caught.", exception);
    }
    
    /**
     * Tests fix for bug6070214 that using Oracle Rownum pagination with non-unique columns
     * throws an SQl exception.
     */
    public void testMaxAndFirstResultsOnDataQuery(){
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        Exception exception = null;
        List resultList = null;
        clearCache();
        Query query = em.createQuery("SELECT e.id, m.id FROM Employee e LEFT OUTER JOIN e.manager m");
        try {
            query.setFirstResult(1);
            query.setMaxResults(1);
            resultList = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
        Assert.assertNull("Exception was caught.", exception);
        Assert.assertTrue("Incorrect number of results returned.  Expected 1, returned "+resultList.size(), resultList.size()==1);
    }
    
    /**
     * Tests fix for bug6070214 that using Oracle Rownum pagination with group by
     * throws an SQl exception.
     */
    public void testMaxAndFirstResultsOnDataQueryWithGroupBy() {
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        Exception exception = null;
        List resultList = null;
        clearCache();
        Query query = em.createQuery("SELECT e.id FROM Employee e group by e.id");
        try {
            query.setFirstResult(1);
            query.setMaxResults(1);
            resultList = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
        Assert.assertNull("Exception was caught.", exception);
        Assert.assertTrue("Incorrect number of results returned.  Expected 1, returned "+resultList.size(), resultList.size()==1);
    }
    
    /**
     * Prior to the fix for GF 2333, the query in this test would generate an invalid query key exception
     *
     */
    public void testInvertedSelectionCriteriaInvalidQueryKey(){
        Exception exception = null;
        try {
            String jpqlString = "select e, a from Employee e, Address a where a.city = 'Ottawa' and e.address.country = a.country";
            List resultList = createEntityManager().createQuery(jpqlString).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }
        
        Assert.assertNull("Exception was caught.", exception);
    }
    
    /*
     * For GF3233, Distinct process fail with NPE when relationship has NULL-valued target.
     */
    public void testDistinctSelectForEmployeeWithNullAddress(){
        org.eclipse.persistence.jpa.EntityManager em = (org.eclipse.persistence.jpa.EntityManager) createEntityManager();
        try {
            em.getTransaction().begin();
            Employee emp = new Employee();
            emp.setFirstName("Dummy");
            emp.setLastName("Person");
            em.persist(emp);
            em.flush();
            List resultList = em.createQuery("SELECT DISTINCT e.address FROM Employee e").getResultList();
        }finally{
            em.getTransaction().rollback();
        }
    }
    
    
  public static void main(String[] args)
  {
    junit.swingui.TestRunner.main(args);
  }
}