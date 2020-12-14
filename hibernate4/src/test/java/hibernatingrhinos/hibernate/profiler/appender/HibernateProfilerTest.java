//-----------------------------------------------------------------------
// <copyright file="HibernateProfilerTests.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.junit.Test;

public class HibernateProfilerTest extends AbstractAppenderTest {
    
    public static final String[] LoggerNames = new String[]
                                                           {
                                                               "org.hibernate.transaction.JDBCTransaction",
                                                               "org.hibernate.transaction.CMTTransaction",
                                                               "org.hibernate.transaction.JTATransaction",
                                                               "org.hibernate.SQL",
                                                               "org.hibernate.impl.SessionImpl",
                                                               "org.hibernate.impl.AbstractSessionImpl",
                                                               "org.hibernate.event.def.DefaultLoadEventListener",
                                                               "org.hibernate.cache.StandardQueryCache",
                                                               "org.hibernate.persister.entity.AbstractEntityPersister",
                                                               "org.hibernate.loader.Loader",
                                                               "org.hibernate.jdbc.AbstractBatcher"
                                                           };

    @Test
    public void gateway_is_initialized_with_default_settings() {
        HibernateProfiler.initialize();
        assertNotNull(getGateway());
        assertEquals("127.0.0.1", getGateway().getAddress());
        assertEquals(22897, getGateway().getPort());
        assertTrue(getGateway().isInitialized());
        HibernateProfiler.stop();
        assertProfilerShutdown();
    }
    
    @Test
    public void gateway_is_initalized_with_port_and_address() {
        AppenderConfiguration configuration = new AppenderConfiguration();
        configuration.setAddress("10.0.0.1");
        configuration.setPort(-55);
        HibernateProfiler.initialize(configuration);
        assertNotNull(getGateway());
        assertEquals("10.0.0.1", getGateway().getAddress());
        assertEquals(-55, getGateway().getPort());
        HibernateProfiler.stop();
        assertProfilerShutdown();
    }

    @Test
    public void gateway_is_initalized_with_port() {
        HibernateProfiler.initialize(-10);
        assertNotNull(getGateway());
        assertEquals(-10, getGateway().getPort());
        HibernateProfiler.stop();
        assertProfilerShutdown();
    }    
    
    @Test
    public void appenders_are_attached_and_removed() {       
        HibernateProfiler.initialize();

        for (int i = 0; i < LoggerNames.length; i++) {
            Logger logger = LogManager.getLogger(LoggerNames[i]);
            assertNotNull("Could not find appender on " + LoggerNames[i], logger.getAppender("Hibernate.Profiler"));
        }
        
        HibernateProfiler.stop();
        assertProfilerShutdown();
    }
    
    @Test
    public void logger_restarted_by_background_thread_when_closed() throws Exception {
        HibernateProfiler.initialize();
        assertNotNull(getGateway());
        assertEquals(1, getGateway().getInitializedCount());

        closeAppender();

        Thread.sleep(500);

        LoggerRepository repository = LogManager.getLoggerRepository();
        Logger logger = repository.getLogger("org.hibernate.SQL");
        assertNotNull(logger.getAppender("Hibernate.Profiler"));
        HibernateProfiler.stop();
        assertProfilerShutdown();
    }
    
    @Test
    public void logger_does_not_restart_after_profiler_stopped() throws Exception {
        HibernateProfiler.initialize();
        assertNotNull(getGateway());
        assertEquals(1, getGateway().getInitializedCount());
        
        HibernateProfiler.stop();
        
        closeAppender();
        
        Thread.sleep(500);
        
        assertEquals(1, getGateway().getInitializedCount());
        assertProfilerShutdown();
    }  
       
    @Override
    public void teardown() {
        super.teardown();
        LogManager.shutdown();
    }
    
    private void closeAppender() {
        LoggerRepository repository = LogManager.getLoggerRepository();
        Logger logger = repository.getLogger("org.hibernate.SQL");
        Appender appender = logger.getAppender("Hibernate.Profiler");
        if (appender != null) appender.close();
    }
}
