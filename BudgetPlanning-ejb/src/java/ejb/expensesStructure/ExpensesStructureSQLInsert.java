
package ejb.expensesStructure;

import ejb.common.SQLAbstract;
import ejb.expensesStructure.ExpensesTypes.ExpenseType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.ejb.Stateless;
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

    @Override
    public boolean execute(Connection connection, String type, String name, 
            String accountName, String price, String safetyStockPcs, 
            String orderQtyPcs) {
        /* Checking of input values. */
        if (!inputCheckType(type) || !inputCheckNullBlank(name) 
                || !inputCheckLength(name) || !inputCheckLength(accountName)
                || stringToDouble(price) == null 
                || stringToDouble(safetyStockPcs) == null
                || stringToDouble(orderQtyPcs) == null) {
            return false;
        }
        /* For SIMPLE_EXPENSES and COMPLEX_EXPENSES the following fields 
        should not be filled: price, safetyStock, orderQty*/
        if (type.equals(ExpenseType.COMPLEX_EXPENSES.getType()) 
                || type.equals(ExpenseType.SIMPLE_EXPENSES.getType())) {
            price = "";
            safetyStockPcs = "";
            orderQtyPcs = "";
        }
        
        PreparedStatement preparedStatement;
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
        
        try {
            //Setting Query Parameters and executing Query;
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, accountName);
            preparedStatement.setInt(4, 0); /* LINKED_TO_COMPLEX_ID is 0 for new records.*/
            preparedStatement.setDouble(5, priceDouble);
            preparedStatement.setDouble(6, safetyStockPcsDouble);
            preparedStatement.setDouble(7, safetyStockCurDouble);
            preparedStatement.setDouble(8, orderQtyPcsDouble);
            preparedStatement.setDouble(9, orderQtyCurDouble);
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
            clear(preparedStatement);
        }
        return true;
    }
}
