
package com.ejb.database;

import com.ejb.database.exceptions.GenericDBException;
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
     * @throws com.ejb.database.exceptions.GenericDBException in case of 
     * database connection establishing error.
     */
    public Connection connection(HttpSession session,
            String sessionAttributeName) throws GenericDBException;
    
    /**
     * Creates and returns database Connection with the use of 
     * {@link DbConnectionProviderLocal#getConnection()}.
     * 
     * @return database Connection.
     * @throws com.ejb.database.exceptions.GenericDBException in case of 
     * database connection establishing error.
     */
    public Connection connection() throws GenericDBException;
    
    /**
     * Closes database Connection using {@link DbConnectionProviderLocal#closeDBConnection(Connection connection)} 
     * and removes it from session attribute.
     * 
     * @param session HttpSession to remove the database Connection attribute.
     * @param sessionAttributeName session attribute name.
     * @throws com.ejb.database.exceptions.GenericDBException in case of 
     * database connection establishing error.
     */
    public void closeConnection(HttpSession session, 
            String sessionAttributeName) throws GenericDBException;
    
    /**
     * Closes database Connection using {@link DbConnectionProviderLocal#closeDBConnection(Connection connection)}.
     * 
     * @param connection database Connection to be closed.
     * @throws com.ejb.database.exceptions.GenericDBException in case of 
     * database connection establishing error.
     */
    public void closeConnection(Connection connection) throws GenericDBException;
}
