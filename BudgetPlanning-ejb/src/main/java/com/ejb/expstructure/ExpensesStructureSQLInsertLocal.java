
package com.ejb.expstructure;

import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import jakarta.ejb.Local;

/**
 * EJB ExpensesStructureSQLInsert Local interface contains methods to perform
 * insert operations of Expenses records in the database.
 */
@Local
public interface ExpensesStructureSQLInsertLocal {
    
    /**
     * Inserts record of Expense into the database.
     * 
     * @param connection database Connection.
     * @param type Expense type.
     * @param name Expense name.
     * @param accountId Expense linked Account database ID.
     * @param price Expense planning price (price for planning purpose).
     * @param safetyStockPcs Expense Safety Stock (PCS) attribute value.
     * @param orderQtyPcs Expense Order Quantity (PCS) attribute value.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void execute(Connection connection, String type, String name, 
            String accountId, String price, String safetyStockPcs, 
            String orderQtyPcs) 
            throws GenericDBOperationException, GenericDBException;
}
