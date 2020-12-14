//-----------------------------------------------------------------------
// <copyright file="StandardQueryCacheFilter.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender.stacktracefilters;

import hibernatingrhinos.hibernate.profiler.appender.AppenderConfiguration;

public class StandardQueryCacheFilter extends AbstractStackTraceFilter {

    public StandardQueryCacheFilter(AppenderConfiguration appenderConfiguration) {
        super(appenderConfiguration);
    }

    public boolean applies(String logger, String msg) {
        return getAppenderConfiguration().isCaptureStackTraceFor2ndLevelCache() &&
               "org.hibernate.cache.StandardQueryCache".equals(logger) &&
               msg.startsWith("returning cached query results for: sql: ");
    }

}
