
package ejb.MainScreen;

import java.sql.Connection;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface PlannedAccountsValuesSQLLocal {
    
    public boolean executeUpdate(Connection connection, String accountId,
            String paramName, Map<String, String> updatedValues);
}
