
package ejb.MainScreen;

import java.sql.Connection;
import java.util.Map;
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

    public Integer getPlanningPeriodsHorizon(Connection connection, String planningPeriodsFrequency);

    public boolean setPlanningPeriodsHorizon(Connection connection, String planningPeriodsFrequency,
            String planningPeriodsHorizon);

    public TreeSet<String> calculateTimePeriodDates(String currentPeriodDate,
            String planningPeriodsFrequency, Integer planningPeriodsHorizon);
}
