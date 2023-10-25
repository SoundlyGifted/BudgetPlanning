
package com.ejb.common;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EJB OperationResultLog is used to to perform Logging operations.
 */
@Stateless
public class OperationResultLog implements OperationResultLogLocal {

    DateFormat formatter = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]");
    
    @EJB
    private LoggerProviderLocal loggerProvider;
    
    private Logger logger;
    private Handler handler;

    private Logger getLogger() {
        if (logger == null) {
            logger = loggerProvider.getLogger();
        }
        handler = loggerProvider.getFileHandler();
        logger.addHandler(handler);
        return logger;
    }
    
    // Adds the message to the Application log (to display to the user).
    private void addToApplicationLog(HttpSession session, String message) {
        if (session != null) {
            // Adding the formatted date/time to the message string.
            Date currentDateTime = new Date();
            String currentDateTimeFormatted = formatter.format(currentDateTime);
            message = currentDateTimeFormatted + " " + message;
            /* Recording the message to the session log attribute 
             * (for displaying to the user).
             */
            String log = (String) session.getAttribute("operationResult");
            if (log == null) {
                log = message;
            } else {
                log = log + "\n" + message;
            }
            session.setAttribute("operationResult", log);
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(HttpSession session, String message) {
        if (message != null && !message.trim().isEmpty()) {
            getLogger().log(Level.INFO, message);
            handler.close();
            addToApplicationLog(session, message);
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(HttpSession session, String message, Throwable ex) {
        if (message != null && !message.trim().isEmpty() && ex != null) {
            getLogger().log(Level.SEVERE, message, ex);
            handler.close();
            addToApplicationLog(session, message);
        }
    }    
    
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public void clear(HttpSession session) {
        if (session != null) {
            session.removeAttribute("operationResult");
        }
    }
}
