package web.expensesStructure;

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
 *
 * @author SoundlyGifted
 */
@WebListener
public class NewServletListener implements HttpSessionListener, HttpSessionAttributeListener {

    @EJB
    private DBConnectionLocal connector;
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession s = se.getSession();
        System.out.println("*** A new session created : " + s.getId() + ", " + s.hashCode());
        
        /* Setting initial System Message Log message. */
        s.setAttribute("operationResult", "Awaiting for initial user command...");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession s = se.getSession();
        connector.closeConnection(s, "expensesStructureDBConnection");
        System.out.println("*** Session destroyed : " + s.getId() + ", " + s.hashCode());
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
