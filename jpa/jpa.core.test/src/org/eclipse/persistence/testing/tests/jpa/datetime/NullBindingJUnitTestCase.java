/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.datetime;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datetime.*;
import org.eclipse.persistence.sessions.DatabaseSession;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * <p>
 * <b>Purpose</b>: Test binding of null values to temporal type fields
 * in TopLink's JPA implementation.
 * <p>
 * <b>Description</b>: This class creates a test suite and adds tests to the
 * suite. The database gets initialized prior to the test methods.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for binding of null values to temporal type fields
 * in TopLink's JPA implementation.
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.datetime.DateTimeTableCreator
 */
public class NullBindingJUnitTestCase extends JUnitTestCase {
    private static int datetimeId;

    public NullBindingJUnitTestCase() {
        super();
    }

    public NullBindingJUnitTestCase(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Null Binding DateTime");
        suite.addTest(new NullBindingJUnitTestCase("testCreateDateTime"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifySqlDate"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyTime"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyTimestamp"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyUtilDate"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyCalendar"));

        return new TestSetup(suite) {

            protected void setUp(){
                DatabaseSession session = JUnitTestCase.getServerSession();
                new DateTimeTableCreator().replaceTables(session);
            }

            protected void tearDown() {
                removeDateTime();

                clearCache();
            }
        };
    }

    /**
     * Creates the DateTime instance used in later tests.
     */
    public void testCreateDateTime() {
        EntityManager em = createEntityManager();
        DateTime dt;

        em.getTransaction().begin();
        dt = new DateTime(new java.sql.Date(0), new java.sql.Time(0), new java.sql.Timestamp(0),
                new java.util.Date(0), java.util.Calendar.getInstance());
        em.persist(dt);
        datetimeId = dt.getId();
        em.getTransaction().commit();
    }

    /**
     */
    public void testNullifySqlDate() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            em.getTransaction().begin();
            dt = em.find(DateTime.class, datetimeId);
            dt.setDate(null);
            em.getTransaction().commit();
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.sql.Date field to null", dt2.getDate() == null);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
    }

    /**
     */
    public void testNullifyTime() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            em.getTransaction().begin();
            dt = em.find(DateTime.class, datetimeId);
            dt.setTime(null);
            em.getTransaction().commit();
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.sql.Time field to null", dt2.getTime() == null);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
    }

    /**
     */
    public void testNullifyTimestamp() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            em.getTransaction().begin();
            dt = em.find(DateTime.class, datetimeId);
            dt.setTimestamp(null);
            em.getTransaction().commit();
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.sql.Timestamp field to null", dt2.getTimestamp() == null);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
    }

    /**
     */
    public void testNullifyUtilDate() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            em.getTransaction().begin();
            dt = em.find(DateTime.class, datetimeId);
            dt.setUtilDate(null);
            em.getTransaction().commit();
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.util.Date field to null", dt2.getUtilDate() == null);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
    }

    /**
     */
    public void testNullifyCalendar() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            em.getTransaction().begin();
            dt = em.find(DateTime.class, datetimeId);
            dt.setCalendar(null);
            em.getTransaction().commit();
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.util.Calendar field to null", dt2.getCalendar() == null);
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
    }

    /**
     * Removes the DateTime instance used in the tests.
     */
    public static void removeDateTime() {
        EntityManager em = createEntityManager();
        DateTime dt;

        em.getTransaction().begin();
        dt = em.find(DateTime.class, datetimeId);
        em.remove(dt);
        em.getTransaction().commit();
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.main(args);
    }
}