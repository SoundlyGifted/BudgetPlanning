
package com.ejb.planningPeriodsConfig;

import com.ejb.common.SQLAbstract;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.ejb.Stateless;

/**
 * EJB PlanningPeriodsConfigSQL is used to change planning periods horizon in 
 * the database planning periods configuration table.
 */
@Stateless
public class PlanningPeriodsConfigSQL extends SQLAbstract 
        implements PlanningPeriodsConfigSQLLocal {

    /**
     * {@inheritDoc}
     */
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
                    "planningPeriodsConfig/select.planningPeriodsHorizon.byFreq");
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

    /**
     * {@inheritDoc}
     */    
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
                    "planningPeriodsConfig/update.planningPeriodsHorizon.byFreq");
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
