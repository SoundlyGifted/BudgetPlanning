
package ejb.calculation;

import java.sql.Connection;
import java.util.ArrayList;
import javax.ejb.Local;

/**
 * EJB ExpensesHandler Local interface contains methods to perform 
 * operations on EntityExpense objects in the EntityExpenseList collection.
 */
@Local
public interface ExpensesHandlerLocal {
    
    /**
     * Gets collection of EntityExpense elements (EntityExpenseList).
     * 
     * @return EntityExpenseList collection.
     */
    public ArrayList<EntityExpense> getEntityExpenseList();
     
    /**
     * Method to remove an EntityExpense element from the EntityExpenseList
     * collection.
     * 
     * @param entity EntityExpense element to be removed.
     */
    public void removeFromEntityExpenseList(EntityExpense entity);
    
    /**
     * Prepares EntityExpense object (calculates values of it's parameters 
     * based on the current data in the database) based on the given 
     * database Expense ID.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @param id database Expense ID.
     * @return prepared EntityExpense object (with calculated parameters).
     */
    public EntityExpense prepareEntityExpenseById(Connection connection, 
            String inputPlanningPeriodsFrequency, Integer id);
    
    /**
     * Actualizes EntityExpenseList (replaces it with the list obtained from
     * the database).
     * 
     * @param connection database Connection.
     * @return EntityExpenseList obtained based on the database records.
     */
    public ArrayList<EntityExpense> actualizeEntityExpenseList(Connection 
            connection);
    
    /**
     * Method is applied to operation of shifting of Current Planning Period
     * towards increasing of period date on one period and re-calculates
     * "Current Stock (PCS)" attributes of all Expenses with type = 'GOODS' 
     * currently present in the database based on domain specific formula.
     * 
     * @param connection database Connection.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean 
        calculateAllCurrentStockPcsForNextPeriod(Connection connection);
    
    /**
     * Method is applied to operation of shifting of Current Planning Period
     * towards decreasing of period date on one period and re-calculates
     * "Current Stock (PCS)" attributes of all Expenses with type = 'GOODS' 
     * currently present in the database based on domain specific formula.
     * 
     * @param connection database Connection.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean 
        calculateAllCurrentStockPcsForPreviousPeriod(Connection connection);        
}
