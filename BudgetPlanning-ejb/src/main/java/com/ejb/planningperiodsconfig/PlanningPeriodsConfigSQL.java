
package com.ejb.planningperiodsconfig;

import com.ejb.common.SQLAbstract;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
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
            connection, String planningPeriodsFrequency) 
            throws GenericDBOperationException, GenericDBException{
        if (!inputCheckNullBlank(planningPeriodsFrequency) ||
                planningPeriodsFrequency.length() > 1) {
            throw new GenericDBOperationException("Unable to get current "
                    + "planning period horizon from the database planning "
                    + "periods configuration table, provided planning period "
                    + "freqency '" + planningPeriodsFrequency + "' is invalid.");
        }
        
        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "planningPeriodsConfig/select.planningPeriodsHorizon"
                                + ".byFreq");
        try {
            preparedStatement.setString(1, planningPeriodsFrequency);
        } catch (SQLException sqlex) {
            clear(preparedStatement);
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("PL_PER_HORIZON");
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
    public void setPlanningPeriodsHorizon(Connection 
            connection, String planningPeriodsFrequency, 
            String planningPeriodsHorizon) 
            throws GenericDBOperationException, GenericDBException {
        if (!inputCheckNullBlank(planningPeriodsFrequency) ||
                planningPeriodsFrequency.length() > 1 ||
                !inputCheckNullBlank(planningPeriodsHorizon) ||
                stringToInt(planningPeriodsHorizon) == null) {
            throw new GenericDBOperationException("Unable to set current value "
                    + "of planning periods horizon in the database planning "
                    + "periods configuration table, invalid input parameter(s).");
        }
        
        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "planningPeriodsConfig/update.planningPeriodsHorizon"
                                + ".byFreq");
        try {
            preparedStatement.setString(1, planningPeriodsFrequency);
        } catch (SQLException sqlex) {
            clear(preparedStatement);
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
        
        int planningPeriodsHorizonInt = stringToInt(planningPeriodsHorizon);
        
        try {
            preparedStatement.setInt(1, planningPeriodsHorizonInt);
            preparedStatement.setString(2, planningPeriodsFrequency);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
        }
    }
}
