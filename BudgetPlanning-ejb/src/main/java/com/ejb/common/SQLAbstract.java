
package com.ejb.common;

import com.ejb.database.QueryProviderLocal;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.ejb.EJB;

/**
 * SQLAbstract class is extended by Session Beans in EJB module that interact 
 * with the database and contain common database operation methods.
 */
public abstract class SQLAbstract extends EjbCommonMethods{
    
    /**
     * Link to the EJB component that retrieves SQL query from SQL file located 
     * in the given path.
     */
    @EJB
    private QueryProviderLocal queryProvider;
    
    /**
     * Creates PreparedStatement based on the Connection and Path given.
     * 
     * @param connection database Connection.
     * @param path String path to the SQL file.
     * @return PreparedStatement based on the Connection and Path given.
     * @throws SQLException
     * @throws IOException 
     */
    public PreparedStatement createPreparedStatement(Connection connection,
            String path)
            throws SQLException, IOException {
        String query = queryProvider.getQuery(path);
        return connection.prepareStatement(query);
    }
    
    /**
     * Clears (closes) the given PreparedStatement.
     * 
     * @param preparedStatement PreparedStatement to be closed.
     */
    public void clear(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                preparedStatement = null;
            } catch (SQLException ex) {
                System.out.println("*** PreparedStatement error "
                        + "druing closing: " + ex.getMessage());
            }
        }
    }
}
