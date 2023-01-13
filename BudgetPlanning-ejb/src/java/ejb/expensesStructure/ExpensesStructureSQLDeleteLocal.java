
package ejb.expensesStructure;

import java.sql.Connection;
import jakarta.ejb.Local;

/**
 * EJB ExpensesStructureSQLDelete Local interface contains methods to perform
 * delete operations on Expenses records in the database.
 */
@Local
public interface ExpensesStructureSQLDeleteLocal {
    
    /**
     * Deletes record of Expense from the database.
     * 
     * @param connection database Connection.
     * @param id database Expense ID.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean executeDeleteById(Connection connection, String id);
}
