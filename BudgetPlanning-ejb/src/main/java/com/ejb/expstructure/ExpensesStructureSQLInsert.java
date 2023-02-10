
package com.ejb.expstructure;

import com.ejb.accstructure.AccountsStructureSQLLocal;
import com.ejb.actualexpenses.ActualExpensesSQLLocal;
import com.ejb.common.SQLAbstract;
import com.ejb.calculation.EntityAccount;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import com.ejb.expstructure.ExpensesTypes.ExpenseType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.ejb.Stateless;
import jakarta.ejb.EJB;

/**
 * EJB ExpensesStructureSQLInsert is used to perform insert operations of 
 * Expenses records in the database.
 */
@Stateless
public class ExpensesStructureSQLInsert extends SQLAbstract
        implements ExpensesStructureSQLInsertLocal {
       
    @EJB
    private ExpensesStructureSQLSelectLocal select;
    
    @EJB
    private AccountsStructureSQLLocal accountsSQL;
    
    @EJB
    private ActualExpensesSQLLocal actualExpensesSQL;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Connection connection, String type, String name, 
            String accountId, String price, String safetyStockPcs, 
            String orderQtyPcs) 
            throws GenericDBOperationException, GenericDBException {
        /* Checking of input values. */
        if (!inputCheckType(type) || !inputCheckNullBlank(name)
                || !inputCheckLength(name)
                || stringToDouble(price) == null
                || stringToDouble(safetyStockPcs) == null
                || stringToDouble(orderQtyPcs) == null) {
            throw new GenericDBOperationException("Wrong Expense type or "
                    + "Expense name provided.");
        }
       
        try (PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "expensesStructure/insert")) {
            
            /* For SIMPLE_EXPENSES and COMPLEX_EXPENSES the following fields 
             * should not be filled: price, safetyStock, orderQty
             */
            if (type.equals(COMPLEX_EXPENSES_SUPPORTED_TYPE)
                    || type.equals(SIMPLE_EXPENSES_SUPPORTED_TYPE)) {
                price = "";
                safetyStockPcs = "";
                orderQtyPcs = "";
            }

            // Checking accountId.
            Integer accountIdInt = stringToInt(accountId);
            if (accountIdInt == null) {
                accountIdInt = 0;
            }

            EntityAccount accountSelected
                    = accountsSQL.executeSelectById(connection, accountIdInt);
            String accountName = accountSelected.getName();

            double priceDouble;
            double safetyStockPcsDouble;
            double safetyStockCurDouble;
            double orderQtyPcsDouble;
            double orderQtyCurDouble;

            if (price == null || price.trim().isEmpty()) {
                priceDouble = (double) 0;
            } else {
                priceDouble = stringToDouble(price);
            }

            if (safetyStockPcs == null || safetyStockPcs.trim().isEmpty()) {
                safetyStockPcsDouble = (double) 0;
            } else {
                safetyStockPcsDouble = stringToDouble(safetyStockPcs);
            }

            if (orderQtyPcs == null || orderQtyPcs.trim().isEmpty()) {
                orderQtyPcsDouble = (double) 0;
            } else {
                orderQtyPcsDouble = stringToDouble(orderQtyPcs);
            }

            safetyStockCurDouble = round(priceDouble * safetyStockPcsDouble, 2);
            orderQtyCurDouble = round(priceDouble * orderQtyPcsDouble, 2);      

            // Setting Query Parameters and executing Query;
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, accountIdInt);
            preparedStatement.setString(4, accountName);
            preparedStatement.setInt(5, 0); /* LINKED_TO_COMPLEX_ID is 0 for 
                                               new records. */
            preparedStatement.setDouble(6, priceDouble);
            preparedStatement.setDouble(7, safetyStockPcsDouble);
            preparedStatement.setDouble(8, safetyStockCurDouble);
            preparedStatement.setDouble(9, orderQtyPcsDouble);
            preparedStatement.setDouble(10, orderQtyCurDouble);
            /* Current Stock parameters are zero by default for new Expense 
             * Categories. 
             */
            preparedStatement.setDouble(11, 0); 
            preparedStatement.setDouble(12, 0);
            preparedStatement.setDouble(13, 0);
            preparedStatement.setDouble(14, 0);
            preparedStatement.executeUpdate();
            
            /* Executing update of Expense ID in the Actual Expenses database
             * table where it was previously set to "-1" (Expense removed
             * status).
             * If the given name is the same as the name of Expense with 
             * ID = -1 then assigning the proper ID value.
             */
            Integer expenseId = select.executeSelectIdByName(connection, name);
            if (expenseId != null) {
                actualExpensesSQL.recoverDeletedExpenseId(connection, 
                        expenseId, name);
            }
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
    }
}
