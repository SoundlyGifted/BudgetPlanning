
package ejb.MainScreen;

import java.sql.Connection;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.Local;

/**
 * EJB PlannedAccountsValuesSQL Local interface contains methods to perform 
 * operations on planned and calculated parameters data of the Accounts in the 
 * database.
 */
@Local
public interface PlannedAccountsValuesSQLLocal {

    /**
     * Updates Account's planned parameter data in the database.
     * 
     * @param connection database Connection.
     * @param accountId database Account ID.
     * @param paramName name of the planned parameter of the Account.
     * @param updatedValues new values of the planned parameter mapped to the 
     * planning Periods dates.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean executeUpdate(Connection connection, String accountId,
            String paramName, Map<String, String> updatedValues);

    /**
     * Selects and returns planned parameter values of a given Account mapped 
     * to the planning Periods dates from database.
     * 
     * @param connection database Connection.
     * @param id database Account ID.
     * @param paramName name of the planned parameter of the Account.
     * @return planned parameter values of a given Account mapped to the 
     * planning Periods dates from database.
     */
    public TreeMap<String, Double> selectPlannedAccountsValuesById(Connection 
            connection, Integer id, String paramName);
    
    /**
     * Updates planned and calculated parameters of all the Accounts in the 
     * database based on values from Account calculation class objects 
     * contained in the list that is returned by 
     * {@link ejb.calculation.AccountsHandlerLocal#getEntityAccountList()}
     * 
     * @param connection database Connection
     * @param inputPlanningPeriodsFrequency planning Periods Frequency.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean executeUpdateAll(Connection connection,
            String inputPlanningPeriodsFrequency);
    
    /**
     * Sets current Period in the database table that contains Accounts plan.
     * 
     * @param connection database Connection
     * @param date the date of the new current planning Period in ISO8601 
     * format.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean setCurrentPeriodDate(Connection connection, String date);
}
