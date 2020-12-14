//-----------------------------------------------------------------------
// <copyright file="HibernateStatisticsProviderTests.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import hibernatingrhinos.hibernate.profiler.appender.entities.Simple;
import hibernatingrhinos.hibernate.profiler.messages.SessionFactoryStats;

import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HibernateStatisticsProviderTest {

    private static SessionFactory sessionFactory;
    private HibernateStatisticsProvider statsProvider;

    @BeforeClass
    public static void initialize() {
        sessionFactory = new Configuration()
            .addPackage("hibernatingrhinos.hibernate.profiler.appender.entities")
            .addAnnotatedClass(Simple.class)
            .configure("hibernatingrhinos/hibernate/profiler/appender/entities/simple.cfg.xml")
            .buildSessionFactory();
        sessionFactory.getStatistics().setStatisticsEnabled(false);
    }

    @Before
    public void setup() {
        statsProvider = new HibernateStatisticsProvider();
    }

    @Test
    public void enables_statistics() {
        assertTrue(sessionFactory.getStatistics().isStatisticsEnabled());
    }

    @Test @SuppressWarnings("unchecked")
    public void get_statistics() {
        Session session = sessionFactory.openSession();
        try {
            Query query = session.createQuery("from Simple s");
            assertEquals(0, query.list().size());
            query = session.createQuery("from Simple s where id > 0");
            assertEquals(0, query.list().size());
        } finally {
            session.close();
        }

        SessionFactoryStats[] stats = statsProvider.getStatistics();
        assertNotNull(stats);
        for (int i = 0; i < stats.length; i++) {
            SessionFactoryStats stat = stats[i];
            assertEquals("unnamed", stat.getName());
            Map map = stat.getStatistics();

            //Test a few of the values
            assertHasKeyValue(map, "QueryCacheHitCount", 0L);
            assertHasKeyValue(map, "QueryExecutionCount", 2L);
            assertHasKeyValue(map, "SessionCloseCount", 1L);
            System.out.println(stat.toXml());
        }
    }

    @SuppressWarnings("unchecked")
    private void assertHasKey(Map map, String expected) {
        assertTrue("Could not find key: " + expected, map.containsKey(expected));
    }

    @SuppressWarnings("unchecked")
    private void assertHasKeyValue(Map map, String key, Object value) {
        assertHasKey(map, key);
        assertEquals("Does not have correct value for key: " + key, value, map.get(key));
    }

    @After
    public void teardown() {
        sessionFactory.getStatistics().setStatisticsEnabled(false);
    }

    @AfterClass
    public static void destroy() {
        if (sessionFactory != null) sessionFactory.close();
    }

}
