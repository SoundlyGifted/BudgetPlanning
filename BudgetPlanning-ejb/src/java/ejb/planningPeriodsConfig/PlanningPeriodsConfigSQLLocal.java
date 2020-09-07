
package ejb.planningPeriodsConfig;

import java.sql.Connection;
import javax.ejb.Local;

/**
 * EJB PlanningPeriodsConfigSQL Local interface contains methods to change 
 * planning periods horizon in the database planning periods configuration 
 * table.
 */
@Local
public interface PlanningPeriodsConfigSQLLocal {
    
    /**
     * Gets current value of planning periods horizon from the database 
     * planning periods configuration table.
     * 
     * @param connection database Connection.
     * @param planningPeriodsFrequency frequency of the planning time periods.
     * @return planning periods horizon value obtained from the database.
     */
    public Integer getPlanningPeriodsHorizon(Connection 
            connection, String planningPeriodsFrequency);
    
    /**
     * Sets current value of planning periods horizon in the database 
     * planning periods configuration table.
     * 
     * @param connection database Connection.
     * @param planningPeriodsFrequency frequency of the planning time periods.
     * @param planningPeriodsHorizon planning time periods horizon.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean setPlanningPeriodsHorizon(Connection 
            connection, String planningPeriodsFrequency, 
            String planningPeriodsHorizon);
}
