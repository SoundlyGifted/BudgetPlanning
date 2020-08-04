
package ejb.expensesStructure;

import ejb.calculation.EntityExpense;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
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

    public HashMap<Integer, String> executeSelectAllTypes(Connection connection);

    public HashMap<Integer, HashMap<String, Double>>
            executeSelectAllValues(Connection connection);

    public HashMap<Integer, HashMap<String, Integer>>
            executeSelectAllLinks(Connection connection);
            
    public Integer executeSelectIdByName (Connection connection, String name);
}
