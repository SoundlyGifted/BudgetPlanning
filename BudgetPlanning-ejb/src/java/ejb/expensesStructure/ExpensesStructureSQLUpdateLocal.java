
package ejb.expensesStructure;

import java.sql.Connection;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ExpensesStructureSQLUpdateLocal {
    
    public boolean execute(Connection connection, String name, String newName, 
            String accountId, String linkedToComplexId, String price,
            String currentStockPcs, String safetyStockPcs, String orderQtyPcs);
    
    public boolean clearAssignmentToComplexExpense(Connection connection, 
            String name);
    
    public boolean updateCurrentStockById(Connection connection, Integer id, 
            Double newCurrentStockPcs);
}
