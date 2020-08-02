
package ejb.MainScreen;

import java.sql.Connection;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface PlannedAccountsValuesSQLLocal {

    public boolean executeUpdate(Connection connection, String accountId,
            String paramName, Map<String, String> updatedValues);

    public TreeMap<String, Double> selectPlannedAccountsValuesById(Connection 
            connection, Integer id, String paramName);
    
    public boolean executeUpdateAll(Connection connection,
            String inputPlanningPeriodsFrequency);    
}
