
package com.ejb.database;

import java.sql.Connection;
import jakarta.ejb.Local;
import jakarta.servlet.http.HttpSession;

/**
 * EJB DBConnection Local interface contains methods to provide and close 
 * database Connection to necessary methods of EJB components.
 */
@Local
public interface DBConnectionLocal {
    
    /**
     * Creates and returns database Connection with the use of 
     * {@link DbConnectionProviderLocal#getConnection()} and assigns it to the 
     * session attribute.
     * 
     * @param session HttpSession to which attribute the Connection to be
     * assigned.
     * @param sessionAttributeName session attribute name for Connection 
     * assignment.
     * @return database Connection.
     */
    public Connection connection(HttpSession session,
            String sessionAttributeName);
    
    /**
     * Creates and returns database Connection with the use of 
     * {@link DbConnectionProviderLocal#getConnection()}.
     * 
     * @return database Connection.
     */
    public Connection connection();
    
    /**
     * Closes database Connection and removes it from session attribute.
     * 
     * @param session HttpSession to remove the database Connection attribute.
     * @param sessionAttributeName session attribute name.
     */
    public void closeConnection(HttpSession session, 
            String sessionAttributeName);
    
    /**
     * Closes database Connection.
     * 
     * @param connection database Connection to be closed.
     */
    public void closeConnection(Connection connection);
}
