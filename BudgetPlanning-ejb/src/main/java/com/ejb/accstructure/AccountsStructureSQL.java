
package com.ejb.accstructure;

import com.ejb.calculation.AccountsHandlerLocal;
import com.ejb.common.SQLAbstract;
import com.ejb.calculation.EntityAccount;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import com.ejb.mainscreen.PlannedAccountsValuesSQLLocal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

/**
 * EJB AccountsStructureSQL is used to perform operations on Account records in 
 * the database.
 */
@Stateless
public class AccountsStructureSQL extends SQLAbstract
        implements AccountsStructureSQLLocal {
    
    @EJB
    private AccountsHandlerLocal aHandler;
    
    @EJB
    private PlannedAccountsValuesSQLLocal plannedAccountsSQL;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void executeInsert(Connection connection, String name,
            String currentRemainder) 
            throws GenericDBOperationException, GenericDBException {
        // Checking of input values.
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)
                || stringToDouble(currentRemainder) == null) {
            throw new GenericDBOperationException("Unable to add the Account, "
                    + "empty or wrong parameters provided.");
        }
        
        double CurrentRemainderDouble = stringToDouble(currentRemainder);
        
        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, "accountsStructure/insert");        

        try {
            // Setting Query Parameters and executing Query.
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, CurrentRemainderDouble);
            preparedStatement.executeUpdate();
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
    public void executeUpdate(Connection connection, String idForUpdate,
            String name, String currentRemainder) 
            throws GenericDBOperationException, GenericDBException {
        // Checking of input values.
        if (stringToInt(idForUpdate) == null || !inputCheckNullBlank(name)
                || !inputCheckLength(name)
                || stringToDouble(currentRemainder) == null) {
            throw new GenericDBOperationException("Unable to update the "
                    + "Account, empty or wrong parameters provided.");
        }

        int idForUpdateInt = stringToInt(idForUpdate);
        double CurrentRemainderDouble = stringToDouble(currentRemainder);

        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, "accountsStructure/update");
        PreparedStatement preparedStatementExpenseLinkToAccount 
                    = createPreparedStatement(connection,
                        "expensesStructure/update.expenseLinkToAccount");            

        try {
            // Setting Query Parameters and executing Query.
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, CurrentRemainderDouble);
            preparedStatement.setInt(3, idForUpdateInt);
            preparedStatement.executeUpdate();
            
            preparedStatementExpenseLinkToAccount.setInt(1, idForUpdateInt);
            preparedStatementExpenseLinkToAccount.setString(2, name);
            preparedStatementExpenseLinkToAccount.setInt(3, idForUpdateInt);
            preparedStatementExpenseLinkToAccount.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
            clear(preparedStatementExpenseLinkToAccount);
        }
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void executeDelete(Connection connection, String id) 
            throws GenericDBOperationException, GenericDBException {
        if (stringToInt(id) == null) {
            throw new GenericDBOperationException("Unable to delete the "
                    + "Account, null Account ID value provided.");
        }
        int idInt = stringToInt(id);

        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, "accountsStructure/delete");
        PreparedStatement preparedStatementExpenseLinkToAccount 
                    = createPreparedStatement(connection,
                        "expensesStructure/update.expenseLinkToAccount");              

        try {
            // Setting Account to "NOT SET" for all the linked Expenses.
            preparedStatementExpenseLinkToAccount.setInt(1, 0);
            preparedStatementExpenseLinkToAccount.setString(2, "NOT SET");
            preparedStatementExpenseLinkToAccount.setInt(3, idInt);
            preparedStatementExpenseLinkToAccount.executeUpdate();            

            // Removing Account from the EntityAccountList.
            EntityAccount acctToDelete = executeSelectById(connection, idInt);
            aHandler.removeFromEntityAccountList(acctToDelete);
            
            // Removing all Plan for the deleted Account (database).
            plannedAccountsSQL.executeDeleteByAccountId(connection, id);
            
            // Removing the Account from the Accounts Structure (database).
            preparedStatement.setInt(1, idInt);
            preparedStatement.executeUpdate();            
            
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
            clear(preparedStatementExpenseLinkToAccount);
        }
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public ArrayList<EntityAccount> executeSelectAll(Connection connection) 
            throws GenericDBOperationException, GenericDBException {
        try (PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "accountsStructure/select.all");
                ResultSet resultSet = preparedStatement.executeQuery()) {
            Collection<EntityAccount> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(new EntityAccount(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getDouble("CURRENT_REMAINDER_CUR")));
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
    public EntityAccount executeSelectByName(Connection connection,
            String name) throws GenericDBOperationException, GenericDBException {
        if (name == null || name.trim().isEmpty()) {
            throw new GenericDBOperationException("Empty Account name provided.");
        }
        
        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "accountsStructure/select.byname");
        try {
            preparedStatement.setString(1, name);
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return new EntityAccount(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getDouble("CURRENT_REMAINDER_CUR"));
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
    public EntityAccount executeSelectById(Connection connection, Integer id) 
            throws GenericDBOperationException, GenericDBException {
        if (id == null || id < 0) {
            throw new GenericDBOperationException("Unable to select the "
                    + "Account, null or wrong Account ID value provided.");
        }
        
        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "accountsStructure/select.byid");
        try {
            preparedStatement.setInt(1, id);
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return new EntityAccount(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getDouble("CURRENT_REMAINDER_CUR"));
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
    public HashMap<Integer, HashMap<String, Double>> 
        executeSelectAllValues(Connection connection) 
                throws GenericDBOperationException {
        HashMap<Integer, HashMap<String, Double>> finalResult = new HashMap<>();
        int id;
        
        Statement statement = null;
        String query = "select ID, CURRENT_REMAINDER_CUR "
                + "from ACCOUNTS_STRUCTURE where ID > 0";

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
                paramValues.put("CURRENT_REMAINDER_CUR", 
                        resultSet.getDouble("CURRENT_REMAINDER_CUR"));
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
    public void updateCurrentRemainderById(Connection connection, Integer id,
            Double newCurrentRemainderCur) 
            throws GenericDBOperationException, GenericDBException {
        if (id == null || id <= 0 || newCurrentRemainderCur == null) {
            throw new GenericDBOperationException("Unable to update the "
                    + "Account, null or wrong Account ID value or null "
                    + "Remainder value provided.");
        }

        PreparedStatement preparedStatement 
                = createPreparedStatement(connection,
                    "accountsStructure/update.currentRemainder.byid");

        try {
            preparedStatement.setDouble(1, newCurrentRemainderCur);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
        }
    } 
}
