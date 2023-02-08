
package com.web.common;

import com.ejb.database.DBConnectionLocal;
import com.ejb.database.exceptions.GenericDBException;
import jakarta.ejb.EJB;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Web application lifecycle listener.
 */
@WebListener
public class NewListener implements HttpSessionListener, 
        HttpSessionAttributeListener {

    @EJB
    private DBConnectionLocal connector;
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession s = se.getSession();
        /* Setting initial System Message Log message. */
        s.setAttribute("operationResult", 
                "Awaiting for initial user command...");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession s = se.getSession();
        try {
            connector.closeConnection(s, "expensesStructureDBConnection");
            connector.closeConnection(s, "accountsStructureDBConnection");
            connector.closeConnection(s, "actualExpensesDBConnection");
            connector.closeConnection(s, "mainScreenDBConnection");        
        } catch (GenericDBException ex) { }
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        
    }

}
