
package com.ejb.database;

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
@DependsOn("DbConnectionProvider")
public class DBConnection implements DBConnectionLocal {

    @EJB
    private DbConnectionProviderLocal connectionProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection connection(HttpSession session,
            String sessionAttributeName) {
        Connection DBConnection
                = (Connection) session.getAttribute(sessionAttributeName);
        try {
            if (DBConnection == null || DBConnection.isClosed()) {
                DBConnection = connectionProvider.getDBConnection();
                session.setAttribute(sessionAttributeName, DBConnection);
            }
        } catch (SQLException ex) {
            System.out.println("*** DBConnection establishing error : "
                    + ex.getMessage());
        }
        return DBConnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection connection() {
        Connection DBconnection = null;
        try {
            DBconnection = connectionProvider.getDBConnection();
        } catch (SQLException ex) {
            System.out.println("*** DBConnection establishing error : "
                    + ex.getMessage());
        }
        return DBconnection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeConnection(HttpSession session,
            String sessionAttributeName) {
        Connection DBConnection
                = (Connection) session.getAttribute(sessionAttributeName);
        try {
            connectionProvider.closeDBConnection(DBConnection);
            session.removeAttribute(sessionAttributeName);
        } catch (SQLException ex) {
            System.out.println("*** DBConnection closing error : "
                    + ex.getMessage());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void closeConnection(Connection connection) {
        try {
            connectionProvider.closeDBConnection(connection);
        } catch (SQLException ex) {
            System.out.println("*** DBConnection closing error : "
                    + ex.getMessage());
        }
    }
}
