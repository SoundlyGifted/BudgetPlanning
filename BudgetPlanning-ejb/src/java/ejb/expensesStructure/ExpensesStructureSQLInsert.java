
package ejb.expensesStructure;


import ejb.expensesStructure.ExpensesTypes.ExpenseType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.ejb.Stateless;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class ExpensesStructureSQLInsert extends ExpensesStructureSQLAbstract
        implements ExpensesStructureSQLInsertLocal {
    
    @EJB
    ExpensesStructureHandlerLocal handler;
    
    @EJB
    ExpensesStructureSQLSelectLocal select;
    
    private PreparedStatement preparedStatement;

    @Override
    public boolean execute(Connection connection, String type, String name, 
            String accountName, String price, String safetyStock, 
            String orderQty, String shopName) {
        /* Checking of input values. */
        if (!inputCheckType(type) || !inputCheckNullBlank(name) || 
                !inputCheckLength(name) || !inputCheckLength(accountName) ||
                !inputCheckLength(shopName)) {
            return false;
        }
        /* For SIMPLE_EXPENSES and COMPLEX_EXPENSES the following fields 
        should not be filled: price, safetyStock, orderQty, shopName*/
        if (type.equals(ExpenseType.COMPLEX_EXPENSES.getType()) 
                || type.equals(ExpenseType.SIMPLE_EXPENSES.getType())) {
            price = "";
            safetyStock = "";
            orderQty = "";
            shopName = "";
        }
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "insert.expense");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLInsert: "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }
        
        int priceInt;
        int safetyStockInt;
        int orderQtyInt;
        
        if (price == null || price.trim().isEmpty()) {
            priceInt = 0;
        } else if (stringToInt(price) == null) {
            return false;
        } else {
            priceInt = stringToInt(price);
        }
        
        if (safetyStock == null || safetyStock.trim().isEmpty()) {
            safetyStockInt = 0;
        } else if (stringToInt(safetyStock) == null) {
            return false;
        } else {
            safetyStockInt = stringToInt(safetyStock);
        }        
        
        if (orderQty == null || orderQty.trim().isEmpty()) {
            orderQtyInt = 0;
        } else if (stringToInt(orderQty) == null) {
            return false;
        } else {
            orderQtyInt = stringToInt(orderQty);
        }      

        try {
            //Setting Query Parameters and executing Query;
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, accountName);
            preparedStatement.setInt(4, 0); /* LINKED_TO_COMPLEX_ID is 0 for new records.*/
            preparedStatement.setInt(5, priceInt);
            preparedStatement.setInt(6, safetyStockInt);
            preparedStatement.setInt(7, orderQtyInt);
            preparedStatement.setString(8, shopName);
            preparedStatement.executeUpdate();
            // Adding Entity to the Entity Object List;
            handler.addToEntityExpenseList(select.
                    executeSelectByName(connection, name));
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLInsert: Error while "
                    + "setting query parameters or executing Insert Query: "
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
