
package com.ejb.calculation;

import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.util.TreeSet;
import jakarta.ejb.Local;

/**
 * EJB TimePeriodsHandler Local interface contains methods to perform 
 * operations on attributes of application planning time periods.
 */
@Local
public interface TimePeriodsHandlerLocal {
    
    /**
     * Calculates set of time period dates that starts with the current period
     * date specified in {@link TimePeriods#currentPeriodDate}. 
     * Needs {@link TimePeriods#currentPeriodDate} to be calculated
     * preliminarily.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @return set of time period dates that starts with the current period
     * date specified in {@link TimePeriods#currentPeriodDate}
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public TreeSet<String> calculateTimePeriodDates(Connection 
            connection, String inputPlanningPeriodsFrequency) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Gets the date of next planning time period based on the value of
     * {@link TimePeriods#currentPeriodDate} and the input planning time periods
     * frequency.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @return date of next planning time period (relative to the current 
     * period date).
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public String getNextPeriodDate(Connection connection,
            String inputPlanningPeriodsFrequency) 
            throws GenericDBOperationException, GenericDBException;
    
    /**
     * Gets the date of previous planning time period based on the value of
     * {@link TimePeriods#currentPeriodDate} and the input planning time periods
     * frequency.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @return date of previous planning time period (relative to the current 
     * period date).
     * @throws com.ejb.database.exceptions.GenericDBException if a database 
     * connection operation or an sql-file reading operation throws an 
     * exception.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public String getPreviousPeriodDate(Connection connection,
            String inputPlanningPeriodsFrequency) 
            throws GenericDBOperationException, GenericDBException;
}
