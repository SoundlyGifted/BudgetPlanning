
package com.ejb.database;

import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.sql.SQLException;
import jakarta.ejb.DependsOn;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpSession;

/**
 * EJB "DBConnection" is used to provide and close database Connection to
 * necessary methods of EJB components.
 */
@Stateless
@DependsOn("DBConnectionProvider")
public class DBConnection implements DBConnectionLocal {

    @EJB
    private DBConnectionProviderLocal connectionProvider;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Connection connection(HttpSession session,
            String sessionAttributeName) throws GenericDBException {
        Connection DBConnection
                = (Connection) session.getAttribute(sessionAttributeName);
        try {
            if (DBConnection == null || DBConnection.isClosed()) {
                DBConnection = connectionProvider.getDBConnection();
                session.setAttribute(sessionAttributeName, DBConnection);
            }
        } catch (SQLException sqlex) {
            throw new GenericDBException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
        return DBConnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection connection() throws GenericDBException {
        return connectionProvider.getDBConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeConnection(HttpSession session,
            String sessionAttributeName) throws GenericDBException {
        Connection DBConnection
                = (Connection) session.getAttribute(sessionAttributeName);
        connectionProvider.closeDBConnection(DBConnection);
        session.removeAttribute(sessionAttributeName);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void closeConnection(Connection connection)
            throws GenericDBException {
        connectionProvider.closeDBConnection(connection);
    }
}
