
package ejb.calculation;

import ejb.calculation.EntityExpense;
import java.sql.Connection;
import java.util.ArrayList;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ExpensesHandlerLocal {
      
    public ArrayList<EntityExpense> getEntityExpenseList();
     
    public void removeFromEntityExpenseList(EntityExpense entity);
    
    public EntityExpense prepareEntityExpenseById(Connection connection, 
            String inputPlanningPeriodsFrequency, Integer id);
    
    public ArrayList<EntityExpense> actualizeEntityExpenseList(Connection 
            connection);
}
