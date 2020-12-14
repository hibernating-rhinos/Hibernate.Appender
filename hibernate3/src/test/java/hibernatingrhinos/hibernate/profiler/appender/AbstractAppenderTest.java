//-----------------------------------------------------------------------
// <copyright file="AbstractAppenderTest.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import hibernatingrhinos.hibernate.profiler.messages.IProfilerMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AbstractAppenderTest {

    private static InMemoryProfilerGatewayFactory factory;

    @BeforeClass
    public static void clazzSetup() {
        factory = new InMemoryProfilerGatewayFactory();
        ProfilerGatewayFactory.setGatewayFactory(factory);
    }

    protected InMemoryProfilerGateway getGateway() {
        return factory.getGateway();
    }


    protected void assertProfilerShutdown() {
        assertTrue(HibernateProfiler.isInitialized() == false);

        for (int i = 0; i < HibernateProfilerTest.LoggerNames.length; i++) {
            Logger logger = LogManager.getLogger(HibernateProfilerTest.LoggerNames[i]);
            assertNull("Found unexpected appender on " + HibernateProfilerTest.LoggerNames[i], logger.getAppender("Hibernate.Profiler"));
        }
    }

    @After
    public void teardown() {
        factory.reset();
    }

    public static class InMemoryProfilerGatewayFactory extends ProfilerGatewayFactory {

        private Object locker = new Object();
        private InMemoryProfilerGateway gateway;

        @Override
        public IProfilerGateway newInstance(String address, int port) {
            synchronized (locker) {
                if (gateway == null)
                    gateway = new InMemoryProfilerGateway(address, port);
                return gateway;
            }
        }

        public InMemoryProfilerGateway getGateway() {
            return gateway;
        }

        public void reset() {
            synchronized (locker) {
                gateway = null;
            }
        }

    }

    public static class InMemoryProfilerGateway implements IProfilerGateway {

        private String address;
        private int port;
        private AtomicBoolean initialized = new AtomicBoolean();
        private AtomicInteger initializedCount = new AtomicInteger();
        private ArrayBlockingQueue<IProfilerMessage[]> messages = new ArrayBlockingQueue<IProfilerMessage[]>(10000);

        public InMemoryProfilerGateway(String address, int port) {
            this.address = address;
            this.port = port;
        }

        public void initialize() {
            initialized.set(true);
            initializedCount.incrementAndGet();
        }

        public void sendMessage(IProfilerMessage... messages) {
            this.messages.offer(messages);
        }

        public ArrayBlockingQueue<IProfilerMessage[]> getMessages() {
            return this.messages;
        }

        public void shutdown() {
            initialized.set(false);
        }

        public boolean isInitialized() {
            return initialized.get();
        }

        public int getInitializedCount() {
            return initializedCount.get();
        }

        public String getAddress() {
            return this.address;
        }

        public int getPort() {
            return this.port;
        }

    }

}
