
package ejb.MainScreen;

import ejb.common.SQLAbstract;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class PlannedVariableParamsSQL extends SQLAbstract 
        implements PlannedVariableParamsSQLLocal {

    @Override
    public boolean executeUpdate(Connection connection, String expenseId, 
            String paramName, Map<String, String> updatedValues) {
        /* Checking of input values. */
        if (stringToInt(expenseId) == null) {
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
                if (valueDouble == null) {
                    valueDouble = (double) 0;
                }
            }
            updatedValuesDouble.put(date, valueDouble);
        }
        
        int expenseIdInt = stringToInt(expenseId);
        
        PreparedStatement preparedStatement;
        try {
            switch (paramName) {
                case "PLANNED_PCS" : 
                    preparedStatement = createPreparedStatement(connection,
                    "mainScreen/update.plannedPcs");
                    break;
                case "PLANNED_CUR" :
                    preparedStatement = createPreparedStatement(connection,
                    "mainScreen/update.plannedCur");
                    break;
                case "CONSUMPTION_PCS" :
                    preparedStatement = createPreparedStatement(connection,
                    "mainScreen/update.consumptionPcs");
                    break;
                default : 
                    return false;
            }
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL "
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
                preparedStatement.setInt(2, expenseIdInt);
                preparedStatement.setString(3, date);

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL "
                    + "- executeUpdate(): Error while setting query parameters "
                    + "or executing Update Query: " + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }
    
    @Override
    public String getCurrentPeriodDate(Connection connection) {
        
        Statement statement = null;
        String query = "select distinct DATE from PLANNED_VARIABLE_PARAMS "
                + "where CURPFL = 'Y' group by DATE having DATE = min(DATE)";

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL : "
                    + "getCurrentPeriodDate() error while creating statement: " 
                    + ex.getMessage());
            return null;
        }

        try (ResultSet resultSet = statement.executeQuery(query)) {
            String currentPeriodDate = null;
            while (resultSet.next()) {
                currentPeriodDate = resultSet.getString("DATE");
            }
            return currentPeriodDate;
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL : "
                    + "getCurrentPeriodDate() error while executing '" + query 
                    + "' query: " + ex.getMessage());
            return null;
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                System.out.println("*** PlannedVariableParamsSQL : "
                        + "getCurrentPeriodDate() error while closing "
                        + "statement: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public boolean setCurrentPeriodDate(Connection connection, String date) {
        
        Statement statement = null;
        String clearDateQuery = "update PLANNED_VARIABLE_PARAMS "
                + "set CURPFL = ''";        
        String setDateQuery = "update PLANNED_VARIABLE_PARAMS set CURPFL = 'Y' "
                + "where DATE = '" + date + "'";

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL : "
                    + "setCurrentPeriodDate() error while creating statement: " 
                    + ex.getMessage());
            return false;
        }

        try {
            statement.executeUpdate(clearDateQuery);
            statement.executeUpdate(setDateQuery);
            return true;
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL : "
                    + "setCurrentPeriodDate() error while executing "
                    + " query: " + ex.getMessage());
            return false;
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                System.out.println("*** PlannedVariableParamsSQL : "
                        + "setCurrentPeriodDate() error while closing "
                        + "statement: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public TreeMap<String, Double> selectPlannedExpensesById(Connection 
            connection, Integer id) {
        if (id == null || id < 1) {
            return null;
        }

        TreeMap<String, Double> plannedExpense = new TreeMap<>();
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "mainScreen/select.plannedExpenses.byExpenseId");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "selectPlannedExpensesById() SQL PreparedStatement "
                    + "failure: " + ex.getMessage() + " ***");
            return null;
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String type = resultSet.getString("TYPE");
                String key = resultSet.getString("DATE");
                Double value = (double) 0;
                if (type.equals("SIMPLE_EXPENSES")) {
                    value = resultSet.getDouble("PLANNED_CUR");
                } else if (type.equals("GOODS")) {
                    value = resultSet.getDouble("PLANNED_PCS");
                }
                plannedExpense.put(key, value);
            }
        } catch (SQLException ex) {
            System.out.println("***PlannedVariableParamsSQL: "
                    + "selectPlannedExpensesById() Error while executing Select "
                    + "Query: " + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
        return plannedExpense;
    }
    
    @Override
    public TreeMap<String, Double> selectConsumptionPcsById(Connection 
            connection, Integer id) {
        if (id == null || id < 1) {
            return null;
        }

        TreeMap<String, Double> consumptionPcs = new TreeMap<>();
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "mainScreen/select.consumptionPcs.byExpenseId");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "selectConsumptionPcsById() SQL PreparedStatement "
                    + "failure: " + ex.getMessage() + " ***");
            return null;
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String key = resultSet.getString("DATE");
                Double value = resultSet.getDouble("CONSUMPTION_PCS");
                consumptionPcs.put(key, value);
            }
        } catch (SQLException ex) {
            System.out.println("***PlannedVariableParamsSQL: "
                    + "selectConsumptionPcsById() Error while executing Select "
                    + "Query: " + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
        return consumptionPcs;
    }
}
