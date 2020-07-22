
package ejb.actualExpenses;

import ejb.common.SQLAbstract;
import ejb.entity.EntityExpense;
import ejb.expensesStructure.ExpensesStructureSQLSelectLocal;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class ActualExpensesSQL extends SQLAbstract 
        implements ActualExpensesSQLLocal {

    @EJB
    private ExpensesStructureSQLSelectLocal select;
    
    @Override
    public boolean executeInsert(Connection connection, String date, 
            String expenseName, String expenseTitle, String shopName, 
            String price, String qty, String comment) {
        /* Checking of input values. */
        if (!inputCheckNullBlank(date) || !inputCheckNullBlank(expenseName)
                || !inputCheckNullBlank(price) || stringToDouble(price) == null
                || !inputCheckLength(expenseName) 
                || !inputCheckLength(expenseTitle) 
                || !inputCheckLength(shopName) || !inputCheckLength(comment)) {
            return false;
        }        
        
        String week;
        int dayN;
        String dayC;
        int monthN;
        String monthC;
        int year;
        int expenseIdInt;
        double priceDouble = stringToDouble(price);
        double qtyDouble;
        double costDouble;
        
        /* Checking if Expense with given name exists in the database 
        (EXPENSES_STRUCTURE table). If exists:
        1) getting Expense ID. 
        2) checking it's type (only SIMPLE_EXPENSES and GOODS can be populated 
        in ACTUAL_EXPENSES table. */
        EntityExpense expenseGiven = select.executeSelectByName(connection, expenseName);
        if (expenseGiven == null) {
            return false;
        } else {
            expenseIdInt = expenseGiven.getId();
            String type = expenseGiven.getType();
            if (!"SIMPLE_EXPENSES".equals(type) && !"GOODS".equals(type)) {
                return false;
            }
        }

        /* setting QTY to 1 if user did not enter QTY. */
        if (qty == null || qty.trim().isEmpty()) {
            qtyDouble = (double) 1;
        } else {
            qtyDouble = stringToDouble(qty);
        }
        
        costDouble = round(priceDouble * qtyDouble, 2);

        /* Calculating time parameters. */
        String[] partOfDate = date.split("\\-");
        dayN = Integer.parseInt(partOfDate[2]);
        monthN = Integer.parseInt(partOfDate[1]);
        year = Integer.parseInt(partOfDate[0]);
        
        Calendar c = Calendar.getInstance();
        c.set(year, monthN-1, dayN);
        dayC = getDay(c.get(Calendar.DAY_OF_WEEK));
        monthC = getMonth(monthN);
        week = getWeek(c.get(Calendar.WEEK_OF_YEAR));
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "actualExpenses/insert");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ActualExpensesSQL - executeInsert(): "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }
        
        try {
            //Setting Query Parameters and executing Query;
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, week);
            preparedStatement.setInt(3, dayN);
            preparedStatement.setString(4, dayC);
            preparedStatement.setInt(5, monthN);
            preparedStatement.setString(6, monthC);
            preparedStatement.setInt(7, year);
            preparedStatement.setInt(8, expenseIdInt);
            preparedStatement.setString(9, expenseName);
            preparedStatement.setString(10, expenseTitle);
            preparedStatement.setString(11, shopName);
            preparedStatement.setDouble(12, priceDouble);
            preparedStatement.setDouble(13, qtyDouble);
            preparedStatement.setDouble(14, costDouble);
            preparedStatement.setString(15, comment);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("*** ActualExpensesSQL - executeInsert(): Error "
                    + "while setting query parameters or executing Insert "
                    + "Query: " + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }

    @Override
    public boolean executeUpdate(Connection connection, String idForUpdate, 
            String date, String expenseName, String expenseTitle, String shopName, 
            String price, String qty, String comment) {
        /* Checking of input values. */
        if (stringToInt(idForUpdate) == null || !inputCheckNullBlank(date) 
                || !inputCheckNullBlank(expenseName)
                || !inputCheckNullBlank(price) || stringToDouble(price) == null
                || !inputCheckLength(expenseName) 
                || !inputCheckLength(expenseTitle) 
                || !inputCheckLength(shopName) || !inputCheckLength(comment)) {
            return false;
        }        

        String week;
        int dayN;
        String dayC;
        int monthN;
        String monthC;
        int year;
        int expenseIdInt;
        double priceDouble = stringToDouble(price);
        double qtyDouble;
        double costDouble;
        int idForUpdateInt = stringToInt(idForUpdate);
        
        /* Checking if Expense with given name exists in the database 
        (EXPENSES_STRUCTURE table). If exists:
        1) getting Expense ID. 
        2) checking it's type (only SIMPLE_EXPENSES and GOODS can be populated 
        in ACTUAL_EXPENSES table. */
        EntityExpense expenseGiven = select.executeSelectByName(connection, expenseName);
        if (expenseGiven == null) {
            return false;
        } else {
            expenseIdInt = expenseGiven.getId();
            String type = expenseGiven.getType();
            if (!"SIMPLE_EXPENSES".equals(type) && !"GOODS".equals(type)) {
                return false;
            }
        }

        /* setting QTY to 1 if user did not enter QTY. */
        if (qty == null || qty.trim().isEmpty()) {
            qtyDouble = (double) 1;
        } else {
            qtyDouble = stringToDouble(qty);
        }        
        
        costDouble = round(priceDouble * qtyDouble, 2);

        /* Calculating time parameters. */
        String[] partOfDate = date.split("\\-");
        dayN = Integer.parseInt(partOfDate[2]);
        monthN = Integer.parseInt(partOfDate[1]);
        year = Integer.parseInt(partOfDate[0]);
        
        Calendar c = Calendar.getInstance();
        c.set(year, monthN-1, dayN);
        dayC = getDay(c.get(Calendar.DAY_OF_WEEK));
        monthC = getMonth(monthN);
        week = getWeek(c.get(Calendar.WEEK_OF_YEAR));
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "actualExpenses/update");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ActualExpensesSQL - executeUpdate(): "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }        
        
        try {
            //Setting Query Parameters and executing Query;
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, week);
            preparedStatement.setInt(3, dayN);
            preparedStatement.setString(4, dayC);
            preparedStatement.setInt(5, monthN);
            preparedStatement.setString(6, monthC);
            preparedStatement.setInt(7, year);
            preparedStatement.setInt(8, expenseIdInt);
            preparedStatement.setString(9, expenseName);
            preparedStatement.setString(10, expenseTitle);
            preparedStatement.setString(11, shopName);
            preparedStatement.setDouble(12, priceDouble);
            preparedStatement.setDouble(13, qtyDouble);
            preparedStatement.setDouble(14, costDouble);
            preparedStatement.setString(15, comment);
            preparedStatement.setInt(16, idForUpdateInt);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("*** ActualExpensesSQL - executeUpdate(): Error "
                    + "while setting query parameters or executing Update "
                    + "Query: " + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;        
    }

    @Override
    public boolean executeDelete(Connection connection, String id) {
        if (stringToInt(id) == null) {
            return false;
        }
        int idInt = stringToInt(id);
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "actualExpenses/delete");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ActualExpensesSQL - executeDelete(): "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }          
        
        try {
            //Setting Query Parameters and executing Query;
            preparedStatement.setInt(1, idInt);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("*** ActualExpensesSQL - executeDelete(): Error "
                    + "while setting query parameters or executing Delete "
                    + "Query: " + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }
    
}
