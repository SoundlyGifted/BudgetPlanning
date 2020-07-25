
package ejb.planningPeriodsConfig;

import java.sql.Connection;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface PlanningPeriodsConfigSQLLocal {
    
    public Integer getPlanningPeriodsHorizon(Connection 
            connection, String planningPeriodsFrequency);
    
    public boolean setPlanningPeriodsHorizon(Connection 
            connection, String planningPeriodsFrequency, 
            String planningPeriodsHorizon);
}
