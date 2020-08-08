
package ejb.calculation;

import java.sql.Connection;
import java.util.TreeSet;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface TimePeriodsHandlerLocal {
    
    public TreeSet<String> calculateTimePeriodDates(Connection 
            connection, String inputPlanningPeriodsFrequency);
    
    public String getNextPeriodDate(Connection connection,
            String inputPlanningPeriodsFrequency);
    
    public String getPreviousPeriodDate(Connection connection,
            String inputPlanningPeriodsFrequency);
}
