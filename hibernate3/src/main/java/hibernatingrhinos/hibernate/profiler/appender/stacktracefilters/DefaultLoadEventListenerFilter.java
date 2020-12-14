//-----------------------------------------------------------------------
// <copyright file="DefaultLoadEventListenerFilter.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender.stacktracefilters;

import hibernatingrhinos.hibernate.profiler.appender.AppenderConfiguration;

public class DefaultLoadEventListenerFilter extends AbstractStackTraceFilter {

    public DefaultLoadEventListenerFilter(AppenderConfiguration appenderConfiguration) {
        super(appenderConfiguration);
    }

    public boolean applies(String logger, String msg) {
        return getAppenderConfiguration().isCaptureStackTraceFor2ndLevelCache() &&
               "org.hibernate.event.def.DefaultLoadEventListener".equals(logger) &&
               msg.startsWith("assembling entity from second-level cache: [");
    }

}
