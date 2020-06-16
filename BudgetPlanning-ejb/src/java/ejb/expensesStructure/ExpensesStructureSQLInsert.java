
package ejb.expensesStructure;


import ejb.common.SQLAbstract;
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
public class ExpensesStructureSQLInsert extends SQLAbstract
        implements ExpensesStructureSQLInsertLocal {
    
    @EJB
    ExpensesStructureHandlerLocal handler;
    
    @EJB
    ExpensesStructureSQLSelectLocal select;
    
    private PreparedStatement preparedStatement;

    @Override
    public boolean execute(Connection connection, String type, String name, 
            String accountName, String price, String safetyStock, 
            String orderQty) {
        /* Checking of input values. */
        if (!inputCheckType(type) || !inputCheckNullBlank(name) 
                || !inputCheckLength(name) || !inputCheckLength(accountName)
                || stringToDouble(price) == null 
                || stringToDouble(safetyStock) == null
                || stringToDouble(orderQty) == null) {
            return false;
        }
        /* For SIMPLE_EXPENSES and COMPLEX_EXPENSES the following fields 
        should not be filled: price, safetyStock, orderQty*/
        if (type.equals(ExpenseType.COMPLEX_EXPENSES.getType()) 
                || type.equals(ExpenseType.SIMPLE_EXPENSES.getType())) {
            price = "";
            safetyStock = "";
            orderQty = "";
        }
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "expensesStructure/insert");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLInsert: "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }
        
        double priceDouble;
        double safetyStockDouble;
        double orderQtyDouble;
        
        System.out.println("*** PRICE = " + price);
        
        if (price == null || price.trim().isEmpty()) {
            priceDouble = (double) 0;
        } else {
            priceDouble = stringToDouble(price);
        }
        
        if (safetyStock == null || safetyStock.trim().isEmpty()) {
            safetyStockDouble = (double) 0;
        } else {
            safetyStockDouble = stringToDouble(safetyStock);
        }        
        
        if (orderQty == null || orderQty.trim().isEmpty()) {
            orderQtyDouble = (double) 0;
        } else {
            orderQtyDouble = stringToDouble(orderQty);
        }      

        try {
            //Setting Query Parameters and executing Query;
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, accountName);
            preparedStatement.setInt(4, 0); /* LINKED_TO_COMPLEX_ID is 0 for new records.*/
            preparedStatement.setDouble(5, priceDouble);
            preparedStatement.setDouble(6, safetyStockDouble);
            preparedStatement.setDouble(7, orderQtyDouble);
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
