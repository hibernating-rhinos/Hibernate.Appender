//-----------------------------------------------------------------------
// <copyright file="HibernateProfilerAppenderTests.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import hibernatingrhinos.hibernate.profiler.appender.entities.Simple;
import hibernatingrhinos.hibernate.profiler.messages.ApplicationAttached;
import hibernatingrhinos.hibernate.profiler.messages.IProfilerMessage;
import hibernatingrhinos.hibernate.profiler.messages.LoggingEventMessage;
import hibernatingrhinos.hibernate.profiler.messages.SessionFactoryStats;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateProfilerAppenderTest extends AbstractAppenderTest {

    private InMemoryProfilerGateway gateway;

    @Before
    public void setup() throws Exception {
        HibernateProfiler.initialize(-999);
        assertNotNull(getGateway());
        gateway = getGateway();
    }

    @Test
    public void messages_are_published() throws Exception {
        Logger logger = LogManager.getLogger("org.hibernate.SQL");
        logger.debug("test message");

        IProfilerMessage[] message = gateway.getMessages().poll(2000, TimeUnit.MILLISECONDS);
        assertNotNull("No message received", message);
        assertEquals(1, message.length);
        assertThat(message[0], is(ApplicationAttached.class));

        message = gateway.getMessages().poll(2000, TimeUnit.MILLISECONDS);
        assertNotNull("No message received", message);
        assertThat(message[0], is(LoggingEventMessage.class));
        LoggingEventMessage logEvent = (LoggingEventMessage)message[0];
        assertEquals("test message", logEvent.getMessage());
        assertEquals("org.hibernate.SQL", logEvent.getLogger());
        assertNotNull(logEvent.getDate());
        assertTrue(logEvent.getThreadId() != null && logEvent.getThreadId() != "");
        assertNotNull(logEvent.getStackTraceInfo());
    }

    @Test
    public void application_attached_is_published() throws Exception {
        IProfilerMessage[] message = gateway.getMessages().poll(2000, TimeUnit.MILLISECONDS);
        assertNotNull("No message received", message);
        assertEquals(1, message.length);
        assertThat(message[0], is(ApplicationAttached.class));
        ApplicationAttached logEvent = (ApplicationAttached)message[0];
        assertNotNull("guid", logEvent.getGuid());
    }

    @Test
    public void statistics_are_published() throws Exception {
        SessionFactory sessionFactory = new Configuration()
            .addPackage("hibernatingrhinos.hibernate.profiler.appender.entities")
            .addAnnotatedClass(Simple.class)
            .configure("hibernatingrhinos/hibernate/profiler/appender/entities/simple.cfg.xml")
            .buildSessionFactory();

        try {
            long timeout = System.currentTimeMillis() + 2000;

            boolean foundSessionStat = false;
            while (System.currentTimeMillis() < timeout && foundSessionStat == false) {
                IProfilerMessage[] messages = gateway.getMessages().poll(2000, TimeUnit.MILLISECONDS);
                assertNotNull(messages);

                for (IProfilerMessage message : messages) {
                    if (message instanceof SessionFactoryStats) {
                        foundSessionStat = true;
                        break;
                    }
                }
            }
            assertTrue(foundSessionStat);
        } finally {
            sessionFactory.close();
        }
    }

    @After
    public void teardown() {
        HibernateProfiler.stop();
        gateway = null;
    }
}
