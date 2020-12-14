//-----------------------------------------------------------------------
// <copyright file="IStackTraceFilter.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender.stacktracefilters;

public interface IStackTraceFilter {
    boolean applies(String logger, String msg);
}
