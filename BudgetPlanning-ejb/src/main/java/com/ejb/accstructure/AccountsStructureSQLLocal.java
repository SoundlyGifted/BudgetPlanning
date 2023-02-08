
package com.ejb.accstructure;

import com.ejb.calculation.EntityAccount;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import jakarta.ejb.Local;

/**
 * EJB AccountsStructureSQL Local interface contains methods to perform 
 * operations on Account records in the database.
 */
@Local
public interface AccountsStructureSQLLocal {
    
    /**
     * Inserts record of Account into the database.
     * 
     * @param connection database Connection.
     * @param name Account name.
     * @param currentRemainder current remainder of the Account in Currency.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void executeInsert(Connection connection, String name, 
            String currentRemainder) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Updates record of Account in the database
     * 
     * @param connection database Connection.
     * @param idForUpdate database Account ID.
     * @param name Account name.
     * @param currentRemainder current remainder of the Account in Currency.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void executeUpdate(Connection connection, String idForUpdate, 
            String name, String currentRemainder) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Deletes record of Account from the database.
     * 
     * @param connection database Connection.
     * @param id database Account ID.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void executeDelete(Connection connection, String id) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Selects all records of Accounts from the database and retruns list of 
     * EntityAccount objects with variable values from the corresponding 
     * database records.
     * 
     * @param connection database Connection.
     * @return ArrayList of EntityAccount objects with variable values from the
     * corresponding database records.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public ArrayList<EntityAccount> executeSelectAll(Connection connection) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Selects record of Account from database by Name and returns EntityAccount
     * object with the corresponding values of it's variables.
     * 
     * @param connection database Connection.
     * @param name Account name.
     * @return EntityAccount object bulit from the values of corresponding 
     * database record.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public EntityAccount executeSelectByName(Connection connection, 
            String name) throws GenericDBOperationException, GenericDBException;
    
    /**
     * Selects record of Account from database by ID and returns EntityAccount
     * object with the corresponding values of it's variables.
     * 
     * @param connection database Connection.
     * @param id database Account ID.
     * @return EntityAccount object bulit from the values of corresponding 
     * database record.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public EntityAccount executeSelectById(Connection connection, Integer id) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Selects all records of Accounts from the database and returns pairs of 
     * values mapped to the database column names which are mapped to the ID
     * of each Account.
     * 
     * @param connection database Connection.
     * @return pairs of values mapped to the database column names which are 
     * mapped to the ID of each Account.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public HashMap<Integer, HashMap<String, Double>> 
        executeSelectAllValues(Connection connection) 
                throws GenericDBOperationException;
        
    /**
     * Updates the value of current remainder (in Currency) of the Account in
     * the database with the new value specified.
     * 
     * @param connection database Connection.
     * @param id database Account ID.
     * @param newCurrentRemainderCur new value of current remainder of the 
     * Account in Currency.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */    
    public void updateCurrentRemainderById(Connection connection, Integer id, 
            Double newCurrentRemainderCur) 
            throws GenericDBOperationException, GenericDBException;    
}
