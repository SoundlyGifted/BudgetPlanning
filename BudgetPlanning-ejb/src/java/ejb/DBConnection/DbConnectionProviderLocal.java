
package ejb.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import jakarta.ejb.Local;

/**
 * EJB DBConnectionProvider Local interface contains methods to create database
 * Connection.
 */
@Local
public interface DbConnectionProviderLocal {
    
    /**
     * Returns database Connection
     * 
     * @return database Connection
     * @throws SQLException 
     */
    public Connection connection() throws SQLException;
    
    /**
     * Creates databaes connection based on config.properties (driver, host, 
     * port, database name, user name, user password).
     * 
     * @return database Connection.
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException;
}
