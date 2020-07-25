
package ejb.MainScreen;

import java.sql.Connection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
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

    public TreeSet<String> calculateTimePeriodDates(String currentPeriodDate,
            String planningPeriodsFrequency, Integer planningPeriodsHorizon);
}
