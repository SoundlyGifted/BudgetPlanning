
package ejb.common;

import ejb.DBConnection.QueryProviderLocal;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.ejb.EJB;

/**
 *
 * @author SoundlyGifted
 */
public abstract class SQLAbstract extends EjbCommonMethods{
    
    @EJB
    private QueryProviderLocal queryProvider;
    
    public PreparedStatement createPreparedStatement(Connection connection,
            String path)
            throws SQLException, IOException {
        String className = this.getClass().getSimpleName();
        System.out.println("*** " + className + ": createPreparedStatement() "
                + "current connection = " + connection.hashCode());
        String query = queryProvider.getQuery(path);
        return connection.prepareStatement(query);
    }
       
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
