
package ejb.calculation.entityLists;

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
    
    public EntityExpense selectFromEntityExpenseListByName(
            Connection connection, String name);
    
    public EntityExpense selectFromEntityExpenseListById(Connection connection, 
            Integer id);
    
    public ArrayList<EntityExpense> actualizeEntityExpenseList(Connection 
            connection);
}
