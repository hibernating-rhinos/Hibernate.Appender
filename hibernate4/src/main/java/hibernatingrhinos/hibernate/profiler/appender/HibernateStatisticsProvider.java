//-----------------------------------------------------------------------
// <copyright file="HibernateStatisticsProvider.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import hibernatingrhinos.hibernate.profiler.messages.SessionFactoryStats;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionFactoryRegistry;
import org.hibernate.stat.Statistics;

public class HibernateStatisticsProvider {

    private List<SessionFactory> factoryInstances = new ArrayList<SessionFactory>();
    private List<Method> statisticsGetters = new ArrayList<Method>();
    private volatile AtomicBoolean foundSessionFactory = new AtomicBoolean(false);
    private Object initLock = new Object();

    public HibernateStatisticsProvider() {
        initialize();
        if (factoryInstances.size() > 0) foundSessionFactory.set(true);
    }

    private void initialize() {
        try {
            Class sessionFactoryObjectInstace = Class.forName("org.hibernate.internal.SessionFactoryRegistry");

            Field field = sessionFactoryObjectInstace.getDeclaredField("sessionFactoryMap");
            field.setAccessible(true);
            Map<String, SessionFactory> factories = (Map<String, SessionFactory>) field.get(SessionFactoryRegistry.INSTANCE);

            for (SessionFactory sessionFactory: factories.values()) {
                factoryInstances.add(sessionFactory);
                sessionFactory.getStatistics().setStatisticsEnabled(true);
                Method[] statsProperties = sessionFactory.getStatistics().getClass().getDeclaredMethods();
                for (Method statMethod : statsProperties) {
                    if (isJavaBeanGetter(statMethod)) {
                        statisticsGetters.add(statMethod);
                    }
                }
            }
        } catch (Exception exception) {
            HibernateProfilerTrace.log("Error accessing statistics", exception);
        }
    }

    private boolean isJavaBeanGetter(Method statMethod) {
        String name = statMethod.getName();
        return statMethod.getParameterTypes().length == 0 && (name.startsWith("get") || name.startsWith("is"));
    }

    public SessionFactoryStats[] getStatistics() {
        if (foundSessionFactory.get() == false) {
            synchronized (initLock) {
                if (foundSessionFactory.get() == false) {
                    initialize();
                    if (factoryInstances.size() > 0)
                        foundSessionFactory.set(true);
                    else
                        return null;
                }
            }
        }

        List<SessionFactoryStats> list = new ArrayList<SessionFactoryStats>(factoryInstances.size());
        try {
            for (SessionFactory sessionFactory : factoryInstances) {
                Statistics stats = sessionFactory.getStatistics();
                SessionFactoryStats stat = new SessionFactoryStats();
                String sfName = "";
                if (sessionFactory instanceof SessionFactoryImpl) {
                    SessionFactoryImpl impl = (SessionFactoryImpl) sessionFactory;
                    sfName = impl.getSettings().getSessionFactoryName();
                }
                stat.setName(sfName == null || sfName == "" ? "unnamed" : sfName);
                stat.setStatistics(statsToMap(stats));
                list.add(stat);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (SessionFactoryStats[])list.toArray(new SessionFactoryStats[list.size()]);
    }

    /**
     * @param stats
     * @return
     */
    private Map statsToMap(Object stats) {
        if (stats == null)
            return new HashMap();
        Map hashtable = new HashMap();
        for (int i = 0; i < statisticsGetters.size(); i++) {
            Method info = (Method)statisticsGetters.get(i);

            try {
                String key = stripBeanPrefix(info.getName());
                Object result = info.invoke(stats, null);
                hashtable.put(key, result);
            } catch (Exception e) {
                hashtable.put(Integer.valueOf(i), null);
            }
        }
        return hashtable;

    }

    private String stripBeanPrefix(String name) {
        boolean isGetter = name.startsWith("get");
        boolean isBooleanGetter = name.startsWith("is");

        if ((isGetter || isBooleanGetter) == false)
            return name;

        return isGetter ? name.substring(3) : name.substring(2);
    }
}
