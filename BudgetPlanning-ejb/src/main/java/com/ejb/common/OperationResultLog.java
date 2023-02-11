
package com.ejb.common;

import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * EJB OperationResultLog is used to to perform operations of Application Log.
 */
@Stateless
public class OperationResultLog implements OperationResultLogLocal {

    DateFormat formatter = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]");
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(HttpSession session, String message) {
        if (session != null && message != null && !message.trim().isEmpty()) {
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
    public void clear(HttpSession session) {
        if (session != null) {
            session.removeAttribute("operationResult");
        }
    }
}
