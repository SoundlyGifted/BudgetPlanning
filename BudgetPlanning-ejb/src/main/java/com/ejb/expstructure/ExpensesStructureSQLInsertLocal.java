
package com.ejb.expstructure;

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
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean execute(Connection connection, String type, String name, 
            String accountId, String price, String safetyStockPcs, 
            String orderQtyPcs);
}
