//-----------------------------------------------------------------------
// <copyright file="HibernateProfilerListener.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

public class HibernateProfilerListener implements ServletContextListener, ServletRequestListener {

	private String applicationName;

    @SuppressWarnings("unchecked")
    private static final ThreadLocal CurrentRequest = new ThreadLocal() {{
       set("");
    }};

    public static String getCurrentRequestUrl() {
        return (String)CurrentRequest.get();
    }

    public String getApplicationName() {
    	return applicationName;
    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	applicationName = servletContextEvent.getServletContext().getServletContextName();
    	if (applicationName == null) {
    		applicationName = "";
    	}
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    	applicationName = null;
    }

    /**
     * @see javax.servlet.ServletRequestListener#requestInitialized(javax.servlet.ServletRequestEvent)
     */
    @SuppressWarnings("unchecked")
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        HttpServletRequest request = (HttpServletRequest)servletRequestEvent.getServletRequest();
        StringBuffer url = request.getRequestURL();
        CurrentRequest.set(url.toString());
    }

    /**
     * @see javax.servlet.ServletRequestListener#requestDestroyed(javax.servlet.ServletRequestEvent)
     */
    @SuppressWarnings("unchecked")
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        CurrentRequest.remove();
    }

}
