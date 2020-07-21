
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
    public Integer getPlanningPeriodsHorizon(Connection 
            connection, String planningPeriodsFrequency) {
        if (!inputCheckNullBlank(planningPeriodsFrequency) ||
                planningPeriodsFrequency.length() > 1) {
            return null;
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "mainScreen/select.planningPeriodsHorizon.byFreq");
            preparedStatement.setString(1, planningPeriodsFrequency);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "getPlanningPeriodsHorizon() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return null;
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("PL_PER_HORIZON");
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "getPlanningPeriodsHorizon() Error while "
                    + "executing Select Query: "
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
    }    
    
    @Override
    public boolean setPlanningPeriodsHorizon(Connection 
            connection, String planningPeriodsFrequency, 
            String planningPeriodsHorizon) {
        if (!inputCheckNullBlank(planningPeriodsFrequency) ||
                planningPeriodsFrequency.length() > 1 ||
                !inputCheckNullBlank(planningPeriodsHorizon) ||
                stringToInt(planningPeriodsHorizon) == null) {
            return false;
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "mainScreen/update.planningPeriodsHorizon.byFreq");
            preparedStatement.setString(1, planningPeriodsFrequency);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "setPlanningPeriodsHorizon() "
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }
        
        int planningPeriodsHorizonInt = stringToInt(planningPeriodsHorizon);
        
        try {
            preparedStatement.setInt(1, planningPeriodsHorizonInt);
            preparedStatement.setString(2, planningPeriodsFrequency);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "setPlanningPeriodsHorizon() Error while "
                    + "executing Select Query: "
                    + ex.getMessage() + "***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }
}
