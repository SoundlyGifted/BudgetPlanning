
package ejb.DBConnection;

import java.sql.Connection;
import javax.ejb.Local;
import javax.servlet.http.HttpSession;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface DBConnectionLocal {
    
    public Connection connection(HttpSession session,
            String sessionAttributeName);
    
    public Connection connection();
    
    public void closeConnection(HttpSession session, 
            String sessionAttributeName);
    
    public void closeConnection(Connection connection);
}
