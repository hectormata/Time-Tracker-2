package com.main.java.task.Utils.Logs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LoggerUtil {

    private Log logger = null;

    public static LoggerUtil getLogger(Class<?> t) {
        return new LoggerUtil(t);
    }

    private LoggerUtil(Class<?> t) {
        logger = LogFactory.getLog(t);
    }

    public void Destroy() {
        logger = null;
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public void debug(String method, Object txt) {
        if (logger.isDebugEnabled())
            logger.debug(getTextToLog(method, txt));
    }

    public void info(String method, Object txt) {
        if (logger.isInfoEnabled())
            logger.info(getTextToLog(method, txt));
    }

    public void warn(String method, Object txt) {
        logger.warn(getTextToLog(method, txt));
    }

    public void error(String method, String txt) {
        logger.error(getTextToLog(method, txt));
    }

    public void error(String method, String msg, Exception e) {
        logger.error(getTextToLog(method, msg), e);
    }

    public void error(String method, Exception e) {
        logger.error(getTextToLog(method, e.getMessage()), e);
    }

    private String getTextToLog(String method, Object txt) {
        return txt == null ? (method + ": -null-data-") : (method + ": " + txt.toString());
    }
}
