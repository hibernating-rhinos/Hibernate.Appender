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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HibernateStatisticsProvider {

    private List factoryInstances = new ArrayList();
    private Method getStatisticsMethod;
    private Method getSettings;
    private Method getSessionFactoryName;
    private List statisticsGetters = new ArrayList();
    private volatile AtomicBoolean foundSessionFactory = new AtomicBoolean(false);
    private Object initLock = new Object();

    public HibernateStatisticsProvider() {
        initialize();
        if (factoryInstances.size() > 0) foundSessionFactory.set(true);
    }

    private void initialize() {
        try {
            Class sessionFactoryObjectInstace = Class.forName("org.hibernate.impl.SessionFactoryObjectFactory");

            Object instances = new Object();
            Field field = sessionFactoryObjectInstace.getDeclaredField("INSTANCES");
            field.setAccessible(true);
            instances = field.get(instances);

            Method method = instances.getClass().getMethod("values", null);
            Collection sessionFactories = (Collection)method.invoke(instances, null);

            Iterator sessionFactoryIt = sessionFactories.iterator();
            while (sessionFactoryIt.hasNext()) {
                Object sessionFactory = sessionFactoryIt.next();
                factoryInstances.add(sessionFactory);
                getStatisticsMethod = sessionFactory.getClass().getMethod("getStatistics", null);
                Object statistics = getStatisticsMethod.invoke(sessionFactory, null);
                Method setStatsEnabled = statistics.getClass().getMethod("setStatisticsEnabled", new Class[]{boolean.class});
                setStatsEnabled.invoke(statistics, new Object[]{Boolean.TRUE});

                getSettings = sessionFactory.getClass().getMethod("getSettings", null);
                Object settings = getSettings.invoke(sessionFactory, null);
                getSessionFactoryName = settings.getClass().getMethod("getSessionFactoryName", null);
                                
                Method[] statsProperties = statistics.getClass().getDeclaredMethods();
                for (int i = 0; i < statsProperties.length; i++) {
                    Method statMethod = statsProperties[i];
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

        List list = new ArrayList();
        try {
            for (Iterator it = factoryInstances.iterator(); it.hasNext();) {
                Object sessionFactory = it.next();
                Object stats = getStatisticsMethod.invoke(sessionFactory, null);
                Object settings = getSettings.invoke(sessionFactory, null);
                String sfName = (String)getSessionFactoryName.invoke(settings, null);
                SessionFactoryStats stat = new SessionFactoryStats();
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
