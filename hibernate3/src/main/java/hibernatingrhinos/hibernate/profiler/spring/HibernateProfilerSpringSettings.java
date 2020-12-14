//-----------------------------------------------------------------------
// <copyright file="HibernateProfilerSpringSettings.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.spring;

public class HibernateProfilerSpringSettings {

    private String initOnStartup;
    private String host;
    private String port;
    private String useSpy;

    public String getInitOnStartup() {
        return initOnStartup;
    }

    public void setInitOnStartup(String initOnStartup) {
        this.initOnStartup = initOnStartup;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUseSpy() {
        return useSpy;
    }

    public void setUseSpy(String useSpy) {
        this.useSpy = useSpy;
    }
}
