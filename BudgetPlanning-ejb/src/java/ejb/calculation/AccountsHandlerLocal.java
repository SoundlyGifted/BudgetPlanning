
package ejb.calculation;

import java.sql.Connection;
import java.util.ArrayList;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface AccountsHandlerLocal {

    public void removeFromEntityAccountList(EntityAccount entity);

    public ArrayList<EntityAccount> getEntityAccountList();

    public ArrayList<EntityAccount> 
        actualizeEntityAccountList(Connection connection);

    public EntityAccount prepareEntityAccountByExpenseId(Connection connection,
            String inputPlanningPeriodsFrequency, Integer inputExpenseId);

    public EntityAccount prepareEntityAccountById(Connection connection,
            String inputPlanningPeriodsFrequency, Integer id); 
    
    public boolean calculateAllCurrentRemainderCurForNextPeriod(Connection connection);
    
    public boolean
            calculateAllCurrentRemainderCurForPreviousPeriod(Connection 
                    connection);
}
