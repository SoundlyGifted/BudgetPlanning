
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
            String accountName, String linkedToComplexId, String price, 
            String safetyStock, String orderQty, String shopName);
    
    public boolean clearAssignmentToComplexExpense(Connection connection, 
            String name);
    
    public boolean clearAssignmentToAccount(Connection connection, String name);
    
    public boolean clearShopName(Connection connection, String name);
}
