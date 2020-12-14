//-----------------------------------------------------------------------
// <copyright file="HibernateProfilerNamespaceTests.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import hibernatingrhinos.hibernate.profiler.appender.AbstractAppenderTest;
import hibernatingrhinos.hibernate.profiler.appender.HibernateProfilerTest;
import hibernatingrhinos.hibernate.profiler.appender.JavaHelper;
import hibernatingrhinos.hibernate.profiler.cache.ProfilerQueryCacheFactory;
import hibernatingrhinos.hibernate.profiler.event.ProfilerInitializeCollectionEventListener;
import hibernatingrhinos.hibernate.profiler.jdbc.ProfilerBatchingBatcherFactory;

import javax.sql.DataSource;

import net.sf.log4jdbc.hibernateprofiler.jdbc4.DriverSpy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.event.EventListeners;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

public class HibernateProfilerNamespaceTest extends AbstractAppenderTest {

    private ClassPathXmlApplicationContext context;

    @Test
    public void can_configure_profiler_with_no_arguments() {
        setupContext("noArguments.xml");

        for (int i = 0; i < HibernateProfilerTest.LoggerNames.length; i++) {
            Logger logger = LogManager.getLogger(HibernateProfilerTest.LoggerNames[i]);
            assertNotNull("Could not find appender on " + HibernateProfilerTest.LoggerNames[i], logger.getAppender("Hibernate.Profiler"));
        }

        shutdownContext();
        assertProfilerShutdown();
    }

    @Test
    public void configures_hibernate_properly() {
        setupContext("noArguments.xml");
        assertConfiguration();
        shutdownContext();
        assertProfilerShutdown();
    }

    @Test
    public void always_set_batch_size() {
        setupContext("noBatching.xml");
        assertConfiguration();
        assertProfilerShutdown();
    }

    @Test
    public void can_append_to_properties_as_typed_string() {
        setupContext("typedStringProperties.xml");
        assertConfiguration();
        assertProfilerShutdown();
    }

    @Test
    public void can_add_listeners_with_none_defined() {
        setupContext("eventListeners.xml");

        AnnotationSessionFactoryBean sessionFactory = (AnnotationSessionFactoryBean)context.getBean("&sessionFactory", AnnotationSessionFactoryBean.class);
        Configuration config = sessionFactory.getConfiguration();
        EventListeners listeners = config.getEventListeners();
        assertEquals(1, listeners.getInitializeCollectionEventListeners().length);
        assertEquals(ProfilerInitializeCollectionEventListener.class, listeners.getInitializeCollectionEventListeners()[0].getClass());
        assertProfilerShutdown();
    }

    @Test
    public void can_append_listeners() {
        setupContext("eventListeners-already-defined.xml");

        AnnotationSessionFactoryBean sessionFactory = (AnnotationSessionFactoryBean)context.getBean("&sessionFactory", AnnotationSessionFactoryBean.class);
        Configuration config = sessionFactory.getConfiguration();
        EventListeners listeners = config.getEventListeners();
        assertEquals(1, listeners.getEvictEventListeners().length);
        assertEquals(SimpleEvictListener.class, listeners.getEvictEventListeners()[0].getClass());
        assertEquals(1, listeners.getInitializeCollectionEventListeners().length);
        assertEquals(ProfilerInitializeCollectionEventListener.class, listeners.getInitializeCollectionEventListeners()[0].getClass());
        assertProfilerShutdown();
    }

    @Test
    public void does_not_add_spy_when_configured_not_to() {
        if (!JavaHelper.isJava6()) return;

        setupContext("dont-add-spy.xml");

        AnnotationSessionFactoryBean sessionFactory = (AnnotationSessionFactoryBean)context.getBean("&sessionFactory", AnnotationSessionFactoryBean.class);
        Configuration config = sessionFactory.getConfiguration();
        assertEquals("jdbc:hsqldb:mem:db", config.getProperty(Environment.URL));
        assertProfilerShutdown();
    }

    @Test
    public void adds_spy_to_xml_configuration() {
        if (!JavaHelper.isJava6()) return;
        setupContext("eventListeners-already-defined.xml");
        assertSpy();
        assertProfilerShutdown();
    }

    @Test
    public void adds_spy_to_xml_configuration_with_placeholders() {
        if (!JavaHelper.isJava6()) return;
        setupContext("eventListeners-already-defined-placeholders.xml");
        assertSpy();
        assertProfilerShutdown();
    }

    @Test
    public void adds_spy_to_typed_string_configuration() {
        if (!JavaHelper.isJava6()) return;
        setupContext("typedStringProperties.xml");
        assertSpy();
        assertProfilerShutdown();
    }

    @Test
    public void adds_spy_to_typed_string_configuration_with_placeholders() {
        if (!JavaHelper.isJava6()) return;
        setupContext("typedStringProperties-placeholders.xml");
        assertSpy();
        assertProfilerShutdown();
    }

    @Test
    public void adds_spy_to_data_source() {
        if (!JavaHelper.isJava6()) return;
        setupContext("dataSource.xml");
        AnnotationSessionFactoryBean sessionFactory = (AnnotationSessionFactoryBean)context.getBean("&sessionFactory", AnnotationSessionFactoryBean.class);
        DataSource dataSource = (DataSource) sessionFactory.getDataSource();
        assertEquals(ConnectionWrappingDataSource.class, dataSource.getClass());
        assertProfilerShutdown();
    }

    @Test
    public void data_source_does_not_add_spy_when_configured_not_to() {
        if (!JavaHelper.isJava6()) return;
        setupContext("dataSource-dont-add-spy.xml");
        AnnotationSessionFactoryBean sessionFactory = (AnnotationSessionFactoryBean)context.getBean("&sessionFactory", AnnotationSessionFactoryBean.class);
        DataSource dataSource = (DataSource) sessionFactory.getDataSource();
        assertNotSame(ConnectionWrappingDataSource.class, dataSource.getClass());
        assertProfilerShutdown();
    }

    private void assertConfiguration() {
        AnnotationSessionFactoryBean sessionFactory = (AnnotationSessionFactoryBean)context.getBean("&sessionFactory", AnnotationSessionFactoryBean.class);
        Configuration configuration = sessionFactory.getConfiguration();
        assertEquals(ProfilerQueryCacheFactory.class.getName(), configuration.getProperty(Environment.QUERY_CACHE_FACTORY));
        assertEquals(ProfilerBatchingBatcherFactory.class.getName(), configuration.getProperty(Environment.BATCH_STRATEGY));
    }

    private void assertSpy() {
        AnnotationSessionFactoryBean sessionFactory = (AnnotationSessionFactoryBean)context.getBean("&sessionFactory", AnnotationSessionFactoryBean.class);
        Configuration config = sessionFactory.getConfiguration();
        assertEquals("jdbc:log4jdbc:hsqldb:mem:db", config.getProperty(Environment.URL));
        assertEquals(DriverSpy.class.getName(), config.getProperty(Environment.DRIVER));
    }

    private void setupContext(String xmlFile) {
        this.context = new ClassPathXmlApplicationContext("hibernatingrhinos/hibernate/profiler/spring/" + xmlFile);
    }

    @After
    public void teardown() {
        super.teardown();
        if (context != null)
            shutdownContext();
    }

    private void shutdownContext() {
        context.close();
        context.destroy();
        context = null;
    }


}