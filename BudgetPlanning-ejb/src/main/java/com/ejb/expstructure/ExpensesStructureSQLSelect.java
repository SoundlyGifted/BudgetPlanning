
package com.ejb.expstructure;

import com.ejb.common.SQLAbstract;
import com.ejb.calculation.EntityExpense;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import jakarta.ejb.Stateless;

/**
 * EJB ExpensesStructureSQLSelect is used to perform select operations of 
 * Expenses records in the database.
 */
@Stateless
public class ExpensesStructureSQLSelect extends SQLAbstract
        implements ExpensesStructureSQLSelectLocal {

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<EntityExpense> executeSelectAll(Connection connection)
            throws GenericDBException, GenericDBOperationException {
        try (PreparedStatement preparedStatement
                = createPreparedStatement(connection,
                        "expensesStructure/select.all"); 
                ResultSet resultSet = preparedStatement.executeQuery()) {
            Collection<EntityExpense> list = new LinkedList<>();
            while (resultSet.next()) {
                EntityExpense expense
                        = new EntityExpense(
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
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public EntityExpense executeSelectByName(Connection connection, 
            String name) throws GenericDBException, GenericDBOperationException {
        if (name == null || name.trim().isEmpty()) {
            throw new GenericDBOperationException("Empty Expense name provided.");
        }
        
        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "expensesStructure/select.byname");
        try {
            preparedStatement.setString(1, name);
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
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
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
        }
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public EntityExpense executeSelectById(Connection connection, Integer id) 
            throws GenericDBException, GenericDBOperationException {
        if (id == null || id < 1) {
            throw new GenericDBOperationException("Wrong database Expense ID "
                    + "provided (null or < 1).");
        }

        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "expensesStructure/select.byid");
        try {
            preparedStatement.setInt(1, id);
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
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
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
        }
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public HashMap<Integer, String> executeSelectAllTypes(Connection 
            connection) throws GenericDBOperationException {

        HashMap<Integer, String> result = new HashMap<>();
        
        Statement statement = null;
        String query = "select ID, TYPE from EXPENSES_STRUCTURE where ID > 0";

        try {
            statement = connection.createStatement();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }

        try (ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                result.put(resultSet.getInt("ID"), resultSet.getString("TYPE"));
            }
            return result;
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(statement);
        }
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public HashMap<Integer, HashMap<String, Double>> 
        executeSelectAllValues(Connection connection) 
                throws GenericDBOperationException {

        HashMap<Integer, HashMap<String, Double>> finalResult = new HashMap<>();
        int id;
        
        Statement statement = null;
        String query = "select ID, PRICE, SAFETY_STOCK_PCS, ORDER_QTY_PCS,"
                + "CURRENT_STOCK_PCS from EXPENSES_STRUCTURE where ID > 0";

        try {
            statement = connection.createStatement();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
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
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(statement);
        }
    }

    /**
     * {@inheritDoc}
     */        
    @Override      
    public HashMap<Integer, HashMap<String, Integer>> 
        executeSelectAllLinks(Connection connection) 
                throws GenericDBOperationException {

        HashMap<Integer, HashMap<String, Integer>> finalResult = new HashMap<>();
        int id;
        
        Statement statement = null;
        String query = "select ID, LINKED_TO_COMPLEX_ID, ACCOUNT_ID"
                + " from EXPENSES_STRUCTURE where ID > 0";

        try {
            statement = connection.createStatement();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
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
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(statement);
        }
    }

    /**
     * {@inheritDoc}
     */        
    @Override    
    public Integer executeSelectIdByName (Connection connection, String name) 
            throws GenericDBOperationException, GenericDBException {
        if (name == null || name.trim().isEmpty()) {
            throw new GenericDBOperationException("Empty Expense name provided.");
        }
        
        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                    "expensesStructure/select.byname");
        
        try {
            preparedStatement.setString(1, name);
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);            
        }

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {                
                return resultSet.getInt("ID");
            } else {
                return null;
            }
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);            
        } finally {
            clear(preparedStatement);
        }
    }
}
