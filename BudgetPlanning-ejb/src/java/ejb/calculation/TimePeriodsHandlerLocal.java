
package ejb.calculation;

import java.sql.Connection;
import java.util.TreeSet;
import jakarta.ejb.Local;

/**
 * EJB TimePeriodsHandler Local interface contains methods to perform 
 * operations on attributes of application planning time periods.
 */
@Local
public interface TimePeriodsHandlerLocal {
    
    /**
     * Calculates set of time period dates that starts with the current period
     * date specified in {@link TimePeriods#currentPeriodDate}. 
     * Needs {@link TimePeriods#currentPeriodDate} to be calculated
     * preliminarily.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @return set of time period dates that starts with the current period
     * date specified in {@link TimePeriods#currentPeriodDate}
     */
    public TreeSet<String> calculateTimePeriodDates(Connection 
            connection, String inputPlanningPeriodsFrequency);
    
    /**
     * Gets the date of next planning time period based on the value of
     * {@link TimePeriods#currentPeriodDate} and the input planning time periods
     * frequency.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @return date of next planning time period (relative to the current 
     * period date).
     */
    public String getNextPeriodDate(Connection connection,
            String inputPlanningPeriodsFrequency);
    
    /**
     * Gets the date of previous planning time period based on the value of
     * {@link TimePeriods#currentPeriodDate} and the input planning time periods
     * frequency.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @return date of previous planning time period (relative to the current 
     * period date).
     */
    public String getPreviousPeriodDate(Connection connection,
            String inputPlanningPeriodsFrequency);
}
