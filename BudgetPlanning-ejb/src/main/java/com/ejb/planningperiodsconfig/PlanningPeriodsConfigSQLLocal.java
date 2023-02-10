
package com.ejb.planningperiodsconfig;

import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import jakarta.ejb.Local;

/**
 * EJB PlanningPeriodsConfigSQL Local interface contains methods to change 
 * planning periods horizon in the database planning periods configuration 
 * table.
 */
@Local
public interface PlanningPeriodsConfigSQLLocal {
    
    /**
     * Gets current value of planning periods horizon from the database 
     * planning periods configuration table.
     * 
     * @param connection database Connection.
     * @param planningPeriodsFrequency frequency of the planning time periods.
     * @return planning periods horizon value obtained from the database.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public Integer getPlanningPeriodsHorizon(Connection 
            connection, String planningPeriodsFrequency) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Sets current value of planning periods horizon in the database 
     * planning periods configuration table.
     * 
     * @param connection database Connection.
     * @param planningPeriodsFrequency frequency of the planning time periods.
     * @param planningPeriodsHorizon planning time periods horizon.
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public void setPlanningPeriodsHorizon(Connection 
            connection, String planningPeriodsFrequency, 
            String planningPeriodsHorizon) 
            throws GenericDBOperationException, GenericDBException;
}
