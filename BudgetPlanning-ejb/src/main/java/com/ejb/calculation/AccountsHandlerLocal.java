
package com.ejb.calculation;

import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.util.ArrayList;
import jakarta.ejb.Local;

/**
 * EJB AccountsHandler Local interface contains methods to perform 
 * operations on EntityAccount objects in the EntityAccountList collection.
 */
@Local
public interface AccountsHandlerLocal {

    /**
     * Removes EntityAccount element from the EntityAccountList collection.
     * 
     * @param entity EntityAccount element to be removed.
     */
    public void removeFromEntityAccountList(EntityAccount entity);

    /**
     * Gets collection of EntityAccount elements (EntityAccountList).
     * 
     * @return EntityAccountList collection.
     */
    public ArrayList<EntityAccount> getEntityAccountList();

    /**
     * Actualizes EntityAccountList (replaces it with the list obtained from
     * the database).
     * 
     * @param connection database Connection.
     * @return EntityAccountList obtained based on the database records.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public ArrayList<EntityAccount> 
        actualizeEntityAccountList(Connection connection) 
                throws GenericDBOperationException, GenericDBException;

    /**
     * Prepares the corresponding EntityAccount object (calculates values of 
     * it's parameters based on the current data in the database) based on the
     * given database Expense ID (with check of it's link to a certain Account).
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @param inputExpenseId given database Expense ID to check for the linked
     * Account and making corresponding EntityAccount object preparation.
     * @return prepared EntityAccount object (with calculated parameters) if
     * Expense with given ID has any linked Account, or null otherwise.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public EntityAccount prepareEntityAccountByExpenseId(Connection connection,
            String inputPlanningPeriodsFrequency, Integer inputExpenseId) 
            throws GenericDBOperationException, GenericDBException;

    /**
     * Prepares EntityAccount object (calculates values of it's parameters 
     * based on the current data in the database) based on the given database 
     * Account ID.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @param id database Account ID.
     * @return prepared EntityAccount object (with calculated parameters).
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public EntityAccount prepareEntityAccountById(Connection connection,
            String inputPlanningPeriodsFrequency, Integer id) 
            throws GenericDBOperationException, GenericDBException; 

    /**
     * Method is applied to operation of shifting of Current Planning Period
     * towards increasing of period date on one period and re-calculates
     * "Current Remainder (CUR)" attributes of all Accounts currently present
     * in the database based on domain specific formula.
     * 
     * @param connection database Connection.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void calculateAllCurrentRemainderCurForNextPeriod(Connection 
            connection) throws GenericDBOperationException, GenericDBException;
    
    /**
     * Method is applied to operation of shifting of Current Planning Period
     * towards decreasing of period date on one period and re-calculates
     * "Current Remainder (CUR)" attributes of all Accounts currently present
     * in the database based on domain specific formula.
     * 
     * @param connection database Connection.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void calculateAllCurrentRemainderCurForPreviousPeriod(Connection connection) 
            throws GenericDBOperationException, GenericDBException;
}
