//-----------------------------------------------------------------------
// <copyright file="HibernateProfilerTrace.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import java.io.PrintStream;

/**
 * Class for logging trace issues internal to the profiler.  Since the profiler
 * itself makes heavy use of the Log4j infrastructure, all the messages from this
 * class are just sent to standard out.
 */
public class HibernateProfilerTrace {
    
    private static boolean traceEnabled = false;
    private static boolean traceEnabledForStats = false;
    private static PrintStream traceOutput = System.out;

    public static void setTraceEnabledForStats(boolean enableTrace) {
        traceEnabledForStats = enableTrace;
    }

    public static void setTraceOutput(PrintStream traceOutput) {
        HibernateProfilerTrace.traceOutput = traceOutput;
    }

    public static void setTraceEnabled(boolean enableTrace) {
        traceEnabled = enableTrace;
    }
    
    /**
     * @param message
     */
    public static void log(Object message) {
        if (traceEnabled) {
            traceOutput.println("HibernateProfilerTrace:: " + message);
        }
    }

    /**
     * @param message
     */
    public static void logStats(Object message) {
        if (traceEnabledForStats) {
            System.out.println(message);
        }
    }
    
    /**
     * @param message
     * @param e
     */
    public static void log(String message, Exception e) {
        if (traceEnabled) {
            traceOutput.println(message);
            traceOutput.println(e.getMessage());
            e.printStackTrace(traceOutput);
        }
    }

}
