
package ejb.expensesStructure;

import ejb.calculation.EntityExpense;
import java.sql.Connection;
import java.util.ArrayList;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ExpensesStructureSQLSelectLocal {
    
    public ArrayList<EntityExpense> executeSelectAll(Connection connection);
    
    public EntityExpense executeSelectByName(Connection connection, 
            String name);
    
    public EntityExpense executeSelectById(Connection connection, Integer id);
}
