
package ejb.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpSession;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
@DependsOn("DbConnectionProvider")
public class DBConnection implements DBConnectionLocal {

    @EJB
    private DbConnectionProviderLocal connectionProvider;

    /* Creating connection and assigning it to the given attribute of the 
    given session. */
    @Override
    public Connection connection(HttpSession session,
            String sessionAttributeName) {
        System.out.println("*** Checking DB connection for current session = "
                + session.hashCode() + ", session attribute = '"
                + sessionAttributeName + "'");
        Connection DBConnection
                = (Connection) session.getAttribute(sessionAttributeName);
        try {
            if (DBConnection == null || DBConnection.isClosed()) {
                System.out.println("*** DBConnection does not exist, "
                        + "getting connection...");
                DBConnection = connectionProvider.getConnection();
                session.setAttribute(sessionAttributeName, DBConnection);
            }
        } catch (SQLException ex) {
            System.out.println("*** DBConnection establishing error : "
                    + ex.getMessage());
        }
        if (DBConnection != null) {
            System.out.println("*** DBConnection established = "
                    + DBConnection.hashCode() + ", and placed to the session "
                    + "attribute named '" + sessionAttributeName + "'");
        }
        return DBConnection;
    }

    @Override
    public Connection connection() {
        System.out.println("*** Attempt to establish DBConnection ...");
        Connection DBconnection = null;
        try {
            DBconnection = connectionProvider.getConnection();
        } catch (SQLException ex) {
            System.out.println("*** DBConnection establishing error : "
                    + ex.getMessage());
        }
        if (DBconnection != null) {
            System.out.println("*** DBConnection established = "
                    + DBconnection.hashCode());
        }
        return DBconnection;
    }

    /* Removing assigned connection from the session attribute and closing the 
    connection. */
    @Override
    public void closeConnection(HttpSession session,
            String sessionAttributeName) {
        System.out.println("*** Checking DB connection for current session = "
                + session.hashCode() + ", session attribute = '"
                + sessionAttributeName + "'");
        Connection DBConnection
                = (Connection) session.getAttribute(sessionAttributeName);
        try {
            if (DBConnection == null || DBConnection.isClosed()) {
            } else {
                System.out.println("*** DB connection exists, attempt to "
                        + "close...");
                session.removeAttribute(sessionAttributeName);
                DBConnection.close();
                DBConnection = null;
            }
        } catch (SQLException ex) {
            System.out.println("*** DBConnection closing error : "
                    + ex.getMessage());
        }
        System.out.println("*** DB connection closed for the session.");
    }

    @Override
    public void closeConnection(Connection connection) {
        try {
            if (connection == null || connection.isClosed()) {
            } else {
                connection.close();
                connection = null;
            }
        } catch (SQLException ex) {
            System.out.println("*** DBConnection closing error : "
                    + ex.getMessage());
        }
        System.out.println("*** DBConnection closed. ");
    }
}
