package web.common;

import ejb.DBConnection.DBConnectionLocal;
import javax.ejb.EJB;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

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
        connector.closeConnection(s, "expensesStructureDBConnection");
        connector.closeConnection(s, "accountsStructureDBConnection");
        connector.closeConnection(s, "actualExpensesDBConnection");
        connector.closeConnection(s, "mainScreenDBConnection");
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
