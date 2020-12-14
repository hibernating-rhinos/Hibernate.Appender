//-----------------------------------------------------------------------
// <copyright file="JDBCTransactionFilter.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender.stacktracefilters;

import hibernatingrhinos.hibernate.profiler.appender.AppenderConfiguration;

/**
 * 
 */
public class JDBCTransactionFilter extends AbstractStackTraceFilter {

    public JDBCTransactionFilter(AppenderConfiguration appenderConfiguration) {
        super(appenderConfiguration);
    }

    public boolean applies(String logger, String msg) {
        return getAppenderConfiguration().isCaptureStackTraceForTransactions() &&
               "org.hibernate.transaction.JDBCTransaction".equals(logger);
    }

}
