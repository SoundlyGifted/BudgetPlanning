
package ejb.expensesStructure;

import java.sql.Connection;
import jakarta.ejb.Local;

/**
 * EJB ExpensesStructureSQLUpdate Local interface contains methods to perform
 * update operations on Expenses records in the database.
 */
@Local
public interface ExpensesStructureSQLUpdateLocal {
    
    /**
     * Updates record of Expense in the database with given values of the
     * Expense attributes.
     * 
     * @param connection database Connection.
     * @param name Expense current name.
     * @param newName Expense new name.
     * @param accountId Expense linked Account database ID.
     * @param linkedToComplexId database ID of the Complex Expense to which this
     * Expense is linked.
     * @param price Expense planning price (price for planning purpose).
     * @param currentStockPcs Expense Current Stock (PCS) attribute value.
     * @param safetyStockPcs Expense Safety Stock (PCS) attribute value.
     * @param orderQtyPcs Expense Order Quantity (PCS) attribute value.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean execute(Connection connection, String name, String newName, 
            String accountId, String linkedToComplexId, String price,
            String currentStockPcs, String safetyStockPcs, String orderQtyPcs);
    
    /**
     * Clears assignment to Complex Expense for the given Expense.
     * 
     * @param connection database Connection.
     * @param name Expense name.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean clearAssignmentToComplexExpense(Connection connection, 
            String name);
    
    /**
     * Updates Current Stock (PCS) attribute of Expense with given database ID
     * in the database.
     * 
     * @param connection database Connection.
     * @param id database Expense ID.
     * @param newCurrentStockPcs new value of Current Stock (PCS) attribute.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean updateCurrentStockById(Connection connection, Integer id, 
            Double newCurrentStockPcs);
}
