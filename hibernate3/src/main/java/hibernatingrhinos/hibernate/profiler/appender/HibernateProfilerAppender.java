//-----------------------------------------------------------------------
// <copyright file="HibernateProfilerAppender.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import hibernatingrhinos.hibernate.profiler.appender.stacktracefilters.IStackTraceFilter;
import hibernatingrhinos.hibernate.profiler.messages.IProfilerMessage;
import hibernatingrhinos.hibernate.profiler.messages.LoggingEventMessage;
import hibernatingrhinos.hibernate.profiler.messages.SessionFactoryStats;
import hibernatingrhinos.hibernate.profiler.messages.StackTraceInfo;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.NDC;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HibernateProfilerAppender extends AppenderSkeleton {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    public List<OnLoggerCloseListener> closeListeners = Collections.synchronizedList(new ArrayList<OnLoggerCloseListener>());
    private StackTraceInfoGenerator stackTraceGenerator = new StackTraceInfoGenerator();
    private ArrayBlockingQueue<IProfilerMessage> eventMessages = new ArrayBlockingQueue<IProfilerMessage>(100000);
    private HibernateStatisticsProvider statisticsProvider;
    private Thread backgroundThreadForNotifyingProfiler;
    private IProfilerGateway gateway;
    private Lock sendingMessageLock = new ReentrantLock();

    private boolean stopSendingMessage;

    /**
     * @return the gateway
     */
    public IProfilerGateway getGateway() {
        return this.gateway;
    }

    /**
     * @param gateway the gateway to set
     */
    public void setGateway(IProfilerGateway gateway) {
        this.gateway = gateway;
    }

    private String getThreadId() {
      return String.valueOf(Thread.currentThread().getId());
    }

    public void addOnLoggerCloseListener(OnLoggerCloseListener listener) {
        closeListeners.add(listener);
    }

    public void removeOnLoggerCloseListener(OnLoggerCloseListener listener) {
        closeListeners.remove(listener);
    }

    public boolean requiresLayout() {
        return false;
    }

    protected void append(LoggingEvent loggingEvent) {
        try {
            if ("/* spying */".equals(NDC.peek())) return;

            String renderedMessage = loggingEvent.getRenderedMessage();
            LoggingEventMessage message = new LoggingEventMessage();
            message.setUrl(UrlHelper.getRequestUrl());
            message.setLogger(loggingEvent.getLoggerName());
            message.setMessage(renderedMessage);
            message.setThreadId(getThreadId());
            message.setSessionId(getThreadId());
            message.setLoggingLevel(loggingEvent.getLevel());
            message.setThreadContext(loggingEvent.getNDC());
            message.setStackTraceInfo(getStackTrace(loggingEvent.getLoggerName(), renderedMessage));
            if (loggingEvent.getThrowableInformation() != null)
                message.setException(loggingEvent.getThrowableInformation().getThrowable());
            eventMessages.offer(message, 250, TimeUnit.MILLISECONDS);
        }
        catch (Exception e)
        {
            errorHandler.error("Could not create message to send", e, ErrorCode.GENERIC_FAILURE);
        }
    }

    /**
     * This method needs to be idempontent because it is possible for the appender
     * to be closed multiple times.
     *
     * @see org.apache.log4j.AppenderSkeleton#close()
     */
    public void close() {
        resetStatisticTimerInternalInstance();

        stopSendingMessage = true;
        if (backgroundThreadForNotifyingProfiler != null) {
            try {
                backgroundThreadForNotifyingProfiler.join();
            } catch (InterruptedException e) {
                //ignore any exceptions closing the background thread
            }
        }

        sendingMessageLock.lock();
        try {
            flushMessagesToProfiler(eventMessages);
            eventMessages.clear();
        } finally {
            sendingMessageLock.unlock();
        }
    
        if (gateway != null)
            gateway.shutdown();

       Iterator<OnLoggerCloseListener> listenersIt = new ArrayList<OnLoggerCloseListener>(this.closeListeners).iterator();
       while (listenersIt.hasNext()) {
           OnLoggerCloseListener listener = listenersIt.next();
           listener.onClose();
       }

       executorService.shutdownNow();
    }

    public Object[] getQueuedMessages() {
        sendingMessageLock.lock();
        try {
            return eventMessages.toArray();
        } finally {
            sendingMessageLock.unlock();
        }
    }

    /**
     * @param copy
     */
    private void flushMessagesToProfiler(ArrayBlockingQueue<IProfilerMessage> copy) {
        try {
            if (gateway == null)
                return;

            gateway.sendMessage(copy.toArray(new IProfilerMessage[copy.size()]));
        } catch (Exception e) {
          //expected, if the profiler is not around, we can just ignore this
        }
    }

    public void activateOptions() {
        executorService.submit(new Runnable() {
            public void run() {
                initializeAppenderOnBackground();
            }
        });

        resetStatisticTimerInternalInstance();
    }

    /**
     * If the appender is finalized, we do not want to have the appender restarted by
     * the background thread.
     *
     * @see org.apache.log4j.AppenderSkeleton#finalize()
     */
    @Override
    public void finalize() {
        if (this.closed)
            return;

        LogLog.debug("Finalizing appender named ["+name+"].");
        closeListeners.clear();
        close();
    }

    private void initializeAppenderOnBackground()
    {
        try
        {
            statisticsProvider = new HibernateStatisticsProvider();

            if (backgroundThreadForNotifyingProfiler == null) {
                backgroundThreadForNotifyingProfiler = new Thread(new Runnable() {
                    public void run() {
                        try {
                            sendMessagesToProfiler();
                        } catch (Exception e) {
                            //ignore exception so our thread does not get killed
                        }
                    }
                });
                backgroundThreadForNotifyingProfiler.setDaemon(true);
                SimpleDateFormat sdf = new SimpleDateFormat();
                backgroundThreadForNotifyingProfiler.setName("HProf: " + sdf.format(new Date()));
                backgroundThreadForNotifyingProfiler.start();
            }
        }
        catch (Exception e) {
            LogLog.error("Could not initalize appender properly", e);
        }
    }

    private boolean resetStatisticTimerInternalInstance() {
        return true;
    }

    private boolean flushAllMessages() {
        if (gateway == null)
            return false;

        sendingMessageLock.lock();
        try {
            ArrayBlockingQueue<IProfilerMessage> copy = new ArrayBlockingQueue<IProfilerMessage>(100000);
            eventMessages.drainTo(copy);

            if (copy.size() == 0)
                return false;

            flushMessagesToProfiler(copy);
            return true;
        } finally {
            sendingMessageLock.unlock();
        }
    }

    private void sendStatisticInformation() {
        sendingMessageLock.lock();
        try {
            if (statisticsProvider == null)
                return;
            if (gateway == null)
                return;
            SessionFactoryStats[] statistics = statisticsProvider.getStatistics();
            if (statistics == null || statistics.length == 0) //the session factory is likely not initialized yet
                return;

            gateway.sendMessage(statistics);
        } catch (Exception e) {
            // this is expected, the profiler is probably not up.
        } finally {
            sendingMessageLock.unlock();
        }
    }

    private long lastSentStatistics = 0;

    private void sendMessagesToProfiler() throws InterruptedException {
        while (stopSendingMessage == false) {
            if (System.currentTimeMillis() - lastSentStatistics > 250) {
                sendStatisticInformation();
                lastSentStatistics = System.currentTimeMillis();
            }

            if (flushAllMessages() == false) {
                Thread.sleep(250);
            }
        }
    }

    private StackTraceInfo getStackTrace(String logger, String message) {
        if (requiresStackTrace(logger, message) == false)
            return null;

        return stackTraceGenerator.getStackTrace();
    }

    private static boolean requiresStackTrace(String logger, String message) {
        IStackTraceFilter[] filters = HibernateProfiler.stackTraceFilters;
        for (int i = 0; i < filters.length; i++) {
            if (filters[i].applies(logger, message))
                return true;
        }
        return false;
    }

    public static interface OnLoggerCloseListener {

        void onClose();

    }

}
