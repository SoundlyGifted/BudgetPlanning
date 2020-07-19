
package ejb.expensesStructure;

import java.sql.Connection;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ExpensesStructureSQLDeleteLocal {
    
    public boolean executeDeleteById(Connection connection, String id);
}
