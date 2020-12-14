//-----------------------------------------------------------------------
// <copyright file="UrlHelper.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import java.lang.reflect.Method;

public class UrlHelper {

    private static Method urlRetrievalMethod;

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
                    urlRetrievalMethod = profilerListenerClass.getMethod("getCurrentRequestUrl", null);
            } catch (Exception e) {
                throw new RuntimeException(e); //again, throw here because this should never happen
            }
        }
    }

    public static String getRequestUrl() {
        if (urlRetrievalMethod == null)
            return "";

        String url = "";
        try {
            url = (String)urlRetrievalMethod.invoke(null, null);
        } catch (Exception e) {
            HibernateProfilerTrace.log("ERROR ::: UrlHelper ::: Error accessing request URL", e);
        }
        return url;
    }

}
