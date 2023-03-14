
package com.ejb.mainscreen;

import com.ejb.calculation.AccountsHandlerLocal;
import com.ejb.calculation.EntityAccount;
import com.ejb.calculation.TimePeriodsHandlerLocal;
import com.ejb.common.SQLAbstract;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

/**
 * EJB PlannedAccountsValuesSQL is used to perform operations on planned and
 * calculated parameters data of the Accounts in the database.
 */
@Stateless
public class PlannedAccountsValuesSQL extends SQLAbstract 
        implements PlannedAccountsValuesSQLLocal {

    @EJB
    private AccountsHandlerLocal accountsHandler;
    
    @EJB
    private TimePeriodsHandlerLocal timePeriods;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void executeUpdate(Connection connection, String accountId, 
            String paramName, Map<String, String> updatedValues) 
            throws GenericDBOperationException, GenericDBException {
        // Checking of input values.
        if (stringToInt(accountId) == null) {
            throw new GenericDBOperationException("Unable to update Account's "
                    + "planned parameter data in the database, provided "
                    + "Account ID '" + accountId + "' is invalid.");
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
        switch (paramName) {
            case "PLANNED_INCOME_CUR":
                preparedStatement = createPreparedStatement(connection,
                        "mainScreen/update.incomeCur");
                break;
            default:
                throw new GenericDBOperationException("Wrong name '" + paramName
                        + "' of the planned parameter of the Account provided. "
                        + "'PLANNED_INCOME_CUR' is supported only.");
        }
        
        try {
            // Setting Query Parameters and executing Query.
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
    public TreeMap<String, Double> selectPlannedAccountsValuesById(Connection 
            connection, Integer id, String paramName) 
            throws GenericDBOperationException, GenericDBException {
        if (id == null || id < 1) {
            return null;
        }

        TreeMap<String, Double> accountParamValues = new TreeMap<>();
        
        PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "mainScreen/select.accountPlannedVarParams.byid");
        try {
            preparedStatement.setInt(1, id);
        } catch (SQLException sqlex) {
            clear(preparedStatement);
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
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
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
        }
        return accountParamValues;
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void executeUpdateAll(Connection connection,
            String inputPlanningPeriodsFrequency) 
            throws GenericDBOperationException, GenericDBException {
        /* List of accounts with calculated variable parameters
         * that need to be updated in the database.
         * Only updating records for those accounts in the database that
         * have parameters calculated. If parameters are null then doing
         * nothing. Set all calculated parameters to null after the database 
         * update.
         */
        ArrayList<EntityAccount> list = accountsHandler.getEntityAccountList();

        /* Obtain current set of Time Period Dates.
         * It won't be calculated again if the value for a given frequency 
         * already exists in TimePeriods class.
         */
        TreeSet<String> timePeriodDates = timePeriods
                .calculateTimePeriodDates(connection,
                        inputPlanningPeriodsFrequency);
        String currentPeriodDate = timePeriodDates.first();

        /* Performing old records delition for the Accounts that contain 
         * calculated variable parameters.
         */
        try (PreparedStatement preparedStatementDelete 
                = createPreparedStatement(connection, 
                        "mainScreen/delete.allAccountsPlannedParams.byid")) {
            // Setting Query Parameters and executing Query.
            for (EntityAccount account : list) {
                if (account.isCalculated()) {
                    int id = account.getId();
                    preparedStatementDelete.setInt(1, id);
                    preparedStatementDelete.setString(2, currentPeriodDate);
                    preparedStatementDelete.addBatch();
                }
            }
            preparedStatementDelete.executeBatch();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }

        /* Performing new records insertion for the Accounts that contain
         * calculated variable parameters.
         */
        try (PreparedStatement preparedStatementInsert 
                = createPreparedStatement(connection, 
                        "mainScreen/insert.allAccountsPlannedParams.byid")) {
            // Setting Query Parameters and executing Query.
            for (EntityAccount account : list) {
                if (account.isCalculated()) {
                    /* Constant and Common Fixed Account parameters for the DB 
                     * record.
                     */
                    int id = account.getId();
                    String name = account.getName();
                    // Variable Account parameters for the DB record.
                    TreeMap<String, Double> plannedRemainderCur
                            = account.getPlannedRemainderCur();
                    TreeMap<String, Double> incomeCur
                            = account.getIncomeCur();
                    for (String date : timePeriodDates) {
                        // Timing variables.
                        String curpfl = "";
                        if (date.equals(currentPeriodDate)) {
                            curpfl = "Y";
                        }
                        String week;
                        int dayN;
                        String dayC;
                        int monthN;
                        String monthC;
                        int year;

                        String[] partOfDate = date.split("\\-");
                        dayN = Integer.parseInt(partOfDate[2]);
                        monthN = Integer.parseInt(partOfDate[1]);
                        year = Integer.parseInt(partOfDate[0]);

                        Calendar c = Calendar.getInstance();
                        c.set(year, monthN - 1, dayN);
                        dayC = getDay(c.get(Calendar.DAY_OF_WEEK));
                        monthC = getMonth(monthN);
                        week = getWeek(c.get(Calendar.WEEK_OF_YEAR));

                        Double plannedRemainderCurVal = (double) 0;
                        if (plannedRemainderCur != null) {
                            plannedRemainderCurVal = plannedRemainderCur
                                    .get(date);
                            if (plannedRemainderCurVal == null) {
                                plannedRemainderCurVal = (double) 0;
                            }
                        }
                        
                        Double incomeCurVal = (double) 0;
                        if (incomeCur != null) {
                            incomeCurVal = incomeCur.get(date);
                            if (incomeCurVal == null) {
                                incomeCurVal = (double) 0;
                            }                            
                        }
                        
                        preparedStatementInsert.setString(1, date);
                        preparedStatementInsert.setString(2, week);
                        preparedStatementInsert.setInt(3, dayN);
                        preparedStatementInsert.setString(4, dayC);
                        preparedStatementInsert.setInt(5, monthN);
                        preparedStatementInsert.setString(6, monthC);
                        preparedStatementInsert.setInt(7, year);
                        preparedStatementInsert.setInt(8, id);
                        preparedStatementInsert.setString(9, name);
                        preparedStatementInsert.setDouble(10, 
                                plannedRemainderCurVal);
                        preparedStatementInsert.setDouble(11, incomeCurVal);
                        preparedStatementInsert.setString(12, curpfl);

                        preparedStatementInsert.addBatch();
                    }
                }
            }
            preparedStatementInsert.executeBatch();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }

        /* Clearing variable parameters for the Accounts with calculated
         * variable parameters.
         */
        for (EntityAccount account : list) {
            if (account.isCalculated()) {
                account.resetVariableParams();
            }
        }
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void setCurrentPeriodDate(Connection connection, String date) 
            throws GenericDBOperationException, GenericDBException {
        
        Statement statementClearCurrentPeriodFlag;
        PreparedStatement psSelectPlannedAccountsByDate
                = createPreparedStatement(connection,
                        "mainScreen/select.plannedAccounts.byDate");      
        try {
            statementClearCurrentPeriodFlag = connection.createStatement();
            psSelectPlannedAccountsByDate.setString(1, date);      
        } catch (SQLException sqlex) {
            clear(psSelectPlannedAccountsByDate);
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
        String clearCurrentPeriodFlag = "update PLANNED_ACCOUNTS_VALUES "
                + "set CURPFL = ''";        

        PreparedStatement psSelectAllFromAccountsStructure 
                = createPreparedStatement(connection, 
                        "accountsStructure/select.all");
        PreparedStatement psSetCurrentPeriodFlagByDate 
                = createPreparedStatement(connection, 
                        "mainScreen/update.plannedAccounts.setCurrentPeriodFlag"
                                + ".byDate");
        PreparedStatement psInsertRecordsForGivenDate 
                = createPreparedStatement(connection, 
                        "mainScreen/insert.allAccountsPlannedParams.byid");

        try(ResultSet resultSet = psSelectPlannedAccountsByDate
                .executeQuery()) {
            if (resultSet.next()) {
                /* Setting Current Period Flag for the given date if there is
                 * a planning data in the database for at least one Account
                 * for this date.
                 */
                statementClearCurrentPeriodFlag
                        .execute(clearCurrentPeriodFlag);
                psSetCurrentPeriodFlagByDate.setString(1, date);
                psSetCurrentPeriodFlagByDate.executeUpdate();
            } else {
                /* If there is no planning data in the database for any of the
                 * Accounts then inserting zero-plan for each of the
                 * Account.
                 */
                try(ResultSet rsSelectAllAccounts 
                        = psSelectAllFromAccountsStructure.executeQuery()) {
                    while (rsSelectAllAccounts.next()) {
                            int id = rsSelectAllAccounts.getInt("ID");
                            String name = rsSelectAllAccounts.getString("NAME");

                            String week;
                            int dayN;
                            String dayC;
                            int monthN;
                            String monthC;
                            int year;

                            String[] partOfDate = date.split("\\-");
                            dayN = Integer.parseInt(partOfDate[2]);
                            monthN = Integer.parseInt(partOfDate[1]);
                            year = Integer.parseInt(partOfDate[0]);

                            Calendar c = Calendar.getInstance();
                            c.set(year, monthN - 1, dayN);
                            dayC = getDay(c.get(Calendar.DAY_OF_WEEK));
                            monthC = getMonth(monthN);
                            week = getWeek(c.get(Calendar.WEEK_OF_YEAR));

                            psInsertRecordsForGivenDate.setString(1, date);
                            psInsertRecordsForGivenDate.setString(2, week);
                            psInsertRecordsForGivenDate.setInt(3, dayN);
                            psInsertRecordsForGivenDate.setString(4, dayC);
                            psInsertRecordsForGivenDate.setInt(5, monthN);
                            psInsertRecordsForGivenDate.setString(6, monthC);
                            psInsertRecordsForGivenDate.setInt(7, year);
                            psInsertRecordsForGivenDate.setInt(8, id);
                            psInsertRecordsForGivenDate.setString(9, name);
                            psInsertRecordsForGivenDate.setDouble(10, 0);
                            psInsertRecordsForGivenDate.setDouble(11, 0);
                            psInsertRecordsForGivenDate.setString(12, "Y");

                            psInsertRecordsForGivenDate.addBatch();
                    }
                    statementClearCurrentPeriodFlag
                        .execute(clearCurrentPeriodFlag);
                    psInsertRecordsForGivenDate.executeBatch();
                }
            }
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(statementClearCurrentPeriodFlag);
            clear(psSelectPlannedAccountsByDate);
            clear(psSelectAllFromAccountsStructure);
            clear(psSetCurrentPeriodFlagByDate);
            clear(psInsertRecordsForGivenDate);
        }
    }
    
    @Override
    public void executeDeleteByAccountId(Connection connection, String id)
            throws GenericDBOperationException, GenericDBException {
        // Checking of input values.
        Integer idInt = stringToInt(id);
        if (idInt == null) {
            throw new GenericDBOperationException("Unable to delete all "
                    + "Account plan (planned and calculated parameter values) "
                    + "from the database, provided Account ID '" + id + "' is "
                    + "invalid.");
        }
        
        try (PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "mainScreen/delete.allAccountsPlannedParams.byid")) {
            // Setting Query Parameters and executing Query.
            preparedStatement.setInt(1, idInt);
            preparedStatement.setString(2, "1970-01-01");
            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
    }
}
