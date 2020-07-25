
package ejb.accountsStructure;

import ejb.common.SQLAbstract;
import ejb.calculation.EntityAccount;
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
public class AccountsStructureSQL extends SQLAbstract
        implements AccountsStructureSQLLocal {
    
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
        PreparedStatement preparedStatementExpensesAccounts;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "accountsStructure/update");
            preparedStatementExpensesAccounts = createPreparedStatement(connection,
                    "accountsStructure/updateExpensesAccounts");            
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
            
            preparedStatementExpensesAccounts.setInt(1, idForUpdateInt);
            preparedStatementExpensesAccounts.setString(2, name);
            preparedStatementExpensesAccounts.setInt(3, idForUpdateInt);
            preparedStatementExpensesAccounts.executeUpdate();
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
        PreparedStatement preparedStatementExpensesAccounts;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "accountsStructure/delete");
            preparedStatementExpensesAccounts = createPreparedStatement(connection,
                    "accountsStructure/updateExpensesAccounts");              
        } catch (SQLException | IOException ex) {
            System.out.println("*** AccountsStructureSQL - executeDelete(): "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        try {
            //Setting Query Parameters and executing Query;
            preparedStatementExpensesAccounts.setInt(1, 0);
            preparedStatementExpensesAccounts.setString(2, "NOT SET");
            preparedStatementExpensesAccounts.setInt(3, idInt);
            preparedStatementExpensesAccounts.executeUpdate();            
            
            preparedStatement.setInt(1, idInt);
            preparedStatement.executeUpdate();
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
}
