
package ejb.MainScreen;

import ejb.common.SQLAbstract;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
            String paramName, HashMap<String, String> updatedValues) {
        /* Checking of input values. */
        if (stringToInt(expenseId) == null) {
            return false;
        }
        HashMap<String, Double> updatedValuesDouble = new HashMap<>();
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
                    "mainScreen/updatePlannedPcs");
                    break;
                case "PLANNED_CUR" :
                    preparedStatement = createPreparedStatement(connection,
                    "mainScreen/updatePlannedCur");
                    break;
                case "CONSUMPTION_PCS" :
                    preparedStatement = createPreparedStatement(connection,
                    "mainScreen/updateConsumptionPcs");
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
    
    
}
