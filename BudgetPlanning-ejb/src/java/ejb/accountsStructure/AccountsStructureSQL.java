
package ejb.accountsStructure;

import ejb.calculation.AccountsHandlerLocal;
import ejb.common.SQLAbstract;
import ejb.calculation.EntityAccount;
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
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class AccountsStructureSQL extends SQLAbstract
        implements AccountsStructureSQLLocal {
    
    @EJB
    private AccountsHandlerLocal aHandler;
    
    @Override
    public boolean executeInsert(Connection connection, String name,
            String currentRemainder) {
        /* Checking of input values. */
        if (!inputCheckNullBlank(name) || !inputCheckLength(name)
                || stringToDouble(currentRemainder) == null) {
            return false;
        }

        double CurrentRemainderDouble = stringToDouble(currentRemainder);

        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "accountsStructure/insert");        
        } catch (SQLException | IOException ex) {
            System.out.println("*** AccountsStructureSQL - executeInsert(): "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            //Setting Query Parameters and executing Query;
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, CurrentRemainderDouble);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("*** AccountsStructureSQL - executeInsert(): "
                    + "Error while setting query parameters or executing "
                    + "Insert Query: " + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }

    @Override
    public boolean executeUpdate(Connection connection, String idForUpdate,
            String name, String currentRemainder) {
        /* Checking of input values. */
        if (stringToInt(idForUpdate) == null || !inputCheckNullBlank(name)
                || !inputCheckLength(name)
                || stringToDouble(currentRemainder) == null) {
            return false;
        }

        int idForUpdateInt = stringToInt(idForUpdate);
        double CurrentRemainderDouble = stringToDouble(currentRemainder);

        PreparedStatement preparedStatement;
        PreparedStatement preparedStatementExpenseLinkToAccount;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "accountsStructure/update");
            preparedStatementExpenseLinkToAccount 
                    = createPreparedStatement(connection,
                        "expensesStructure/update.expenseLinkToAccount");            
        } catch (SQLException | IOException ex) {
            System.out.println("*** AccountsStructureSQL - executeUpdate(): "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            //Setting Query Parameters and executing Query;
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, CurrentRemainderDouble);
            preparedStatement.setInt(3, idForUpdateInt);
            preparedStatement.executeUpdate();
            
            preparedStatementExpenseLinkToAccount.setInt(1, idForUpdateInt);
            preparedStatementExpenseLinkToAccount.setString(2, name);
            preparedStatementExpenseLinkToAccount.setInt(3, idForUpdateInt);
            preparedStatementExpenseLinkToAccount.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("*** AccountsStructureSQL - executeUpdate(): "
                    + "Error while setting query parameters or executing "
                    + "Update Query: " + ex.getMessage() + " ***");
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
        PreparedStatement preparedStatementExpenseLinkToAccount;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "accountsStructure/delete");
            preparedStatementExpenseLinkToAccount 
                    = createPreparedStatement(connection,
                        "expensesStructure/update.expenseLinkToAccount");              
        } catch (SQLException | IOException ex) {
            System.out.println("*** AccountsStructureSQL - executeDelete(): "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            //Setting Query Parameters and executing Query;
            preparedStatementExpenseLinkToAccount.setInt(1, 0);
            preparedStatementExpenseLinkToAccount.setString(2, "NOT SET");
            preparedStatementExpenseLinkToAccount.setInt(3, idInt);
            preparedStatementExpenseLinkToAccount.executeUpdate();            
            
            preparedStatement.setInt(1, idInt);
            preparedStatement.executeUpdate();
            
            EntityAccount accountInList = aHandler
                    .getEntityAccountList().get(idInt);
            if (accountInList != null) {
                aHandler.removeFromEntityAccountList(accountInList);
            }
        } catch (SQLException ex) {
            System.out.println("*** AccountsStructureSQL - executeDelete(): "
                    + "Error while setting query parameters or executing "
                    + "Delete Query: " + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }

    @Override
    public ArrayList<EntityAccount> executeSelectAll(Connection connection) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "accountsStructure/select.all");
        } catch (SQLException | IOException ex) {
            System.out.println("*** AccountsStructureSQL: "
                    + "executeSelectAll() SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            Collection<EntityAccount> list = new LinkedList<>();
            while (resultSet.next()) {
                list.add(new EntityAccount(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getDouble("CURRENT_REMAINDER_CUR")));
            }
            return new ArrayList<>(list);
        } catch (SQLException ex) {
            System.out.println("***AccountsStructureSQL: "
                    + "executeSelectAll() Error while executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
    }

    @Override
    public EntityAccount executeSelectByName(Connection connection,
            String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "accountsStructure/select.byname");
            preparedStatement.setString(1, name);
        } catch (SQLException | IOException ex) {
            System.out.println("*** AccountsStructureSQL: "
                    + "executeSelectByName() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return new EntityAccount(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getDouble("CURRENT_REMAINDER_CUR"));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("***AccountsStructureSQL: executeSelectByName() "
                    + "Error while executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
    }

    @Override
    public EntityAccount executeSelectById(Connection connection, Integer id) {
        if (id == null || id < 0) {
            return null;
        }

        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "accountsStructure/select.byid");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** AccountsStructureSQL: executeSelectById()"
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return new EntityAccount(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getDouble("CURRENT_REMAINDER_CUR"));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("***AccountsStructureSQL: executeSelectById() "
                    + "Error while executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
    }
    
    @Override
    public HashMap<Integer, HashMap<String, Double>> 
        executeSelectAllValues(Connection connection) {

        HashMap<Integer, HashMap<String, Double>> finalResult = new HashMap<>();
        int id;
        
        Statement statement = null;
        String query = "select ID, CURRENT_REMAINDER_CUR "
                + "from ACCOUNTS_STRUCTURE";

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("*** AccountsStructureSQL : "
                    + "executeSelectAllValues() error while creating "
                    + "statement: " + ex.getMessage());
            return null;
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
        } catch (SQLException ex) {
            System.out.println("*** AccountsStructureSQL : "
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
    public boolean updateCurrentRemainderById(Connection connection, Integer id,
            Double newCurrentRemainderCur) {
        if (id == null || id <= 0 || newCurrentRemainderCur == null) {
            return false;
        }

        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "accountsStructure/update.currentRemainder.byid");
        } catch (SQLException | IOException ex) {
            System.out.println("*** AccountsStructureSQL: "
                    + "updateCurrentRemainderById() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            preparedStatement.setDouble(1, newCurrentRemainderCur);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("***AccountsStructureSQL: "
                    + "updateCurrentRemainderById() Error while "
                    + "setting query parameters or executing Update Query: "
                    + ex.getMessage() + "***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    } 
}
