
package ejb.expensesStructure;

import ejb.entity.EntityExpense;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class ExpensesStructureSQLSelect extends ExpensesStructureSQLAbstract
        implements ExpensesStructureSQLSelectLocal {

    private PreparedStatement preparedStatement;

    @Override
    public ArrayList<EntityExpense> executeSelectAll(Connection connection) {
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "select.expense.all");
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLSelect: "
                    + "executeSelectAll() SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            Collection<EntityExpense> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(new EntityExpense(resultSet.getInt("ID"),
                        resultSet.getString("TYPE"),
                        resultSet.getString("NAME"),
                        resultSet.getString("ACCOUNT_LINKED"),
                        resultSet.getInt("LINKED_TO_COMPLEX_ID"),
                        resultSet.getString("TITLE"),
                        resultSet.getInt("PRICE"),
                        resultSet.getInt("SAFETY_STOCK"),
                        resultSet.getInt("ORDER_QTY"),
                        resultSet.getString("SHOP_NAME")));
            }
            return new ArrayList<>(list);
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLSelect: "
                    + "executeSelectAll() Error while executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear();
        }
    }
    
    @Override
    public EntityExpense executeSelectByNameAndTitle(Connection connection, 
            String name, String title) {
        if (name == null || name.trim().isEmpty() || title == null || 
                title.length() > 255) {
            return null;
        }
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "select.expense.bynameandtitle");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, title);
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLSelect: "
                    + "executeSelectByNameAndTitle() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return new EntityExpense(resultSet.getInt("ID"),
                        resultSet.getString("TYPE"),
                        resultSet.getString("NAME"),
                        resultSet.getString("ACCOUNT_LINKED"),
                        resultSet.getInt("LINKED_TO_COMPLEX_ID"),
                        resultSet.getString("TITLE"),
                        resultSet.getInt("PRICE"),
                        resultSet.getInt("SAFETY_STOCK"),
                        resultSet.getInt("ORDER_QTY"),
                        resultSet.getString("SHOP_NAME"));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLSelect: "
                    + "executeSelectByNameAndTitle() Error while "
                    + "executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear();
        }
    }
    
    @Override
    public EntityExpense executeSelectById(Connection connection, Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "select.expense.byid");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLSelect: executeSelectById()"
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
            return new EntityExpense(resultSet.getInt("ID"),
                    resultSet.getString("TYPE"),
                    resultSet.getString("NAME"),
                    resultSet.getString("ACCOUNT_LINKED"),
                    resultSet.getInt("LINKED_TO_COMPLEX_ID"),
                    resultSet.getString("TITLE"),
                    resultSet.getInt("PRICE"),
                    resultSet.getInt("SAFETY_STOCK"),
                    resultSet.getInt("ORDER_QTY"),
                    resultSet.getString("SHOP_NAME"));                
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLSelect: executeSelectById() Error while "
                    + "executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear();
        }
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
