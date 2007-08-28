/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa;

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.advanced.AdvancedJPAJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.AdvancedJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.NamedNativeQueryJUnitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.CallbackEventJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.EntityManagerJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.SQLResultSetMappingTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.JoinedAttributeAdvancedJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.ReportQueryMultipleReturnTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.ExtendedPersistenceContextJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.ReportQueryConstructorExpressionTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.OptimisticConcurrencyJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.compositepk.AdvancedCompositePKJunitTest;

import org.eclipse.persistence.testing.tests.jpa.inheritance.LifecycleCallbackJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inheritance.DeleteAllQueryInheritanceJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inheritance.EntityManagerJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.inheritance.MixedInheritanceJUnitTestCase;

import org.eclipse.persistence.testing.tests.jpa.inherited.OrderedListJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inherited.InheritedModelJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inherited.InheritedCallbacksJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inherited.EmbeddableSuperclassJunitTest;

import org.eclipse.persistence.testing.tests.jpa.relationships.EMQueryJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.relationships.ExpressionJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.relationships.UniAndBiDirectionalMappingTestSuite;
import org.eclipse.persistence.testing.tests.jpa.relationships.VirtualAttributeTestSuite;

import org.eclipse.persistence.testing.tests.jpa.validation.ValidationTestSuite;
import org.eclipse.persistence.testing.tests.jpa.validation.QueryParameterValidationTestSuite;

import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLUnitTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLSimpleTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLComplexTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLValidationTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLComplexAggregateTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLDateTimeTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLParameterTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLExamplesTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLModifyTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.AdvancedQueryTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLInheritanceTestSuite;

import org.eclipse.persistence.testing.tests.jpa.jpaadvancedproperties.JPAAdvPropertiesJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.xml.EntityMappingsJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.ddlgeneration.DDLGenerationJUnitTestSuite;
import org.eclipse.persistence.testing.tests.ejb.ejbqltesting.JUnitJPQLInheritanceTestSuite;

public class FullRegressionTestSuite extends TestSuite{
    
    public static Test suite() {
        TestSuite fullSuite = new TestSuite();
        fullSuite.setName("FullRegressionTestSuite");
        
        // Advanced model        
        TestSuite suite = new TestSuite();
        suite.setName("advanced");
        suite.addTest(NamedNativeQueryJUnitTest.suite());
        suite.addTest(CallbackEventJUnitTestSuite.suite());
        suite.addTest(EntityManagerJUnitTestSuite.suite());
        suite.addTest(SQLResultSetMappingTestSuite.suite());
        suite.addTest(JoinedAttributeAdvancedJunitTest.suite());
        suite.addTest(ReportQueryMultipleReturnTestSuite.suite());
        suite.addTest(ExtendedPersistenceContextJUnitTestSuite.suite());
        suite.addTest(ReportQueryConstructorExpressionTestSuite.suite());
        suite.addTest(OptimisticConcurrencyJUnitTestSuite.suite());
        suite.addTest(AdvancedJPAJunitTest.suite());
        suite.addTest(AdvancedJunitTest.suite());
        suite.addTest(AdvancedCompositePKJunitTest.suite());
        fullSuite.addTest(suite);

        // FieldAccess advanced model
        suite = new TestSuite();
        suite.setName("fieldaccess");
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.NamedNativeQueryJUnitTest.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.CallbackEventJUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.EntityManagerJUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.SQLResultSetMappingTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.JoinedAttributeAdvancedJunitTest.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ReportQueryMultipleReturnTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ExtendedPersistenceContextJUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ReportQueryConstructorExpressionTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.OptimisticConcurrencyJUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.AdvancedJPAJunitTest.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.AdvancedJunitTest.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.UniAndBiDirectionalMappingTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.ExpressionJUnitTestSuite.class);
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.VirtualAttributeTestSuite.suite());
        fullSuite.addTest(suite);
        
        // Inheritance model.
        suite = new TestSuite();
        suite.setName("inheritance");
        suite.addTest(LifecycleCallbackJunitTest.suite());
        suite.addTest(DeleteAllQueryInheritanceJunitTest.suite());
        suite.addTest(EntityManagerJUnitTestCase.suite());
        suite.addTest(MixedInheritanceJUnitTestCase.suite());
        fullSuite.addTest(suite);
        
        // Inherited model.
        suite = new TestSuite();
        suite.setName("inherited");
        suite.addTest(OrderedListJunitTest.suite());
        suite.addTest(InheritedModelJunitTest.suite());
        suite.addTest(InheritedCallbacksJunitTest.suite());
        suite.addTest(EmbeddableSuperclassJunitTest.suite());
        fullSuite.addTest(suite);
        
        // Relationship model.        
        suite = new TestSuite();
        suite.setName("relationships");
        suite.addTestSuite(EMQueryJUnitTestSuite.class);
        suite.addTestSuite(ExpressionJUnitTestSuite.class);
        suite.addTest(VirtualAttributeTestSuite.suite());
        suite.addTest(ValidationTestSuite.suite());
        suite.addTest(QueryParameterValidationTestSuite.suite());
        suite.addTest(UniAndBiDirectionalMappingTestSuite.suite());
        fullSuite.addTest(suite);
        
        // JPQL testing model.
        suite = new TestSuite();
        suite.setName("jpql");
        suite.addTest(JUnitJPQLUnitTestSuite.suite());
        suite.addTest(JUnitJPQLSimpleTestSuite.suite());
        suite.addTest(JUnitJPQLComplexTestSuite.suite());
        suite.addTest(JUnitJPQLInheritanceTestSuite.suite());
        suite.addTest(JUnitJPQLValidationTestSuite.suite());
        suite.addTest(JUnitJPQLComplexAggregateTestSuite.suite());
        suite.addTest(JUnitJPQLDateTimeTestSuite.suite());
        suite.addTest(JUnitJPQLParameterTestSuite.suite());
        suite.addTest(JUnitJPQLExamplesTestSuite.suite());
        suite.addTest(JUnitJPQLModifyTestSuite.suite());
        suite.addTest(JUnitJPQLModifyTestSuite.suite());
        suite.addTest(AdvancedQueryTestSuite.suite());
        fullSuite.addTest(suite);

        // XML model
        fullSuite.addTest(EntityMappingsJUnitTestSuite.suite());

        // DDL model
        fullSuite.addTest(DDLGenerationJUnitTestSuite.suite());
        
        // JPA Advanced Properties model
        fullSuite.addTest(JPAAdvPropertiesJUnitTestCase.suite());
        
        // DataTypes model    
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.datatypes.NullBindingJUnitTestCase.suite());
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.datatypes.arraypks.PrimitiveArrayPKCachingJUnitTestCase.suite());
	
        // DateTime model
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.datetime.NullBindingJUnitTestCase.suite());

        // Lob model
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.lob.LobJUnitTestCase.suite());

        try{
	        Class structConverterClass = Class.forName("org.eclipse.persistence.testing.tests.jpa.structconverter.StructConverterTestSuite");
	        Test testCase = (Test)(structConverterClass.getMethod("suite", null).invoke(null, null));
	        fullSuite.addTest(testCase);
        } catch (Exception e){
        	// TODO print a proper warning
        	// this will usually happen if the Oracle-specific tests are not on the classpath
        	e.printStackTrace();
        }
        
        return fullSuite;
    }
    

    public static void main(String[] args)
    {
         junit.swingui.TestRunner.main(args);
    }
}