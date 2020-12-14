//-----------------------------------------------------------------------
// <copyright file="HibernateProfiler.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import hibernatingrhinos.hibernate.profiler.appender.HibernateProfilerAppender.OnLoggerCloseListener;
import hibernatingrhinos.hibernate.profiler.appender.stacktracefilters.*;

import hibernatingrhinos.hibernate.profiler.messages.ApplicationAttached;
import org.apache.log4j.*;
import org.apache.log4j.xml.XMLLayout;
import org.hibernate.cfg.Configuration;

public class HibernateProfiler {

    public static final Integer DefaultPort = 22897;
    public static final String DefaultHost = "127.0.0.1";

    private static String logFilename;
    private static final Logger sessionImplLogger = LogManager.getLogger("org.hibernate.impl.SessionImpl");
    private static final Object initLock = new Object();
    private static volatile boolean initialized = false;

    private static final String[] LoggerNames =
        new String[] {
            "org.hibernate.transaction.JDBCTransaction",
            "org.hibernate.transaction.CMTTransaction",
            "org.hibernate.transaction.JTATransaction",
            "org.hibernate.SQL",
            "org.hibernate.impl.SessionImpl",
            "org.hibernate.impl.AbstractSessionImpl",
            "org.hibernate.event.def.DefaultLoadEventListener",
            "org.hibernate.event.def.DefaultInitializeCollectionEventListener",
            "org.hibernate.cache.StandardQueryCache",
            "org.hibernate.persister.entity.AbstractEntityPersister",
            "org.hibernate.loader.Loader",
            "org.hibernate.jdbc.AbstractBatcher",
            "hibernatingrhinos.hibernate.profiler.cache.ProfilerQueryCache",
            "hibernatingrhinos.hibernate.profiler.event.ProfilerInitializeCollectionEventListener"
         };

    protected static IStackTraceFilter[] stackTraceFilters = new IStackTraceFilter[]{new JDBCTransactionFilter(new AppenderConfiguration()),
            new SqlFilter(new AppenderConfiguration()), new DefaultLoadEventListenerFilter(new AppenderConfiguration()),
            new StandardQueryCacheFilter(new AppenderConfiguration())};

    public static Configuration configure(Configuration config, boolean useSpy) {
        ProfilerConfiguration configuration = new ProfilerConfiguration(useSpy);
        configuration.configure(config);
        return config;
    }

    public static Configuration configure(Configuration config) {
        return configure(config, true);
    }

    public static void initialize() {
        initialize(DefaultPort);
    }

    public static void initialize(String address, int port) {
        AppenderConfiguration configuration = new AppenderConfiguration();
        configuration.setAddress(address);
        configuration.setPort(port);
        initialize(configuration);
    }

    public static void initialize(int port) {
        AppenderConfiguration configuration = new AppenderConfiguration();
        configuration.setPort(port);
        initialize(configuration);
    }

    public static void setCurrentSessionName(String name) {
        sessionImplLogger.info("Rename session to: " + name);
    }

    public static void stop() {
        synchronized (initLock) {
            initialized = false;
        }

        Hierarchy repository = (Hierarchy)LogManager.getLoggerRepository();

        for (String LoggerName : LoggerNames) {
            Logger logger = repository.getLogger(LoggerName);
            logger.setLevel(Level.TRACE);
            logger.removeAppender("Hibernate.Profiler");
        }
    }

    /**
     * @return the initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized the initialized to set
     */
    public static void setInitialized(boolean initialized) {
        HibernateProfiler.initialized = initialized;
    }

    public static void initialize(final AppenderConfiguration configuration) {
        synchronized (initLock) {
            if (initialized)
                return;

            stackTraceFilters = new IStackTraceFilter[]{new JDBCTransactionFilter(configuration), new SqlFilter(configuration),
                    new DefaultLoadEventListenerFilter(configuration), new StandardQueryCacheFilter(configuration)};

            Hierarchy repository = (Hierarchy)LogManager.getLoggerRepository();
            HibernateProfilerAppender profilerAppender = new HibernateProfilerAppender();
            profilerAppender.setName("Hibernate.Profiler");

            IProfilerGateway gateway = ProfilerGatewayFactory.getGateway(configuration.getAddress(), configuration.getPort());
            gateway.initialize();
            profilerAppender.setGateway(gateway);

            profilerAppender.addOnLoggerCloseListener(new OnLoggerCloseListener() {
                public void onClose() {
                    if (!initialized)
                        return;

                    Thread t = new Thread() {
                        public void run() {
                            try {
                                initialize(configuration);
                            } catch (Exception e) {
                                // ignore exception
                            }
                        }
                    };
                    t.setDaemon(true);
                    t.start();
                }
            });

            setupLoggers(repository, profilerAppender);

            profilerAppender.activateOptions();

            gateway.sendMessage(new ApplicationAttached(ApplicationNameHelper.getApplicationName(), GuidHelper.getGuid()));

            initialized = true;
        }
    }

    public static void initializeOfflineProfiling(String filename) {
        synchronized (initLock) {
            if (filename == null || "".equals(filename))
                throw new IllegalArgumentException("Log filename cannot be null or empty");

            if (logFilename != null && logFilename.equals(filename))
                return;

            logFilename = filename;
            Hierarchy repository = (Hierarchy)LogManager.getLoggerRepository();
            FileAppender profilerAppender = new FileAppender();
            profilerAppender.setLayout(new XMLLayout());
            profilerAppender.setFile(logFilename);
            profilerAppender.setName("Hibernate Profiler");

            profilerAppender.activateOptions();

            setupLoggers(repository, profilerAppender);

            initialized = true;
        }
    }

    /**
     * @param repository
     * @param profilerAppender
     */
    private static void setupLoggers(Hierarchy repository, Appender profilerAppender) {
        Logger logger = repository.getLogger("org.hibernate");
        if (logger.getLevel() == null || logger.getLevel().isGreaterOrEqual(Level.WARN))
            logger.setLevel(Level.WARN);
        logger.setAdditivity(false);
        logger.addAppender(profilerAppender);

        for (String LoggerName : LoggerNames) {
            logger = repository.getLogger(LoggerName);
            logger.setLevel(Level.TRACE);
            logger.setAdditivity(false);
            if (!logger.getName().startsWith("org.hiberate"))
                logger.addAppender(profilerAppender);
        }
    }

}
