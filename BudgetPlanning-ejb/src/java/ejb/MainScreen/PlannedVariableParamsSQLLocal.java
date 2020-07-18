
package ejb.MainScreen;

import java.sql.Connection;
import java.util.HashMap;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface PlannedVariableParamsSQLLocal {
    public boolean executeUpdate(Connection connection, String expenseId, 
            String paramName, HashMap<String, String> updatedValues);
}
