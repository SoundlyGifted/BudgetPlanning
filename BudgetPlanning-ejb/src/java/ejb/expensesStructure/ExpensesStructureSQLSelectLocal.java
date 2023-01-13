
package ejb.expensesStructure;

import ejb.calculation.EntityExpense;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import jakarta.ejb.Local;

/**
 * EJB ExpensesStructureSQLSelect Local interface contains methods to perform
 * select operations of Expenses records in the database.
 */
@Local
public interface ExpensesStructureSQLSelectLocal {

    /**
     * Selects all records of Expenses from the database and retruns list of 
     * EntityExpense objects with variable values from the corresponding 
     * database records.
     * 
     * @param connection database Connection.
     * @return ArrayList of EntityExpense objects with variable values from the
     * corresponding database records.
     */
    public ArrayList<EntityExpense> executeSelectAll(Connection connection);

    /**
     * Selects record of Expense from database by Name and returns EntityExpense
     * object with the corresponding values of it's variables.
     * 
     * @param connection database Connection.
     * @param name Expense name.
     * @return EntityExpense object bulit from the values of corresponding 
     * database record.
     */
    public EntityExpense executeSelectByName(Connection connection,
            String name);

    /**
     * Selects record of Expense from database by ID and returns EntityExpense
     * object with the corresponding values of it's variables.
     * 
     * @param connection database Connection.
     * @param id database Expense ID.
     * @return EntityExpense object bulit from the values of corresponding 
     * database record.
     */
    public EntityExpense executeSelectById(Connection connection, Integer id);

    /**
     * Selects all Expense types from the database and returns values of types
     * mapped to the Expense database IDs.
     * 
     * @param connection database Connection.
     * @return values of Expense types mapped to the Expense database IDs.
     */
    public HashMap<Integer, String> executeSelectAllTypes(Connection connection);

    /**
     * Selects all records of Expenses from the database and returns pairs of 
     * values mapped to the database column names which are mapped to the ID
     * of each Expense.
     * 
     * @param connection database Connection.
     * @return pairs of values mapped to the database column names which are 
     * mapped to the ID of each Expense.
     */
    public HashMap<Integer, HashMap<String, Double>>
            executeSelectAllValues(Connection connection);

    /**
     * Selects all records of Expenses from the database and returns pairs of 
     * links (to Accounts and Complex Expenses) mapped to the database column 
     * names which are mapped to the ID of each Expense.
     * 
     * @param connection database Connection.
     * @return pairs of links (to Accounts and Complex Expenses) mapped to the 
     * database column names which are mapped to the ID of each Expense.
     */        
    public HashMap<Integer, HashMap<String, Integer>>
            executeSelectAllLinks(Connection connection);
            
    /**
     * Selects record ID of Expense from database by Expense Name. 
     * 
     * @param connection database Connection.
     * @param name Expense name.
     * @return database Expense ID.
     */      
    public Integer executeSelectIdByName (Connection connection, String name);
}
