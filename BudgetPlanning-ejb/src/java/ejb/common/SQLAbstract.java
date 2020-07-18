
package ejb.common;

import ejb.DBConnection.QueryProviderLocal;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.ejb.EJB;

/**
 * Abstract class to contain common Methods that are used by Session Beans in
 * EJB module that interact with the database.
 * 
 * @author SoundlyGifted
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
     * @param connection Connection to the database.
     * @param path String path to the SQL file without extension (starting from 
     *             the "resources/sql/")
     * @return PreparedStatement based on the Connection and Path given.
     * @throws SQLException
     * @throws IOException 
     */
    public PreparedStatement createPreparedStatement(Connection connection,
            String path)
            throws SQLException, IOException {
        String className = this.getClass().getSimpleName();
        System.out.println("*** " + className + ": createPreparedStatement() "
                + "current connection = " + connection.hashCode());
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
