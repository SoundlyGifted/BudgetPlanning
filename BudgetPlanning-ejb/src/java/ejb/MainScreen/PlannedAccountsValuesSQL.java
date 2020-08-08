
package ejb.MainScreen;

import ejb.calculation.AccountsHandlerLocal;
import ejb.calculation.EntityAccount;
import ejb.calculation.TimePeriodsHandlerLocal;
import ejb.common.SQLAbstract;
import java.io.IOException;
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
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class PlannedAccountsValuesSQL extends SQLAbstract 
        implements PlannedAccountsValuesSQLLocal {

    @EJB
    private AccountsHandlerLocal accountsHandler;
    
    @EJB
    private TimePeriodsHandlerLocal timePeriods;
    
    @Override
    public boolean executeUpdate(Connection connection, String accountId, 
            String paramName, Map<String, String> updatedValues) {
        /* Checking of input values. */
        if (stringToInt(accountId) == null) {
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
                if (valueDouble == null || valueDouble < 0) {
                    valueDouble = (double) 0;
                }
            }
            updatedValuesDouble.put(date, valueDouble);
        }
        
        int accountIdInt = stringToInt(accountId);
        
        PreparedStatement preparedStatement;
        try {
            switch (paramName) {
                case "PLANNED_INCOME_CUR" : 
                    preparedStatement = createPreparedStatement(connection,
                    "mainScreen/update.incomeCur");
                    break;
                default : 
                    return false;
            }
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedAccountsValuesSQL "
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
                preparedStatement.setInt(2, accountIdInt);
                preparedStatement.setString(3, date);

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException ex) {
            System.out.println("*** PlannedAccountsValuesSQL "
                    + "- executeUpdate(): Error while setting query parameters "
                    + "or executing Update Query: " + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }
    
    @Override
    public TreeMap<String, Double> selectPlannedAccountsValuesById(Connection 
            connection, Integer id, String paramName) {
        if (id == null || id < 1) {
            return null;
        }

        TreeMap<String, Double> accountParamValues = new TreeMap<>();
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "mainScreen/select.accountPlannedVarParams.byid");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedAccountsValuesSQL: "
                    + "selectPlannedVariableParamsById() SQL PreparedStatement "
                    + "failure: " + ex.getMessage() + " ***");
            return null;
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
        } catch (SQLException ex) {
            System.out.println("***PlannedAccountsValuesSQL: "
                    + "selectPlannedVariableParamsById() Error while executing "
                    + "Select Query: " + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
        return accountParamValues;
    }
    
    @Override
    public boolean executeUpdateAll(Connection connection,
            String inputPlanningPeriodsFrequency) {

        // List of accounts with calculated variable parameters
        // that need to be updated in the database.
        // Only updating records for those accounts in the database that
        // have parameters calculated. If parameters are null then doing
        // nothing. Set all calculated parameters to null after the database 
        // update.
        ArrayList<EntityAccount> list = accountsHandler.getEntityAccountList();

        // Obtain current set of Time Period Dates.
        // It won't be calculated again if the value for a given frequency 
        // already exists in TimePeriods class.
        TreeSet<String> timePeriodDates = timePeriods
                .calculateTimePeriodDates(connection,
                        inputPlanningPeriodsFrequency);
        String currentPeriodDate = timePeriodDates.first();

        PreparedStatement preparedStatementDelete;
        PreparedStatement preparedStatementInsert;
        try {
            preparedStatementDelete = createPreparedStatement(connection,
                    "mainScreen/delete.allAccountsPlannedParams.byid");
            preparedStatementInsert = createPreparedStatement(connection,
                    "mainScreen/insert.allAccountsPlannedParams.byid");
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedAccountsValuesSQL - "
                    + "executeUpdateAll(): SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        // Performing old records delition for the Accounts that contain 
        // calculated variable parameters.
        try {
            //Setting Query Parameters and executing Query;
            for (EntityAccount account : list) {
                if (account.isCalculated()) {
                    int id = account.getId();
                    preparedStatementDelete.setInt(1, id);
                    preparedStatementDelete.setString(2, currentPeriodDate);
                    preparedStatementDelete.addBatch();
                }
            }
            preparedStatementDelete.executeBatch();
        } catch (SQLException ex) {
            System.out.println("*** PlannedAccountsValuesSQL "
                    + "- executeUpdateAll(): Error while setting query "
                    + "parameters or executing Update Query: "
                    + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatementDelete);
        }

        // Performing new records insertion for the Accounts that contain
        // calculated variable parameters.
        try {
            //Setting Query Parameters and executing Query;
            for (EntityAccount account : list) {
                if (account.isCalculated()) {
                    // Constant and Common Fixed Account parameters for the DB 
                    // record.
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
                            plannedRemainderCurVal = plannedRemainderCur.get(date);
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
        } catch (SQLException ex) {
            System.out.println("*** PlannedAccountsValuesSQL "
                    + "- executeUpdateAll(): Error while setting query "
                    + "parameters or executing Update Query: "
                    + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatementInsert);
        }

        // Clearing variable parameters for the Accounts with calculated
        // variable parameters.
        for (EntityAccount account : list) {
            if (account.isCalculated()) {
                account.resetVariableParams();
            }
        }
        return true;
    }
    
    @Override
    public boolean setCurrentPeriodDate(Connection connection, String date) {
        
        Statement statementClearCurrentPeriodFlag = null;
        String clearCurrentPeriodFlag = "update PLANNED_ACCOUNTS_VALUES "
                + "set CURPFL = ''";        

        PreparedStatement psSelectPlannedAccountsByDate;
        PreparedStatement psSelectAllFromAccountsStructure;
        PreparedStatement psSetCurrentPeriodFlagByDate;
        PreparedStatement psInsertRecordsForGivenDate;
        try {
            statementClearCurrentPeriodFlag = connection.createStatement();
            
            psSelectPlannedAccountsByDate = createPreparedStatement(connection, 
                    "mainScreen/select.plannedAccounts.byDate");
            psSelectPlannedAccountsByDate.setString(1, date);
            
            psSelectAllFromAccountsStructure 
                    = createPreparedStatement(connection, 
                            "accountsStructure/select.all");
            
            psSetCurrentPeriodFlagByDate = createPreparedStatement(connection, 
                    "mainScreen/update.plannedAccounts.setCurrentPeriodFlag"
                            + ".byDate");
            
            psInsertRecordsForGivenDate = createPreparedStatement(connection, 
                    "mainScreen/insert.allAccountsPlannedParams.byid");
            
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedAccountsValuesSQL : "
                    + "setCurrentPeriodDate() error while creating statements: " 
                    + ex.getMessage());
            return false;
        }

        try(ResultSet resultSet = psSelectPlannedAccountsByDate
                .executeQuery()) {
            if (resultSet.next()) {
                // Setting Current Period Flag for the given date if there is
                // a planning data in the database for at least one Account
                // for this date.
                statementClearCurrentPeriodFlag
                        .execute(clearCurrentPeriodFlag);
                psSetCurrentPeriodFlagByDate.setString(1, date);
                psSetCurrentPeriodFlagByDate.executeUpdate();
            } else {
                // If there is no planning data in the database for any of the
                // Accounts then inserting zero-plan for each of the
                // Account.
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
        } catch (SQLException ex) {
            System.out.println("***PlannedAccountsValuesSQL: "
                    + "setCurrentPeriodDate() Error while executing "
                    + "Query: " + ex.getMessage() + "***");
            return false;
        } finally {
            clear(psSelectPlannedAccountsByDate);
            clear(psSelectAllFromAccountsStructure);
            clear(psSetCurrentPeriodFlagByDate);
            clear(psInsertRecordsForGivenDate);
        }
        return true;
    }
}
