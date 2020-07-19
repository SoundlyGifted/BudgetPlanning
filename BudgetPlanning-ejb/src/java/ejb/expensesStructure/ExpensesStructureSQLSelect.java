
package ejb.expensesStructure;

import ejb.common.SQLAbstract;
import ejb.entity.EntityExpense;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class ExpensesStructureSQLSelect extends SQLAbstract
        implements ExpensesStructureSQLSelectLocal {

    @Override
    public ArrayList<EntityExpense> executeSelectAll(Connection connection) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "expensesStructure/select.all");
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
                        resultSet.getInt("ACCOUNT_ID"),
                        resultSet.getString("ACCOUNT_LINKED"),
                        resultSet.getInt("LINKED_TO_COMPLEX_ID"),
                        resultSet.getDouble("PRICE"),
                        resultSet.getDouble("CURRENT_STOCK_PCS"),                        
                        resultSet.getDouble("CURRENT_STOCK_CUR"),                        
                        resultSet.getDouble("CURRENT_STOCK_WSC_PCS"),                        
                        resultSet.getDouble("CURRENT_STOCK_WSC_CUR"),                        
                        resultSet.getDouble("SAFETY_STOCK_PCS"),
                        resultSet.getDouble("SAFETY_STOCK_CUR"),
                        resultSet.getDouble("ORDER_QTY_PCS"),
                        resultSet.getDouble("ORDER_QTY_CUR")));
            }
            return new ArrayList<>(list);
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLSelect: "
                    + "executeSelectAll() Error while executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
    }
    
    @Override
    public EntityExpense executeSelectByName(Connection connection, 
            String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "expensesStructure/select.byname");
            preparedStatement.setString(1, name);
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLSelect: "
                    + "executeSelectByName() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return new EntityExpense(resultSet.getInt("ID"),
                        resultSet.getString("TYPE"),
                        resultSet.getString("NAME"),
                        resultSet.getInt("ACCOUNT_ID"),
                        resultSet.getString("ACCOUNT_LINKED"),
                        resultSet.getInt("LINKED_TO_COMPLEX_ID"),
                        resultSet.getDouble("PRICE"),
                        resultSet.getDouble("CURRENT_STOCK_PCS"),                        
                        resultSet.getDouble("CURRENT_STOCK_CUR"),                        
                        resultSet.getDouble("CURRENT_STOCK_WSC_PCS"),                        
                        resultSet.getDouble("CURRENT_STOCK_WSC_CUR"),                        
                        resultSet.getDouble("SAFETY_STOCK_PCS"),
                        resultSet.getDouble("SAFETY_STOCK_CUR"),
                        resultSet.getDouble("ORDER_QTY_PCS"),
                        resultSet.getDouble("ORDER_QTY_CUR"));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLSelect: "
                    + "executeSelectByName() Error while "
                    + "executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
    }
    
    @Override
    public EntityExpense executeSelectById(Connection connection, Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "expensesStructure/select.byid");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLSelect: "
                    + "executeSelectById() SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
            return new EntityExpense(resultSet.getInt("ID"),
                    resultSet.getString("TYPE"),
                    resultSet.getString("NAME"),
                    resultSet.getInt("ACCOUNT_ID"),
                    resultSet.getString("ACCOUNT_LINKED"),
                    resultSet.getInt("LINKED_TO_COMPLEX_ID"),
                    resultSet.getDouble("PRICE"),
                    resultSet.getDouble("CURRENT_STOCK_PCS"),                        
                    resultSet.getDouble("CURRENT_STOCK_CUR"),                        
                    resultSet.getDouble("CURRENT_STOCK_WSC_PCS"),                        
                    resultSet.getDouble("CURRENT_STOCK_WSC_CUR"),                       
                    resultSet.getDouble("SAFETY_STOCK_PCS"),
                    resultSet.getDouble("SAFETY_STOCK_CUR"),
                    resultSet.getDouble("ORDER_QTY_PCS"),
                    resultSet.getDouble("ORDER_QTY_CUR"));                
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLSelect: "
                    + "executeSelectById() Error while executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
    }
}
