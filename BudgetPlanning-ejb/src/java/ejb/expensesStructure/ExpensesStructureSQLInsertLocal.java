
package ejb.expensesStructure;

import java.sql.Connection;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ExpensesStructureSQLInsertLocal {
    
    public boolean execute(Connection connection, String type, String name, 
            String accountId, String price, String safetyStockPcs, 
            String orderQtyPcs);
}
