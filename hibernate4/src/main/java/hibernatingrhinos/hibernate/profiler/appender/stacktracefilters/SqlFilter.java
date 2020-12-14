//-----------------------------------------------------------------------
// <copyright file="SqlFilter.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender.stacktracefilters;

import hibernatingrhinos.hibernate.profiler.appender.AppenderConfiguration;

public class SqlFilter extends AbstractStackTraceFilter {

    public SqlFilter(AppenderConfiguration appenderConfiguration) {
        super(appenderConfiguration);
    }

    public boolean applies(String logger, String msg) {
        return getAppenderConfiguration().isCaptureStackTraceForSql() &&
               "org.hibernate.SQL".equals(logger);
    }

}
