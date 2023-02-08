
package com.ejb.expstructure;

import com.ejb.accstructure.AccountsStructureSQLLocal;
import com.ejb.actualexpenses.ActualExpensesSQLLocal;
import com.ejb.common.SQLAbstract;
import com.ejb.calculation.EntityAccount;
import com.ejb.calculation.EntityExpense;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

/**
 * EJB ExpensesStructureSQLInsert is used to perform update operations on 
 * Expenses records in the database.
 */
@Stateless
public class ExpensesStructureSQLUpdate extends SQLAbstract
        implements ExpensesStructureSQLUpdateLocal {

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
    public void execute(Connection connection, String name, String newName, 
            String accountId, String linkedToComplexId, String price,
            String currentStockPcs, String safetyStockPcs, String orderQtyPcs) 
            throws GenericDBOperationException, GenericDBException {
        /* If no expense name provided for the update operation then cancelling
         * this method. Also checking lengths of String variables. 
         */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)) {
            throw new GenericDBOperationException("Empty current Expense name "
                    + "provided, or the length exceeds the limit.");
        }
        
        // Checking accountId.
        Integer accountIdInt = stringToInt(accountId);
        if (accountIdInt == null) {
            accountIdInt = 0;
        }
        EntityAccount accountSelected = accountsSQL
                .executeSelectById(connection, accountIdInt);
        String accountName = accountSelected.getName();

        // Expense selected from database for the update operation.
        EntityExpense entityExpenseFromDB
                = select.executeSelectByName(connection, name);
        if (entityExpenseFromDB == null) {
            throw new GenericDBOperationException("Unable to find Expense '" 
                    + name + "' in the database Expenses structure table.");
        }
        
        /* If Link to Complex Expense already set and Link to Account 
         * operation requested then return false 
         * (unable to handle such request).
         */
        int idOfComplexExpense = entityExpenseFromDB.getLinkedToComplexId();
        if (idOfComplexExpense != 0 && accountIdInt != 0) {
            String NameOfComplexExpense
                    = select.executeSelectById(connection, accountIdInt)
                            .getName();
            throw new GenericDBOperationException("Unable to set "
                    + "Account for the '" + name + "' Expense. "
                    + "This Expense is a part of a Complex "
                    + "Expense '" + NameOfComplexExpense + "'.");
        }


        /* For SIMPLE_EXPENSES and COMPLEX_EXPENSES the following fields 
         * should not be filled: price, safetyStockPcs, orderQtyPcs
         */
        String entityExpenseFromDBType = entityExpenseFromDB.getType();
        if (entityExpenseFromDBType
                .equals(ExpensesTypes.ExpenseType.COMPLEX_EXPENSES.getType())
                || entityExpenseFromDBType
                        .equals(ExpensesTypes.ExpenseType.SIMPLE_EXPENSES
                                .getType())) {
            if ((price != null && !price.trim().isEmpty()) 
                    || (safetyStockPcs != null && !safetyStockPcs.trim()
                            .isEmpty()) 
                    || (orderQtyPcs != null && !orderQtyPcs.trim().isEmpty())) {
                throw new GenericDBOperationException("Unable to set "
                        + "Price/Safety Stock/Order QTY for the '" + name
                        + "' Expense of type '" + entityExpenseFromDBType + "'. "
                        + "These parameters are not applicable for Simple or "
                        + "Complex Expense types.");
            }
        }
        
        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, "expensesStructure/update");
        PreparedStatement psUpdateActualExpensesName
                = createPreparedStatement(connection,
                        "actualExpenses/update.actualExpensesName");

        // Processing assigning to the Complex Expense.
        int linkedToComplexIdInt;
        if (inputCheckNullBlank(linkedToComplexId)) {
            linkedToComplexIdInt = stringToInt(linkedToComplexId);
            if (entityExpenseFromDB.getId() == stringToInt(linkedToComplexId)) {
                // Complex expense Category cannot be linked to itself.
                linkedToComplexId = "";
            } else {
                if (linkedToComplexIdInt != 0) {
                    accountId = "0";
                    accountName = "NOT SET";
                    entityExpenseFromDB.setAccountLinked("");                    
                }
            }
        }

        /* 1) If some input parameter not entered (is null or blank) then 
         * leaving the same value as before the update operation. 
         * 2) If all input parameters not entered (are null or blank) then
         * cancelling update operation (nothing to update). 
         */
        boolean allInputParamsNullOrBlank = true;
        
        String[] enteredParams = new String[] {newName, accountId, accountName,
            linkedToComplexId, price, currentStockPcs, safetyStockPcs, 
            orderQtyPcs};
        
        String[] entityExpenseParams = new String[]{
            entityExpenseFromDB.getName(),
            Integer.toString(entityExpenseFromDB.getAccountId()),
            entityExpenseFromDB.getAccountLinked(),
            Integer.toString(entityExpenseFromDB.getLinkedToComplexId()),
            Double.toString(entityExpenseFromDB.getPrice()),
            Double.toString(entityExpenseFromDB.getCurrentStockPcs()),
            Double.toString(entityExpenseFromDB.getSafetyStockPcs()),
            Double.toString(entityExpenseFromDB.getOrderQtyPcs())};
        
        for (int i = 0; i < enteredParams.length; i++) {
            if (enteredParams[i] == null || enteredParams[i].trim().isEmpty()) {
                enteredParams[i] = entityExpenseParams[i];
            } else {
                allInputParamsNullOrBlank = false;
            }
        }
        if (allInputParamsNullOrBlank) {
            throw new GenericDBOperationException("Unable to handle the update "
                    + "operation, no parameters were entered.");
        }

        newName = enteredParams[0];
        accountId = enteredParams[1];
        accountName = enteredParams[2];
        linkedToComplexId = enteredParams[3];
        price = enteredParams[4];
        currentStockPcs = enteredParams[5];
        safetyStockPcs = enteredParams[6];
        orderQtyPcs = enteredParams[7];

        accountIdInt = stringToInt(accountId);
        linkedToComplexIdInt = stringToInt(linkedToComplexId);
        double priceDouble = stringToDouble(price);
        double currentStockPcsDouble = stringToDouble(currentStockPcs);
        double currentStockCurDouble;
        double currentStockWscPcsDouble;
        double currentStockWscCurDouble;
        double safetyStockPcsDouble = stringToDouble(safetyStockPcs);
        double safetyStockCurDouble;
        double orderQtyPcsDouble = stringToDouble(orderQtyPcs);
        double orderQtyCurDouble;

        currentStockCurDouble = round(priceDouble * currentStockPcsDouble, 2);
        safetyStockCurDouble = round(priceDouble * safetyStockPcsDouble, 2);
        orderQtyCurDouble = round(priceDouble * orderQtyPcsDouble, 2);
        
        currentStockWscPcsDouble = currentStockPcsDouble - safetyStockPcsDouble;
        currentStockWscCurDouble = round(priceDouble * 
                currentStockWscPcsDouble, 2);

        Integer expenseId = select.executeSelectIdByName(connection, name);
        
        try {
            // Main update statement execution.
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, accountIdInt);
            preparedStatement.setString(3, accountName);
            preparedStatement.setInt(4, linkedToComplexIdInt);
            preparedStatement.setDouble(5, priceDouble);
            preparedStatement.setDouble(6, currentStockPcsDouble);
            preparedStatement.setDouble(7, currentStockCurDouble);
            preparedStatement.setDouble(8, currentStockWscPcsDouble);
            preparedStatement.setDouble(9, currentStockWscCurDouble);
            preparedStatement.setDouble(10, safetyStockPcsDouble);
            preparedStatement.setDouble(11, safetyStockCurDouble);
            preparedStatement.setDouble(12, orderQtyPcsDouble);
            preparedStatement.setDouble(13, orderQtyCurDouble);
            preparedStatement.setString(14, name);
            preparedStatement.executeUpdate();
            
            // Executing update of Expense ID where it was previously set to 
            // "-1" (Expense removed status).
            // If the given name is the same as the name of Expense with 
            // ID = -1 then assigning the proper ID value.
            if (expenseId != null) {
                actualExpensesSQL.recoverDeletedExpenseId(connection, 
                        expenseId, newName);
            }

            // Executing update of Expense Name in the Actual Expenses database
            // table.
            psUpdateActualExpensesName.setString(1, newName);
            psUpdateActualExpensesName.setString(2, name);
            psUpdateActualExpensesName.executeUpdate();
            
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
            clear(psUpdateActualExpensesName);
        }
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void clearAssignmentToComplexExpense(Connection connection, 
            String name) throws GenericDBOperationException, GenericDBException {
        /* If no expense name provided for the update operation then cancelling
         * this method. 
         */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)) {
            throw new GenericDBOperationException("Empty current Expense name "
                    + "provided, or the length exceeds the limit.");
        }

        // Check if corresponding record is in the database.
        if (select.executeSelectByName(connection, name) == null) {
            throw new GenericDBOperationException("Unable to find Expense '" 
                    + name + "' in the database Expenses structure table.");
        }

        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                    "expensesStructure/update.clearComplexExpLink");

        try {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateCurrentStockById(Connection connection, Integer id, 
            Double newCurrentStockPcs) 
            throws GenericDBOperationException, GenericDBException {
        if (id == null || id <= 0 || newCurrentStockPcs == null) {
            throw new GenericDBOperationException("Empty or wrong current "
                    + "Expense id provided, or new Stock value is null.");
        }

        HashMap<Integer, HashMap<String, Double>> allValues = 
                select.executeSelectAllValues(connection);
        Double price = allValues.get(id).get("PRICE");
        Double safetyStock = allValues.get(id).get("SAFETY_STOCK_PCS");
        Double newCurrentStockCur = round(newCurrentStockPcs * price, 2);
        Double newCurrentStockWscPcs = newCurrentStockPcs - safetyStock;
        Double newCurrentStockWscCur = round(newCurrentStockWscPcs * price, 2);

        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                    "expensesStructure/update.currentStock.byid");

        try {
            preparedStatement.setDouble(1, newCurrentStockPcs);
            preparedStatement.setDouble(2, newCurrentStockCur);
            preparedStatement.setDouble(3, newCurrentStockWscPcs);
            preparedStatement.setDouble(4, newCurrentStockWscCur);
            preparedStatement.setInt(5, id);

            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
        }
    }
}
