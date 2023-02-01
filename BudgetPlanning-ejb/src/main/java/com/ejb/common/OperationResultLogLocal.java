
package com.ejb.common;

import jakarta.ejb.Local;
import jakarta.servlet.http.HttpSession;

/**
 * EJB OperationResultLog Local interface contains methods to perform operations
 * of Application Log.
 */
@Local
public interface OperationResultLogLocal {
    
    /**
     * Sets given String record representing result of any application operation 
     * to the "operationResult" attribute of given HttpSession.
     * 
     * @param session HttpSession to which "operationResult" attribute the 
     * operation result record will be added.
     * @param resultToAdd String record representing result of any application 
     * operation.
     */
    public void add(HttpSession session, String resultToAdd);
    
    /**
     * Clears "operationResult" attribute of a given HttpSession.
     * 
     * @param session HttpSession of which "operationResult" attribute will be
     * cleared.
     */
    public void clear(HttpSession session);
}
