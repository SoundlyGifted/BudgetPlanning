
package ejb.MainScreen;

import java.sql.Connection;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface PlannedVariableParamsSQLLocal {

    public boolean executeUpdate(Connection connection, String expenseId,
            String paramName, Map<String, String> updatedValues);

    public String getCurrentPeriodDate(Connection connection);

    public boolean setCurrentPeriodDate(Connection connection, String date);
}
