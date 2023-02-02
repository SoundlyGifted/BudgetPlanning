
package com.ejb.database;

import java.sql.Connection;
import java.sql.SQLException;
import jakarta.ejb.Local;

/**
 * EJB DBConnectionProvider Local interface contains methods to create and close 
 * database connection.
 */
@Local
public interface DBConnectionProviderLocal {
    
    /**
     * Creates databaes connection based on config.properties (driver, host, 
     * port, database name, user name, user password).
     * 
     * @return database Connection.
     * @throws SQLException if some error occured while establishing the 
     * database connection.
     */
    public Connection getDBConnection() throws SQLException;
    
    /**
     * Closes database Connection.
     * 
     * @param connection database Connection
     * @throws java.sql.SQLException if some error occured while closing the 
     * database connection.
     */
    public void closeDBConnection(Connection connection) throws SQLException;    
}
