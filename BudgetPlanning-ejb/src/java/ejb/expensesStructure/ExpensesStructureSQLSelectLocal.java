
package ejb.expensesStructure;

import ejb.entity.EntityExpense;
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
    public EntityExpense executeSelectByNameAndTitle(Connection connection, 
            String name, String title);
    public EntityExpense executeSelectById(Connection connection, Integer id);
}
