
package ejb.MainScreen;

import java.sql.Connection;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.Local;

/**
 * EJB PlannedVariableParamsSQL Local interface contains methods to perform 
 * operations on planned and calculated parameters data of the Expenses in the 
 * database.
 */
@Local
public interface PlannedVariableParamsSQLLocal {

    /**
     * Updates Expense's planned parameter data in the database.
     * 
     * @param connection database Connection.
     * @param expenseId database Expense ID.
     * @param paramName name of the planned parameter of the Expense.
     * @param updatedValues new values of the planned parameter mapped to the 
     * planning Periods dates.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean executeUpdate(Connection connection, String expenseId,
            String paramName, Map<String, String> updatedValues);

    /**
     * Gets current Period from the database table that contains Expenses plan.
     * 
     * @param connection database Connection.
     * @return the date of the current planning Period in ISO8601 format.
     */
    public String getCurrentPeriodDate(Connection connection);

    /**
     * Sets current Period in the database table that contains Expenses plan.
     * 
     * @param connection database Connection.
     * @param date the date of the new current planning Period in ISO8601 
     * format.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean setCurrentPeriodDate(Connection connection, String date);
    
    /**
     * Selects and returns planned Expenses parameter values of a given Expense 
     * mapped to the planning Periods dates from database.
     * 
     * @param connection database Connection.
     * @param id database Expense ID.
     * @return planned Expenses parameter values of a given Expense mapped to 
     * the planning Periods dates from database.
     */
    public TreeMap<String, Double> selectPlannedExpensesById(Connection 
            connection, Integer id);
    
    /**
     * Selects and returns Consumption planned parameter values of a given 
     * Expense mapped to the planning Periods dates from database.
     * 
     * @param connection database Connection.
     * @param id database Expense ID.
     * @return Consumption planned parameter values of a given Expense mapped to
     * the planning Periods dates from database.
     */
    public TreeMap<String, Double> selectConsumptionPcsById(Connection 
            connection, Integer id);
    
    /**
     * Selects and returns "Actual - Planned" expense Difference planned 
     * parameter values of a given Expense mapped to the planning Periods dates 
     * from database.
     * 
     * @param connection database Connection.
     * @param id database Expense ID.
     * @return "Actual - Planned" expense Difference planned parameter values of 
     * a given Expense mapped to the planning Periods dates from database.
     */
    public TreeMap<String, Double> selectDifferencePcsById(Connection 
            connection, Integer id);
    
    /**
     * Selects from database planned Expenses and "Actual - Planned" expense 
     * Difference parameters summed up for the Expenses that are linked to the 
     * Account with given ID for a given planning Period date.
     * 
     * @param connection database Connection.
     * @param accountId database Account ID.
     * @param date date in ISO8601 format for the parameters selection.
     * @return planned Expenses and "Actual - Planned" expense 
     * Difference parameters summed up for the Expenses that are linked to the 
     * Account with given ID for a given planning Period date. The values of 
     * both parameters are mapped to the corresponding database columns names.
     */
    public TreeMap<String, Double> 
        selectPlannedExpAndDiffCurSumByAcctIdAndDate(Connection connection, 
                Integer accountId, String date);

    /**
     * Selects from database planned Expenses parameter summed up for the 
     * Expenses that are linked to the Account with given ID.
     * 
     * @param connection database Connection.
     * @param accountId database Account ID.
     * @return planned Expenses parameter summed up for the Expenses that are 
     * linked to the Account with given ID.
     */
    public TreeMap<String, Double> 
        selectPlannedExpCurSumByAcctId(Connection connection, 
                Integer accountId);        

    /**
     * Updates planned and calculated parameters of all the Expenses in the 
     * database based on values from Expense calculation class objects 
     * contained in the list that is returned by 
     * {@link ejb.calculation.ExpensesHandlerLocal#getEntityExpenseList()}
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency planning Periods Frequency.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean executeUpdateAll(Connection connection, 
            String inputPlanningPeriodsFrequency);
    
    /**
     * Deletes all Expense plan (planned and calculated parameters values) from
     * the database for the given Expense ID.
     * 
     * @param connection database Connection.
     * @param id database Expense ID.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean executeDeleteByExpenseId(Connection connection, String id);
}
