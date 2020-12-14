//-----------------------------------------------------------------------
// <copyright file="AbstractStackTraceFilter.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender.stacktracefilters;

import hibernatingrhinos.hibernate.profiler.appender.AppenderConfiguration;

public abstract class AbstractStackTraceFilter implements IStackTraceFilter {

    protected AppenderConfiguration appenderConfiguration;
    
    public AbstractStackTraceFilter(AppenderConfiguration appenderConfiguration) {
        this.appenderConfiguration = appenderConfiguration;
    }

    protected AppenderConfiguration getAppenderConfiguration() {
        return this.appenderConfiguration;
    }
        
}
