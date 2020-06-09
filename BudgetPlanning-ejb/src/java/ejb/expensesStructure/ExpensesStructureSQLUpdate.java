
package ejb.expensesStructure;

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
public class ExpensesStructureSQLUpdate extends ExpensesStructureSQLAbstract
        implements ExpensesStructureSQLUpdateLocal {

    @EJB
    private ExpensesStructureHandlerLocal handler;

    @EJB
    private ExpensesStructureSQLSelectLocal select;

    private PreparedStatement preparedStatement;

    @Override
    public boolean execute(Connection connection, String name, String newName, 
            String accountName, String linkedComplExpName, String title, 
            String newTitle, String price, String safetyStock, String orderQty, 
            String shopName) {
        /* If no expense name provided for the update operation then cancelling
        this method. Also checking lengths of String variables. */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)
                || !inputCheckLength(accountName) || !inputCheckLength(title)
                || !inputCheckLength(shopName)) {
            return false;
        }
        /* entityExpense selected from database for the update operation. */
        EntityExpense entityExpenseFromDB
                = select.executeSelectByNameAndTitle(connection, name, title);
        if (entityExpenseFromDB == null) {
            return false;
        }
        /* For SIMPLE_EXPENSES and COMPLEX_EXPENSES the following fields 
        should not be filled: title, price, safetyStock, orderQty, shopName*/
        if (entityExpenseFromDB.getType()
                .equals(ExpensesTypes.ExpenseType.COMPLEX_EXPENSES.getType())
                || entityExpenseFromDB.getType()
                        .equals(ExpensesTypes.ExpenseType.SIMPLE_EXPENSES
                                .getType())) {
            if ((title != null && !title.trim().isEmpty()) || (price != null
                    && !price.trim().isEmpty()) || (safetyStock != null
                    && !safetyStock.trim().isEmpty()) || (orderQty != null
                    && !orderQty.trim().isEmpty()) || (shopName != null
                    && !shopName.trim().isEmpty())) {
                return false;
            }
        }

        try {
            preparedStatement = createPreparedStatement(connection, 
                    "update.expense");
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
            linkedToComplexId, newTitle, price, safetyStock, orderQty, shopName};
        String[] entityExpenseParams = new String[]{
            entityExpenseFromDB.getName(),
            entityExpenseFromDB.getAccountLinked(),
            Integer.toString(entityExpenseFromDB.getLinkedToComplexId()),
            entityExpenseFromDB.getTitle(),
            Integer.toString(entityExpenseFromDB.getPrice()),
            Integer.toString(entityExpenseFromDB.getSafetyStock()),
            Integer.toString(entityExpenseFromDB.getOrderQty()),
            entityExpenseFromDB.getShopName()};
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
        newTitle = enteredParams[3];
        price = enteredParams[4];
        safetyStock = enteredParams[5];
        orderQty = enteredParams[6];
        shopName = enteredParams[7];

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
                entityExpense.setTitle(newTitle);
                entityExpense.setPrice(priceInt);
                entityExpense.setSafetyStock(safetyStockInt);
                entityExpense.setOrderQty(orderQtyInt);
                entityExpense.setShopName(shopName);
            } else {
                return false;
            }

            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, accountName);
            preparedStatement.setInt(3, linkedToComplexIdInt);
            preparedStatement.setString(4, newTitle);
            preparedStatement.setInt(5, priceInt);
            preparedStatement.setInt(6, safetyStockInt);
            preparedStatement.setInt(7, orderQtyInt);
            preparedStatement.setString(8, shopName);
            preparedStatement.setString(9, name);
            preparedStatement.setString(10, title);
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
                = select.executeSelectByNameAndTitle(connection, 
                        complexExpenseName, "");
        if (entityDB == null || 
                !entityDB.getType().equals("COMPLEX_EXPENSES")) {
            return null;
        }
        return Integer.toString(entityDB.getId());
    }

    @Override
    public boolean clearAssignmentToComplexExpense(Connection connection, 
            String name, String title) {
        /* If no expense name provided for the update operation then cancelling
        this method. */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)
                || !inputCheckLength(title)) {
            return false;
        }

        /* Check if corresponding record is in the database. */
        if (select.executeSelectByNameAndTitle(connection, name, 
                title) == null) {
            return false;
        }

        try {
            preparedStatement = createPreparedStatement(connection, 
                    "update.expense.clearComplexExpLink");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLUpdate: "
                    + "clearAssignmentToComplexExpense() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            EntityExpense entity = 
                    handler.selectFromEntityExpenseListByNameAndTitle(name, title);
            if (entity != null) {
                entity.setLinkedToComplexId(0);
            } else {
                return false;
            }
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, title);
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
    public boolean clearAssignmentToAccount(Connection connection, String name, 
            String title) {
        /* If no expense name provided for the update operation then cancelling
        this method. */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)
                || !inputCheckLength(title)) {
            return false;
        }

        /* Check if corresponding record is in the database. */
        if (select.executeSelectByNameAndTitle(connection, name, title) == null) {
            return false;
        }

        try {
            preparedStatement = createPreparedStatement(connection, 
                    "update.expense.clearAccount");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLUpdate: "
                    + "clearAssignmentToAccount() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            EntityExpense expense = 
                    handler.selectFromEntityExpenseListByNameAndTitle(name, title);
            if (expense != null) {
                expense.setAccountLinked("");
            } else {
                return false;
            }
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, title);
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

    @Override
    public boolean clearShopName(Connection connection, String name, 
            String title) {
        /* If no expense name provided for the update operation then cancelling
        this method. */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)
                || !inputCheckLength(title)) {
            return false;
        }

        /* Check if corresponding record is in the database. */
        if (select.executeSelectByNameAndTitle(connection, name, 
                title) == null) {
            return false;
        }

        try {
            preparedStatement = createPreparedStatement(connection, 
                    "update.expense.clearShopName");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLUpdate: "
                    + "clearShopName() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            EntityExpense expense = 
                    handler.selectFromEntityExpenseListByNameAndTitle(name, title);
            if (expense != null) {
                expense.setShopName("");
            } else {
                return false;
            }
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, title);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLUpdate: "
                    + "clearShopName() Error while "
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
