
package ejb.MainScreen;

import ejb.common.SQLAbstract;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class PlannedAccountsValuesSQL extends SQLAbstract 
        implements PlannedAccountsValuesSQLLocal {

    @Override
    public boolean executeUpdate(Connection connection, String accountId, 
            String paramName, Map<String, String> updatedValues) {
        /* Checking of input values. */
        if (stringToInt(accountId) == null) {
            return false;
        }
        Map<String, Double> updatedValuesDouble = new TreeMap<>();
        for (Map.Entry<String, String> entry : updatedValues.entrySet()) {
            String date = entry.getKey();
            String value = entry.getValue();
            Double valueDouble;
            if (!inputCheckNullBlank(value)) {
                valueDouble = (double) 0;
            } else {
                valueDouble = stringToDouble(value);
                if (valueDouble == null || valueDouble < 0) {
                    valueDouble = (double) 0;
                }
            }
            updatedValuesDouble.put(date, valueDouble);
        }
        
        int accountIdInt = stringToInt(accountId);
        
        PreparedStatement preparedStatement;
        try {
            switch (paramName) {
                case "PLANNED_INCOME_CUR" : 
                    preparedStatement = createPreparedStatement(connection,
                    "mainScreen/update.incomeCur");
                    break;
                default : 
                    return false;
            }
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedAccountsValuesSQL "
                    + "- executeUpdate(): SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }
        
        try {
            //Setting Query Parameters and executing Query;
            for (Map.Entry<String, Double> entry : 
                    updatedValuesDouble.entrySet()) {
                String date = entry.getKey();
                Double valueDouble = entry.getValue();
                preparedStatement.setDouble(1, valueDouble);
                preparedStatement.setInt(2, accountIdInt);
                preparedStatement.setString(3, date);

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException ex) {
            System.out.println("*** PlannedAccountsValuesSQL "
                    + "- executeUpdate(): Error while setting query parameters "
                    + "or executing Update Query: " + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }
    
    @Override
    public TreeMap<String, Double> selectPlannedAccountsValuesById(Connection 
            connection, Integer id, String paramName) {
        if (id == null || id < 1) {
            return null;
        }

        TreeMap<String, Double> accountParamValues = new TreeMap<>();
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "mainScreen/select.accountPlannedVarParams.byid");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedAccountsValuesSQL: "
                    + "selectPlannedVariableParamsById() SQL PreparedStatement "
                    + "failure: " + ex.getMessage() + " ***");
            return null;
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String key = resultSet.getString("DATE");
                Double value;
                if (paramName.equals("PLANNED_REMAINDER_CUR") 
                        || paramName.equals("PLANNED_INCOME_CUR")) {
                    value = resultSet.getDouble(paramName);
                } else {
                    return null;
                }
                accountParamValues.put(key, value);
            }
        } catch (SQLException ex) {
            System.out.println("***PlannedAccountsValuesSQL: "
                    + "selectPlannedVariableParamsById() Error while executing "
                    + "Select Query: " + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
        return accountParamValues;
    }
}
