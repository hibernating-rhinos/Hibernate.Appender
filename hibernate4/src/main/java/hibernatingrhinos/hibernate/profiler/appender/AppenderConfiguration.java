//-----------------------------------------------------------------------
// <copyright file="AppenderConfiguration.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

public class AppenderConfiguration {

    private String address = HibernateProfiler.DefaultHost;
    private int port = HibernateProfiler.DefaultPort;
    private boolean captureStackTraceFor2ndLevelCache = true;
    private boolean captureStackTraceForSql = true;
    private boolean captureStackTraceForTransactions = true;
        
    /**
     * @return the address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return this.port;
    }
    
    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the captureStackTraceFor2ndLevelCache
     */
    public boolean isCaptureStackTraceFor2ndLevelCache() {
        return this.captureStackTraceFor2ndLevelCache;
    }
    
    /**
     * @param captureStackTraceFor2ndLevelCache the captureStackTraceFor2ndLevelCache to set
     */
    public void setCaptureStackTraceFor2ndLevelCache(boolean captureStackTraceFor2ndLevelCache) {
        this.captureStackTraceFor2ndLevelCache = captureStackTraceFor2ndLevelCache;
    }
    
    /**
     * @return the captureStackTraceForSql
     */
    public boolean isCaptureStackTraceForSql() {
        return this.captureStackTraceForSql;
    }
    
    /**
     * @param captureStackTraceForSql the captureStackTraceForSql to set
     */
    public void setCaptureStackTraceForSql(boolean captureStackTraceForSql) {
        this.captureStackTraceForSql = captureStackTraceForSql;
    }
    
    /**
     * @return the captureStackTraceForTransactions
     */
    public boolean isCaptureStackTraceForTransactions() {
        return this.captureStackTraceForTransactions;
    }
    
    /**
     * @param captureStackTraceForTransactions the captureStackTraceForTransactions to set
     */
    public void setCaptureStackTraceForTransactions(boolean captureStackTraceForTransactions) {
        this.captureStackTraceForTransactions = captureStackTraceForTransactions;
    }

}
