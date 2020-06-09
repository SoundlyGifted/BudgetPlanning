
package ejb.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface DbConnectionProviderLocal {
    public Connection connection() throws SQLException;
    public Connection getConnection() throws SQLException;
}
