//-----------------------------------------------------------------------
// <copyright file="ProfilerConfiguration.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import hibernatingrhinos.hibernate.profiler.cache.ProfilerQueryCacheFactory;
import hibernatingrhinos.hibernate.profiler.jdbc.ProfilerBatchingBatcherFactory;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.log4jdbc.hibernateprofiler.jdbc4.DriverSpy;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
import org.springframework.beans.factory.config.TypedStringValue;

public class ProfilerConfiguration {

    private static final Pattern URL_LINE_PATTERN = Pattern.compile("(\\p{Blank})*" + Environment.URL + "=(.*)");
    private static final Pattern DRIVER_LINE_PATTERN = Pattern.compile("(\\p{Blank})*" + Environment.DRIVER + "=(.*)");
    private boolean useSpy = shouldUseSpy(true);

    public ProfilerConfiguration() {
        this.useSpy = shouldUseSpy(true);
    }

    public ProfilerConfiguration(boolean useSpy) {
        this.useSpy = shouldUseSpy(useSpy);
    }

    private boolean shouldUseSpy(boolean userSpecified) {
        return userSpecified && JavaHelper.isJava6();
    }

    public void configure(Configuration config) {
        configure(config.getProperties());

        /**
         * TODO
        config.setListener("load-collection", ProfilerInitializeCollectionEventListener.class.getName());
         */
    }

    public void configure(Properties properties) {
        properties.put(Environment.QUERY_CACHE_FACTORY, ProfilerQueryCacheFactory.class.getName());
        properties.put(BatchBuilderInitiator.BUILDER, ProfilerBatchingBatcherFactory.class.getName());

        if (!properties.containsKey(Environment.GENERATE_STATISTICS))
            properties.put(Environment.GENERATE_STATISTICS, "true");

        if (!useSpy) return;

        Object dataSourceKey = null;
        Object driverKey = null;

        for (Object key : properties.keySet()) {
            if (key instanceof String) {
                if (Environment.URL.equals(key)) {
                    dataSourceKey = key;
                }
                if (Environment.DRIVER.equals(key)) {
                    driverKey = key;
                }
            } else if (key instanceof TypedStringValue) {
                TypedStringValue tsv = (TypedStringValue) key;
                if (Environment.URL.equals(tsv.getValue())) {
                    dataSourceKey = key;
                }
                if (Environment.DRIVER.equals(tsv.getValue())) {
                    driverKey = key;
                }
            }
        }

        if (dataSourceKey == null) return;

        Object dataSourceObject = properties.get(dataSourceKey);
        String dataSource = null;
        if (dataSourceObject instanceof String) {
            dataSource = (String) dataSourceObject;
        } else if (dataSourceObject instanceof TypedStringValue) {
            TypedStringValue tsv = (TypedStringValue) dataSourceObject;
            dataSource = tsv.getValue();
        }

        if (dataSource != null) {
            if (!dataSource.startsWith("jdbc:")) return;//don't do anything with an invalid connection string

            String newUrl = "jdbc:log4jdbc:" + dataSource.substring(5);

            if (dataSourceObject instanceof String) {
                properties.put(dataSourceKey, newUrl);
            } else if (dataSourceObject instanceof TypedStringValue) {
                TypedStringValue tsv = (TypedStringValue) dataSourceObject;
                tsv.setValue(newUrl);
                properties.put(dataSourceKey, tsv);
            }


            Object driver = properties.get(driverKey);
            if (driver instanceof String) {
                properties.setProperty(Environment.DRIVER, DriverSpy.class.getName());
            } else if (driver instanceof TypedStringValue) {
                TypedStringValue tsv = (TypedStringValue) driver;
                tsv.setValue(DriverSpy.class.getName());
                properties.put(Environment.DRIVER, tsv);
            }
        }
    }

    public String configure(String string) {
        StringBuilder buffer = new StringBuilder(string);
        buffer.append("\n");

        buffer.append(Environment.QUERY_CACHE_FACTORY).append("=").append(ProfilerQueryCacheFactory.class.getName()).append("\n");
        buffer.append(BatchBuilderInitiator.BUILDER).append("=").append(ProfilerBatchingBatcherFactory.class.getName()).append("\n");

        if (!string.contains(Environment.GENERATE_STATISTICS))
            buffer.append(Environment.GENERATE_STATISTICS).append("=").append("true").append("\n");
        if (useSpy) {
            String[] configLines = buffer.toString().split("\r\n|\r|\n");
            buffer = new StringBuilder();
            for (String line : configLines) {
                String configLine = line;

                Matcher matcher = URL_LINE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String connectionString = matcher.group(2);
                    String newUrl = "jdbc:log4jdbc:" + connectionString.substring(5);
                    configLine = Environment.URL + "=" + newUrl;
                    buffer.append(configLine).append("\n");
                    continue;
                }

                matcher = DRIVER_LINE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    configLine = Environment.DRIVER + "=" + DriverSpy.class.getName();
                }

                buffer.append(configLine).append("\n");
            }
        }

        return buffer.toString();
    }
}
