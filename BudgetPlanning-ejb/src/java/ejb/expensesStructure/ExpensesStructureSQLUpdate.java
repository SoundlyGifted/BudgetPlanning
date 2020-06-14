
package ejb.expensesStructure;

import ejb.common.SQLAbstract;
import ejb.entity.EntityExpense;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private PreparedStatement preparedStatement;

    @Override
    public boolean execute(Connection connection, String name, String newName, 
            String accountName, String linkedComplExpName, String price, 
            String safetyStock, String orderQty) {
        /* If no expense name provided for the update operation then cancelling
        this method. Also checking lengths of String variables. */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)
                || !inputCheckLength(accountName)) {
            return false;
        }
        /* entityExpense selected from database for the update operation. */
        EntityExpense entityExpenseFromDB
                = select.executeSelectByName(connection, name);
        if (entityExpenseFromDB == null) {
            return false;
        }
        /* For SIMPLE_EXPENSES and COMPLEX_EXPENSES the following fields 
        should not be filled: price, safetyStock, orderQty*/
        if (entityExpenseFromDB.getType()
                .equals(ExpensesTypes.ExpenseType.COMPLEX_EXPENSES.getType())
                || entityExpenseFromDB.getType()
                        .equals(ExpensesTypes.ExpenseType.SIMPLE_EXPENSES
                                .getType())) {
            if ((price != null && !price.trim().isEmpty()) 
                    || (safetyStock != null && !safetyStock.trim().isEmpty()) 
                    || (orderQty != null && !orderQty.trim().isEmpty())) {
                return false;
            }
        }

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
        String linkedToComplexId = getComplexExpenseId(connection, linkedComplExpName);
        if (linkedToComplexId != null) {
            if (entityExpenseFromDB.getId() == stringToInt(linkedToComplexId)) {
                /* Complex expense Category cannot be linked to itself. */
                linkedToComplexId = "";
            } else {
                /* If linked to Complex Expense the account assignment 
                should be removed. */
                accountName = "";
                entityExpenseFromDB.setAccountLinked("");
            }
        }

        /* 1) If some input parameter not entered (is null or blank) then 
           leaving the same value as before the update operation. 
           2) If all input parameters not entered (are null or blank) then
           cancelling update operation (nothing to update). */
        boolean allInputParamsNullOrBlank = true;
        String[] enteredParams = new String[]{newName, accountName,
            linkedToComplexId, price, safetyStock, orderQty};
        String[] entityExpenseParams = new String[]{
            entityExpenseFromDB.getName(),
            entityExpenseFromDB.getAccountLinked(),
            Integer.toString(entityExpenseFromDB.getLinkedToComplexId()),
            Integer.toString(entityExpenseFromDB.getPrice()),
            Integer.toString(entityExpenseFromDB.getSafetyStock()),
            Integer.toString(entityExpenseFromDB.getOrderQty())};
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
        accountName = enteredParams[1];
        linkedToComplexId = enteredParams[2];
        price = enteredParams[3];
        safetyStock = enteredParams[4];
        orderQty = enteredParams[5];

        int linkedToComplexIdInt = stringToInt(linkedToComplexId);
        int priceInt = stringToInt(price);
        int safetyStockInt = stringToInt(safetyStock);
        int orderQtyInt = stringToInt(orderQty);

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
                entityExpense.setAccountLinked(accountName);
                entityExpense.setLinkedToComplexId(linkedToComplexIdInt);
                entityExpense.setPrice(priceInt);
                entityExpense.setSafetyStock(safetyStockInt);
                entityExpense.setOrderQty(orderQtyInt);
            } else {
                return false;
            }

            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, accountName);
            preparedStatement.setInt(3, linkedToComplexIdInt);
            preparedStatement.setInt(4, priceInt);
            preparedStatement.setInt(5, safetyStockInt);
            preparedStatement.setInt(6, orderQtyInt);
            preparedStatement.setString(7, name);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLUpdate: Error while "
                    + "setting query parameters or executing Update Query: "
                    + ex.getMessage() + "***");
            return false;
        } finally {
            clear();
        }
        return true;
    }

    private String getComplexExpenseId(Connection connection, String complexExpenseName) {
        if (complexExpenseName == null || complexExpenseName.trim().isEmpty()) {
            return null;
        }
        EntityExpense entityDB
                = select.executeSelectByName(connection, 
                        complexExpenseName);
        if (entityDB == null || 
                !entityDB.getType().equals("COMPLEX_EXPENSES")) {
            return null;
        }
        return Integer.toString(entityDB.getId());
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
            clear();
        }
        return true;
    }
    
    @Override
    public boolean clearAssignmentToAccount(Connection connection, String name) {
        /* If no expense name provided for the update operation then cancelling
        this method. */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)) {
            return false;
        }

        /* Check if corresponding record is in the database. */
        if (select.executeSelectByName(connection, name) == null) {
            return false;
        }

        try {
            preparedStatement = createPreparedStatement(connection, 
                    "expensesStructure/update.clearAccount");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLUpdate: "
                    + "clearAssignmentToAccount() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            EntityExpense expense = 
                    handler.selectFromEntityExpenseListByName(name);
            if (expense != null) {
                expense.setAccountLinked("");
            } else {
                return false;
            }
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLUpdate: "
                    + "clearAssignmentToAccount() Error while "
                    + "setting query parameters or executing Update Query: "
                    + ex.getMessage() + "***");
            return false;
        } finally {
            clear();
        }
        return true;
    }    
  
    private void clear() {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                preparedStatement = null;
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
