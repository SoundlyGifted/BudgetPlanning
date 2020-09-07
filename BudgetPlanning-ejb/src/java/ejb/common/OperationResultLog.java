
package ejb.common;

import javax.ejb.Stateless;
import javax.servlet.http.HttpSession;

/**
 * EJB OperationResultLog is used to to perform operations of Application Log.
 */
@Stateless
public class OperationResultLog implements OperationResultLogLocal {

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(HttpSession session, String resultToAdd) {
        if (session != null && resultToAdd != null && !resultToAdd.trim().isEmpty()) {
            String log = (String) session.getAttribute("operationResult");
            if (log == null) {
                log = resultToAdd;
            } else {
                log = log + "\n" + resultToAdd;
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
