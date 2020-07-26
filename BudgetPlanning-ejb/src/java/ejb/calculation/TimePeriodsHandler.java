
package ejb.calculation;

import ejb.MainScreen.PlannedVariableParamsSQLLocal;
import ejb.common.EjbCommonMethods;
import ejb.planningPeriodsConfig.PlanningPeriodsConfigSQLLocal;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author SoundlyGifted
 */
@Singleton
@Startup
public class TimePeriodsHandler extends EjbCommonMethods 
        implements TimePeriodsHandlerLocal {

    @EJB
    private PlannedVariableParamsSQLLocal plannedParams;
    
    @EJB
    private PlanningPeriodsConfigSQLLocal planningPeriodsConfig;    
    
    private boolean updateCurrentPeriodDate(Connection connection) {
        String newCurrentPeriodDate = plannedParams
                .getCurrentPeriodDate(connection);
        if (TimePeriods.currentPeriodDate == null || 
                TimePeriods.currentPeriodDate.trim().isEmpty()) {
            TimePeriods.currentPeriodDate = newCurrentPeriodDate;
            return true;
        } else {
            if (!TimePeriods.currentPeriodDate.equals(newCurrentPeriodDate)) {
                TimePeriods.currentPeriodDate = newCurrentPeriodDate;
                return true;
            }
        }
        return false;
    }
    
    private boolean updatePlanningPeriodsHorizon(Connection connection,
            String planningPeriodsFrequency) {
        Integer newPlanningPeriodsHorizon = planningPeriodsConfig
                .getPlanningPeriodsHorizon(connection, 
                        planningPeriodsFrequency);
        if (TimePeriods.planningPeriodsHorizon == null) {
            TimePeriods.planningPeriodsHorizon = newPlanningPeriodsHorizon;
            return true;
        } else {
            if (!Objects.equals(TimePeriods.planningPeriodsHorizon, 
                    newPlanningPeriodsHorizon)) {
                TimePeriods.planningPeriodsHorizon = newPlanningPeriodsHorizon;
                return true;
            }
        }
        return false;
    }

    @Override
    public TreeSet<String> calculateTimePeriodDates(Connection 
            connection, String inputPlanningPeriodsFrequency) {

        boolean freqencyUpdated;
        boolean currentPeriodDateUpdated;
        boolean planningPeriodsHorizonUpated;
        /* Check if input planning Periods Frequency is correct value from 
         * the list of allowed Frequencies.
         */
        if (!inputCheckFrequency(inputPlanningPeriodsFrequency)) {
            return TimePeriods.timePeriodDates;
        } else {
            if (TimePeriods.planningPeriodsFrequency == null || 
                    TimePeriods.planningPeriodsFrequency.trim().isEmpty()) {
                TimePeriods.planningPeriodsFrequency 
                        = inputPlanningPeriodsFrequency;
                freqencyUpdated = true;
            } else {
                if (!TimePeriods.planningPeriodsFrequency
                        .equals(inputPlanningPeriodsFrequency)) {
                    TimePeriods.planningPeriodsFrequency 
                            = inputPlanningPeriodsFrequency;
                    freqencyUpdated = true;                
                } else {
                    freqencyUpdated = false;
                }                
            }
        }
        currentPeriodDateUpdated = updateCurrentPeriodDate(connection);
        planningPeriodsHorizonUpated = updatePlanningPeriodsHorizon(connection, 
                TimePeriods.planningPeriodsFrequency);
        /* return current value of timePeriodDates if no parameter changed since
         * last calculation (no need to make same calculation again).
         */       
        if (!freqencyUpdated && !currentPeriodDateUpdated && 
                !planningPeriodsHorizonUpated) {
            return TimePeriods.timePeriodDates;
        }

        TreeSet<String> result = new TreeSet<>();
        result.add(TimePeriods.currentPeriodDate);
        String tempDate = TimePeriods.currentPeriodDate;

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        for (int i = 1; i < TimePeriods.planningPeriodsHorizon; i++) {
            try {
                c.setTime(fmt.parse(tempDate));
            } catch (ParseException ex) {
                System.out.println("EntityExpense: calculateTimePeriodDates() "
                        + "- error while parsing next date " + tempDate + " : "
                        + ex.getMessage());
            }
            switch (TimePeriods.planningPeriodsFrequency) {
                case "W":
                    c.add(Calendar.DAY_OF_MONTH, 7);
                    break;
                case "M":
                    c.add(Calendar.MONTH, 1);
                    break;
                case "D":
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                default:
                    return null;
            }
            String newDate = fmt.format(c.getTime());
            result.add(newDate);
            tempDate = newDate;
        }
        TimePeriods.timePeriodDates = result;
        return result;
    }
}
