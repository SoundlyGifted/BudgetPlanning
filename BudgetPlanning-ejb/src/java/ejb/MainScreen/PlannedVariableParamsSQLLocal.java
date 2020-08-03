
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
public interface PlannedVariableParamsSQLLocal {

    public boolean executeUpdate(Connection connection, String expenseId,
            String paramName, Map<String, String> updatedValues);

    public String getCurrentPeriodDate(Connection connection);

    public boolean setCurrentPeriodDate(Connection connection, String date);
    
    public TreeMap<String, Double> selectPlannedExpensesById(Connection 
            connection, Integer id);
    
    public TreeMap<String, Double> selectConsumptionPcsById(Connection 
            connection, Integer id);
    
    public boolean executeUpdateAll(Connection connection, 
            String inputPlanningPeriodsFrequency);
    
    public boolean executeDeleteByExpenseId(Connection connection, String id);
}
