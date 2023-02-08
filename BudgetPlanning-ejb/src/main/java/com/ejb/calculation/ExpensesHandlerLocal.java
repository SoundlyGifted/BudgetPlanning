
package com.ejb.calculation;

import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.util.ArrayList;
import jakarta.ejb.Local;

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
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public EntityExpense prepareEntityExpenseById(Connection connection, 
            String inputPlanningPeriodsFrequency, Integer id) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Actualizes EntityExpenseList (replaces it with the list obtained from
     * the database).
     * 
     * @param connection database Connection.
     * @return EntityExpenseList obtained based on the database records.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public ArrayList<EntityExpense> actualizeEntityExpenseList(Connection 
            connection) throws GenericDBOperationException, GenericDBException;
    
    /**
     * Method is applied to operation of shifting of Current Planning Period
     * towards increasing of period date on one period and re-calculates
     * "Current Stock (PCS)" attributes of all Expenses with type = 'GOODS' 
     * currently present in the database based on domain specific formula.
     * 
     * @param connection database Connection.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void calculateAllCurrentStockPcsForNextPeriod(Connection connection) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Method is applied to operation of shifting of Current Planning Period
     * towards decreasing of period date on one period and re-calculates
     * "Current Stock (PCS)" attributes of all Expenses with type = 'GOODS' 
     * currently present in the database based on domain specific formula.
     * 
     * @param connection database Connection.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void calculateAllCurrentStockPcsForPreviousPeriod(Connection connection) 
            throws GenericDBOperationException, GenericDBException;        
}
