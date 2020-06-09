
package ejb.expensesStructure;

import java.sql.Connection;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ExpensesStructureSQLDeleteLocal {
    public boolean execute(Connection connection, String name, String title);
    public boolean execute(Connection connection, String id);
}
