//-----------------------------------------------------------------------
// <copyright file="ApplicationNameHelper.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import java.lang.reflect.Method;

public class ApplicationNameHelper {

    private static Method applicationNameRetrievalMethod;
    private static String applicationName;

    /**
     * Uses reflection to load HibernateProfilerListener in case the profiler is not being used
     * within a web application and does have the Servlet API in the classpath.
     */
    static {
        boolean isWebApp = false;
        try {
            isWebApp = Class.forName("javax.servlet.http.HttpServletRequest") != null;
        } catch (ClassNotFoundException e) {
            //ignore
        }

        if (isWebApp) {
            Class profilerListenerClass = null;
            try {
                profilerListenerClass = Class.forName("hibernatingrhinos.hibernate.profiler.web.HibernateProfilerListener");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e); //don't ignore this one, should never happen
            }

            try {
                if (profilerListenerClass != null)
                	applicationNameRetrievalMethod = profilerListenerClass.getMethod("getApplicationName", null);
            } catch (Exception e) {
                throw new RuntimeException(e); //again, throw here because this should never happen
            }
        }
    }

    public static String getApplicationName() {
    	if (applicationName != null)
    		return applicationName;

        if (applicationNameRetrievalMethod == null)
            return "";

        String name = "";
        try {
            name = (String)applicationNameRetrievalMethod.invoke(null, null);
        } catch (Exception e) {
            HibernateProfilerTrace.log("ERROR ::: ApplicationNameHelper ::: Error accessing request Application Name", e);
        }
        return name;
    }

    /**
     * Allow clients to setup name of application.
     *
     * @param applicationName
     */
    public static void setApplicationName(String applicationName) {
    	ApplicationNameHelper.applicationName = applicationName;
    }
}
