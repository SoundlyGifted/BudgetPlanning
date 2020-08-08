
package ejb.expensesStructure;

import ejb.common.SQLAbstract;
import ejb.calculation.EntityExpense;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
                EntityExpense expense = 
                        new EntityExpense(
                            resultSet.getInt("ID"),
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
                expense.calculateFixedParameters();       
                list.add(expense);
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
                EntityExpense expense = 
                        new EntityExpense(
                            resultSet.getInt("ID"),
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
                expense.calculateFixedParameters();                   
                return expense;
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
                EntityExpense expense = 
                        new EntityExpense(
                            resultSet.getInt("ID"),
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
                expense.calculateFixedParameters();                   
                return expense;              
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
    
    @Override
    public HashMap<Integer, String> executeSelectAllTypes(Connection 
            connection) {

        HashMap<Integer, String> result = new HashMap<>();
        
        Statement statement = null;
        String query = "select ID, TYPE from EXPENSES_STRUCTURE where ID > 0";

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("*** ExpensesStructureSQLSelect : "
                    + "executeSelectAllTypes() error while creating statement: "
                    + ex.getMessage());
            return null;
        }

        try (ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                result.put(resultSet.getInt("ID"), resultSet.getString("TYPE"));
            }
            return result;
        } catch (SQLException ex) {
            System.out.println("*** ExpensesStructureSQLSelect : "
                    + "executeSelectAllTypes() error while executing '" + query
                    + "' query: " + ex.getMessage());
            return null;
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                System.out.println("*** ExpensesStructureSQLSelect : "
                        + "executeSelectAllTypes() error while closing "
                        + "statement: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public HashMap<Integer, HashMap<String, Double>> 
        executeSelectAllValues(Connection connection) {

        HashMap<Integer, HashMap<String, Double>> finalResult = new HashMap<>();
        int id;
        
        Statement statement = null;
        String query = "select ID, PRICE, SAFETY_STOCK_PCS, ORDER_QTY_PCS,"
                + "CURRENT_STOCK_PCS from EXPENSES_STRUCTURE where ID > 0";

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("*** ExpensesStructureSQLSelect : "
                    + "executeSelectAllValues() error while creating "
                    + "statement: " + ex.getMessage());
            return null;
        }

        try (ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                id = resultSet.getInt("ID");
                HashMap<String, Double> paramValues = new HashMap<>();                
                paramValues.put("PRICE", resultSet.getDouble("PRICE"));
                paramValues.put("SAFETY_STOCK_PCS", 
                        resultSet.getDouble("SAFETY_STOCK_PCS"));
                paramValues.put("ORDER_QTY_PCS", 
                        resultSet.getDouble("ORDER_QTY_PCS"));
                paramValues.put("CURRENT_STOCK_PCS", 
                        resultSet.getDouble("CURRENT_STOCK_PCS"));
                finalResult.put(id, paramValues);
            }
            return finalResult;
        } catch (SQLException ex) {
            System.out.println("*** ExpensesStructureSQLSelect : "
                    + "executeSelectAllValues() error while executing '" + query
                    + "' query: " + ex.getMessage());
            return null;
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                System.out.println("*** ExpensesStructureSQLSelect : "
                        + "executeSelectAllValues() error while closing "
                        + "statement: " + ex.getMessage());
            }
        }
    }
    
    @Override      
    public HashMap<Integer, HashMap<String, Integer>> 
        executeSelectAllLinks(Connection connection) {

        HashMap<Integer, HashMap<String, Integer>> finalResult = new HashMap<>();
        int id;
        
        Statement statement = null;
        String query = "select ID, LINKED_TO_COMPLEX_ID, ACCOUNT_ID"
                + " from EXPENSES_STRUCTURE where ID > 0";

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("*** ExpensesStructureSQLSelect : "
                    + "executeSelectAllLinks() error while creating "
                    + "statement: " + ex.getMessage());
            return null;
        }

        try (ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                id = resultSet.getInt("ID");
                HashMap<String, Integer> paramLinks = new HashMap<>();                
                paramLinks.put("LINKED_TO_COMPLEX_ID", 
                        resultSet.getInt("LINKED_TO_COMPLEX_ID"));
                paramLinks.put("ACCOUNT_ID", resultSet.getInt("ACCOUNT_ID"));
                finalResult.put(id, paramLinks);                
            }
            return finalResult;
        } catch (SQLException ex) {
            System.out.println("*** ExpensesStructureSQLSelect : "
                    + "executeSelectAllLinks() error while executing '" + query
                    + "' query: " + ex.getMessage());
            return null;
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                System.out.println("*** ExpensesStructureSQLSelect : "
                        + "executeSelectAllLinks() error while closing "
                        + "statement: " + ex.getMessage());
            }
        }
    }
        
    @Override    
    public Integer executeSelectIdByName (Connection connection, String name) {
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
                    + "executeSelectIdByName() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {                
                return resultSet.getInt("ID");
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLSelect: "
                    + "executeSelectIdByName() Error while "
                    + "executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }        
    }    
}
