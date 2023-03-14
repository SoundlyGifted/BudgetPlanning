
package com.ejb.mainscreen;

import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.util.Map;
import java.util.TreeMap;
import jakarta.ejb.Local;

/**
 * EJB PlannedAccountsValuesSQL Local interface contains methods to perform 
 * operations on planned and calculated parameters data of the Accounts in the 
 * database.
 */
@Local
public interface PlannedAccountsValuesSQLLocal {

    /**
     * Updates Account's planned parameter data in the database.
     * 
     * @param connection database Connection.
     * @param accountId database Account ID.
     * @param paramName name of the planned parameter of the Account.
     * @param updatedValues new values of the planned parameter mapped to the 
     * planning Periods dates.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void executeUpdate(Connection connection, String accountId,
            String paramName, Map<String, String> updatedValues) 
            throws GenericDBOperationException, GenericDBException;

    /**
     * Selects and returns planned parameter values of a given Account mapped 
     * to the planning Periods dates from database.
     * 
     * @param connection database Connection.
     * @param id database Account ID.
     * @param paramName name of the planned parameter of the Account.
     * @return planned parameter values of a given Account mapped to the 
     * planning Periods dates from database.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public TreeMap<String, Double> selectPlannedAccountsValuesById(Connection 
            connection, Integer id, String paramName) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Updates planned and calculated parameters of all the Accounts in the 
     * database based on values from Account calculation class objects 
     * contained in the list that is returned by 
     * {@link ejb.calculation.AccountsHandlerLocal#getEntityAccountList()}
     * 
     * @param connection database Connection
     * @param inputPlanningPeriodsFrequency planning Periods Frequency.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void executeUpdateAll(Connection connection,
            String inputPlanningPeriodsFrequency) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Sets current Period in the database table that contains Accounts plan.
     * 
     * @param connection database Connection
     * @param date the date of the new current planning Period in ISO8601 
     * format.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void setCurrentPeriodDate(Connection connection, String date) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Deletes all Account plan (planned and calculated parameter values) from
     * the database for the given Account ID.
     * 
     * @param connection database Connection.
     * @param id database Account ID.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void executeDeleteByAccountId(Connection connection, String id) 
            throws GenericDBOperationException, GenericDBException;
}
