
package com.ejb.expstructure;

import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
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
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void executeDeleteById(Connection connection, String id) 
            throws GenericDBException, GenericDBOperationException;
}
