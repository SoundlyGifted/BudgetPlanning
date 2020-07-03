
package ejb.expensesStructure;

import ejb.accountsStructure.AccountsStructureSQLLocal;
import ejb.common.SQLAbstract;
import ejb.entity.EntityAccount;
import ejb.entity.EntityExpense;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class ExpensesStructureSQLUpdate extends SQLAbstract
        implements ExpensesStructureSQLUpdateLocal {

    @EJB
    private ExpensesStructureHandlerLocal handler;

    @EJB
    private ExpensesStructureSQLSelectLocal select;

    @EJB
    private AccountsStructureSQLLocal accountsSQL;  
    
    @Override
    public boolean execute(Connection connection, String name, String newName, 
            String accountId, String linkedToComplexId, String price, 
            String safetyStockPcs, String orderQtyPcs) {
        /* If no expense name provided for the update operation then cancelling
        this method. Also checking lengths of String variables. */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)) {
            return false;
        }
        
        /* Checking accountId. */
        Integer accountIdInt = stringToInt(accountId);
        if (accountIdInt == null) {
            accountIdInt = 0;
        }
        EntityAccount accountSelected = accountsSQL.executeSelectById(connection, accountIdInt);
        String accountName = accountSelected.getName();

        /* entityExpense selected from database for the update operation. */
        EntityExpense entityExpenseFromDB
                = select.executeSelectByName(connection, name);
        if (entityExpenseFromDB == null) {
            return false;
        } else {
            /* If Link to Complex Expense already set and Link to Account 
            operation requested then return false 
            (unable to handle such request).*/
            if (entityExpenseFromDB.getLinkedToComplexId() != 0) {
                if (accountIdInt != 0) {
                    return false;
                }
            }
        }

        /* For SIMPLE_EXPENSES and COMPLEX_EXPENSES the following fields 
        should not be filled: price, safetyStockPcs, orderQtyPcs*/
        if (entityExpenseFromDB.getType()
                .equals(ExpensesTypes.ExpenseType.COMPLEX_EXPENSES.getType())
                || entityExpenseFromDB.getType()
                        .equals(ExpensesTypes.ExpenseType.SIMPLE_EXPENSES
                                .getType())) {
            if ((price != null && !price.trim().isEmpty()) 
                    || (safetyStockPcs != null && !safetyStockPcs.trim().isEmpty()) 
                    || (orderQtyPcs != null && !orderQtyPcs.trim().isEmpty())) {
                return false;
            }
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "expensesStructure/update");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLUpdate: "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        /* Processing assigning to the Complex Expense. */
        int linkedToComplexIdInt;
        if (inputCheckNullBlank(linkedToComplexId)) {
            linkedToComplexIdInt = stringToInt(linkedToComplexId);
            if (entityExpenseFromDB.getId() == stringToInt(linkedToComplexId)) {
                /* Complex expense Category cannot be linked to itself. */
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
           leaving the same value as before the update operation. 
           2) If all input parameters not entered (are null or blank) then
           cancelling update operation (nothing to update). */
        boolean allInputParamsNullOrBlank = true;
        String[] enteredParams = new String[]{newName, accountId, accountName,
            linkedToComplexId, price, safetyStockPcs, orderQtyPcs};
        String[] entityExpenseParams = new String[]{
            entityExpenseFromDB.getName(),
            Integer.toString(entityExpenseFromDB.getAccountId()),
            entityExpenseFromDB.getAccountLinked(),
            Integer.toString(entityExpenseFromDB.getLinkedToComplexId()),
            Double.toString(entityExpenseFromDB.getPrice()),
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
            return false;
        }

        newName = enteredParams[0];
        accountId = enteredParams[1];
        accountName = enteredParams[2];
        linkedToComplexId = enteredParams[3];
        price = enteredParams[4];
        safetyStockPcs = enteredParams[5];
        orderQtyPcs = enteredParams[6];

        accountIdInt = stringToInt(accountId);
        linkedToComplexIdInt = stringToInt(linkedToComplexId);
        double priceDouble = stringToDouble(price);
        double safetyStockPcsDouble = stringToDouble(safetyStockPcs);
        double safetyStockCurDouble;
        double orderQtyPcsDouble = stringToDouble(orderQtyPcs);
        double orderQtyCurDouble;

        safetyStockCurDouble = round(priceDouble * safetyStockPcsDouble, 2);
        orderQtyCurDouble = round(priceDouble * orderQtyPcsDouble, 2);
        
        try {
            /* Updating Entity in the Entity Object List.
            entityExpense selected from Entity Object List for the update 
            operation. */
            EntityExpense entityExpense
                    = handler
                            .selectFromEntityExpenseListById(entityExpenseFromDB
                                    .getId());
            if (entityExpense != null) {
                entityExpense.setName(newName);
                entityExpense.setAccountId(accountIdInt);
                entityExpense.setAccountLinked(accountName);
                entityExpense.setLinkedToComplexId(linkedToComplexIdInt);
                entityExpense.setPrice(priceDouble);
                entityExpense.setSafetyStockPcs(safetyStockPcsDouble);
                entityExpense.setSafetyStockCur(safetyStockCurDouble);
                entityExpense.setOrderQtyPcs(orderQtyPcsDouble);
                entityExpense.setOrderQtyCur(orderQtyCurDouble);
            } else {
                return false;
            }

            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, accountIdInt);
            preparedStatement.setString(3, accountName);
            preparedStatement.setInt(4, linkedToComplexIdInt);
            preparedStatement.setDouble(5, priceDouble);
            preparedStatement.setDouble(6, safetyStockPcsDouble);
            preparedStatement.setDouble(7, safetyStockCurDouble);
            preparedStatement.setDouble(8, orderQtyPcsDouble);
            preparedStatement.setDouble(9, orderQtyCurDouble);
            preparedStatement.setString(10, name);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLUpdate: Error while "
                    + "setting query parameters or executing Update Query: "
                    + ex.getMessage() + "***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }

    @Override
    public boolean clearAssignmentToComplexExpense(Connection connection, 
            String name) {
        /* If no expense name provided for the update operation then cancelling
        this method. */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)) {
            return false;
        }

        /* Check if corresponding record is in the database. */
        if (select.executeSelectByName(connection, name) == null) {
            return false;
        }

        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "expensesStructure/update.clearComplexExpLink");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLUpdate: "
                    + "clearAssignmentToComplexExpense() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            EntityExpense entity = 
                    handler.selectFromEntityExpenseListByName(name);
            if (entity != null) {
                entity.setLinkedToComplexId(0);
            } else {
                return false;
            }
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLUpdate: "
                    + "clearAssignmentToComplexExpense() Error while "
                    + "setting query parameters or executing Update Query: "
                    + ex.getMessage() + "***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }
}
