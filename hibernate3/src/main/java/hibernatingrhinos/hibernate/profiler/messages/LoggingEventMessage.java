//-----------------------------------------------------------------------
// <copyright file="LoggingEventMessage.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.messages;


import org.apache.log4j.Level;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class LoggingEventMessage implements IProfilerMessage {

    private String message = "";
    private String logger = "";
    private Date date = new Date();
    private String threadId = "";
    private String sessionId = "";
    private Level loggingLevel = Level.OFF;
    private String url = "";
    private String threadContext = "";
    private Throwable exception;
    private StackTraceInfo stackTraceInfo;

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the logger
     */
    public String getLogger() {
        return this.logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(String logger) {
        this.logger = logger;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the loggingLevel
     */
    public Level getLoggingLevel() {
        return this.loggingLevel;
    }

    /**
     * @param level the loggingLevel to set
     */
    public void setLoggingLevel(Level level) {
        this.loggingLevel = level;
    }

    /**
     * @return the threadId
     */
    public String getThreadId() {
        return this.threadId;
    }

    /**
     * @param threadId the threadId to set
     */
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        if (url != null)
            this.url = url;
    }

    /**
     * @return the threadContext
     */
    public String getThreadContext() {
        return this.threadContext;
    }

    /**
     * @param threadContext the threadContext to set
     */
    public void setThreadContext(String threadContext) {
        if (threadContext != null)
            this.threadContext = threadContext;
    }

    /**
     * @return the stackTraceInfo
     */
    public StackTraceInfo getStackTraceInfo() {
        return this.stackTraceInfo;
    }

    /**
     * @param stackTraceInfo the stackTraceInfo to set
     */
    public void setStackTraceInfo(StackTraceInfo stackTraceInfo) {
        this.stackTraceInfo = stackTraceInfo;
    }

    /**
     * @return the exception
     */
    public Throwable getException() {
        return this.exception;
    }

    /**
     * @param throwable the exception to set
     */
    public void setException(Throwable throwable) {
        this.exception = throwable;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @see hibernatingrhinos.hibernate.profiler.messages.IProfilerMessage#toXml()
     */
    public String toXml() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<LoggingEventInfo>");
        buffer.append("<Message><![CDATA[").append(message).append("]]></Message>");
        buffer.append("<Logger>").append(logger).append("</Logger>");
        if (url != null)
            buffer.append("<Url><![CDATA[").append(url).append("]]></Url>");
        if (threadContext != null)
            buffer.append("<ThreadContext><![CDATA[").append(threadContext).append("]]></ThreadContext>");
        buffer.append("<Date>").append(date.getTime()).append("</Date>");
        if (stackTraceInfo != null)
            buffer.append("<StackTraceInfo>").append(stackTraceInfo.toXml()).append("</StackTraceInfo>");
        buffer.append("<Level>").append(loggingLevel.toInt()).append("</Level>");
        buffer.append("<SessionId>").append(sessionId).append("</SessionId>");
        buffer.append("</LoggingEventInfo>");
        return buffer.toString();
    }

    /**
     * @param throwable
     * @return
     */
    private static String getExceptionString(Throwable throwable) {
        String exceptionString = "";
        if (throwable != null) {
            StringWriter writer = new StringWriter();
            writer.write(throwable.getMessage());
            throwable.printStackTrace(new PrintWriter(writer));
            exceptionString = writer.toString();
        }
        return exceptionString;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toXml();
    }

}

