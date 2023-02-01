
package com.ejb.MainScreen;

import com.ejb.calculation.EntityExpense;
import com.ejb.calculation.ExpensesHandlerLocal;
import com.ejb.calculation.TimePeriodsHandlerLocal;
import com.ejb.common.SQLAbstract;
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
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

/**
 * EJB PlannedVariableParamsSQL is used to perform operations on planned and
 * calculated parameters data of the Expenses in the database.
 */
@Stateless
public class PlannedVariableParamsSQL extends SQLAbstract 
        implements PlannedVariableParamsSQLLocal {

    @EJB
    private ExpensesHandlerLocal expensesHandler;
    
    @EJB
    private TimePeriodsHandlerLocal timePeriods;
      
    /**
     * {@inheritDoc} 
     */
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
                if (valueDouble == null || valueDouble < 0) {
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

    /**
     * {@inheritDoc} 
     */    
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
 
    /**
     * {@inheritDoc} 
     */    
    @Override
    public boolean setCurrentPeriodDate(Connection connection, String date) {
        
        Statement statementClearCurrentPeriodFlag = null;
        String clearCurrentPeriodFlag = "update PLANNED_VARIABLE_PARAMS "
                + "set CURPFL = ''";        

        PreparedStatement psSelectPlannedParamsByDate;
        PreparedStatement psSelectAllFromExpensesStructure;
        PreparedStatement psSetCurrentPeriodFlagByDate;
        PreparedStatement psInsertRecordsForGivenDate;
        try {
            statementClearCurrentPeriodFlag = connection.createStatement();
            
            psSelectPlannedParamsByDate = createPreparedStatement(connection, 
                    "mainScreen/select.plannedParams.byDate");
            psSelectPlannedParamsByDate.setString(1, date);
            
            psSelectAllFromExpensesStructure 
                    = createPreparedStatement(connection, 
                            "expensesStructure/select.all");
            
            psSetCurrentPeriodFlagByDate = createPreparedStatement(connection, 
                    "mainScreen/update.plannedExpenses.setCurrentPeriodFlag"
                            + ".byDate");
            
            psInsertRecordsForGivenDate = createPreparedStatement(connection, 
                    "mainScreen/insert.allExpensesPlannedParams.byid");
            
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL : "
                    + "setCurrentPeriodDate() error while creating statements: " 
                    + ex.getMessage());
            return false;
        }

        try(ResultSet resultSet = psSelectPlannedParamsByDate.executeQuery()) {
            if (resultSet.next()) {
                // Setting Current Period Flag for the given date if there is
                // a planning data in the database for at least one Expense
                // Category for this date.
                statementClearCurrentPeriodFlag
                        .execute(clearCurrentPeriodFlag);
                psSetCurrentPeriodFlagByDate.setString(1, date);
                psSetCurrentPeriodFlagByDate.executeUpdate();
            } else {
                // If there is no planning data in the database for any of the
                // Expense Categories then inserting zero-plan for each of the
                // Expense Categories of "SIMPLE_EXPENSES" and "GOODS" type.
                try(ResultSet rsSelectAllExpenses 
                        = psSelectAllFromExpensesStructure.executeQuery()) {
                    while (rsSelectAllExpenses.next()) {
                        String type = rsSelectAllExpenses.getString("TYPE");
                        if (type.equals("SIMPLE_EXPENSES") 
                                || type.equals("GOODS")) {
                            int id = rsSelectAllExpenses.getInt("ID");
                            String name = rsSelectAllExpenses.getString("NAME");

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
                            psInsertRecordsForGivenDate.setDouble(12, 0);
                            psInsertRecordsForGivenDate.setDouble(13, 0);
                            psInsertRecordsForGivenDate.setDouble(14, 0);
                            psInsertRecordsForGivenDate.setDouble(15, 0);
                            psInsertRecordsForGivenDate.setDouble(16, 0);
                            psInsertRecordsForGivenDate.setDouble(17, 0);
                            psInsertRecordsForGivenDate.setDouble(18, 0);
                            psInsertRecordsForGivenDate.setDouble(19, 0);
                            psInsertRecordsForGivenDate.setDouble(20, 0);
                            psInsertRecordsForGivenDate.setDouble(21, 0);
                            psInsertRecordsForGivenDate.setString(22, "Y");

                            psInsertRecordsForGivenDate.addBatch();
                        }
                    }
                    statementClearCurrentPeriodFlag
                        .execute(clearCurrentPeriodFlag);
                    psInsertRecordsForGivenDate.executeBatch();
                }
            }
        } catch (SQLException ex) {
            System.out.println("***PlannedVariableParamsSQL: "
                    + "setCurrentPeriodDate() Error while executing "
                    + "Query: " + ex.getMessage() + "***");
            return false;
        } finally {
            clear(psSelectPlannedParamsByDate);
            clear(psSelectAllFromExpensesStructure);
            clear(psSetCurrentPeriodFlagByDate);
            clear(psInsertRecordsForGivenDate);
        }
        return true;
    }

    /**
     * {@inheritDoc} 
     */    
    @Override
    public TreeMap<String, Double> selectPlannedExpensesById(Connection 
            connection, Integer id) {
        if (id == null || id < 1) {
            return null;
        }

        TreeMap<String, Double> plannedExpense = new TreeMap<>();
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "mainScreen/select.plannedExpenses.byExpenseId");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "selectPlannedExpensesById() SQL PreparedStatement "
                    + "failure: " + ex.getMessage() + " ***");
            return null;
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String type = resultSet.getString("TYPE");
                String key = resultSet.getString("DATE");
                Double value = (double) 0;
                if (type.equals("SIMPLE_EXPENSES") 
                        || type.equals("COMPLEX_EXPENSES")) {
                    value = resultSet.getDouble("PLANNED_CUR");
                } else if (type.equals("GOODS")) {
                    value = resultSet.getDouble("PLANNED_PCS");
                }
                plannedExpense.put(key, value);
            }
        } catch (SQLException ex) {
            System.out.println("***PlannedVariableParamsSQL: "
                    + "selectPlannedExpensesById() Error while executing Select "
                    + "Query: " + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
        return plannedExpense;
    }

    /**
     * {@inheritDoc} 
     */    
    @Override
    public TreeMap<String, Double> selectConsumptionPcsById(Connection 
            connection, Integer id) {
        if (id == null || id < 1) {
            return null;
        }

        TreeMap<String, Double> consumptionPcs = new TreeMap<>();
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "mainScreen/select.consumptionPcs.byExpenseId");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "selectConsumptionPcsById() SQL PreparedStatement "
                    + "failure: " + ex.getMessage() + " ***");
            return null;
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String key = resultSet.getString("DATE");
                Double value = resultSet.getDouble("CONSUMPTION_PCS");
                consumptionPcs.put(key, value);
            }
        } catch (SQLException ex) {
            System.out.println("***PlannedVariableParamsSQL: "
                    + "selectConsumptionPcsById() Error while executing Select "
                    + "Query: " + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
        return consumptionPcs;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public TreeMap<String, Double> selectDifferencePcsById(Connection 
            connection, Integer id) {
        if (id == null || id < 1) {
            return null;
        }

        TreeMap<String, Double> consumptionPcs = new TreeMap<>();
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "mainScreen/select.differencePcs.byExpenseId");
            preparedStatement.setInt(1, id);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "selectDifferencePcsById() SQL PreparedStatement "
                    + "failure: " + ex.getMessage() + " ***");
            return null;
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String key = resultSet.getString("DATE");
                Double value = resultSet.getDouble("DIFFERENCE_PCS");
                consumptionPcs.put(key, value);
            }
        } catch (SQLException ex) {
            System.out.println("***PlannedVariableParamsSQL: "
                    + "selectDifferencePcsById() Error while executing Select "
                    + "Query: " + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
        return consumptionPcs;
    }    

    /**
     * {@inheritDoc} 
     */    
    @Override
    public TreeMap<String, Double> 
        selectPlannedExpAndDiffCurSumByAcctIdAndDate(Connection connection, 
                Integer accountId, String date) {
        if (accountId == null || accountId < 1 || !inputCheckNullBlank(date)) {
            return null;
        }

        TreeMap<String, Double> result = new TreeMap<>();
        Double plannedCurSum;
        Double differenceCurSum;
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "mainScreen/select"
                            + ".plannedExpAndDiffCurSum.byAcctIdAndDate");
            preparedStatement.setInt(1, accountId);
            preparedStatement.setString(2, date);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "selectPlannedExpAndDiffCurSumByAcctIdAndDate() SQL "
                    + "PreparedStatement failure: " + ex.getMessage() + " ***");
            return null;
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                plannedCurSum = resultSet.getDouble("PLANNED_CUR");
                differenceCurSum = resultSet.getDouble("DIFFERENCE_CUR");
                result.put("PLANNED_CUR", plannedCurSum);
                result.put("DIFFERENCE_CUR", differenceCurSum);
            }
        } catch (SQLException ex) {
            System.out.println("***PlannedVariableParamsSQL: "
                    + "selectPlannedExpAndDiffCurSumByAcctIdAndDate() Error "
                    + "while executing Select Query: " 
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
        return result;            
    }    

    /**
     * {@inheritDoc} 
     */        
    @Override
    public TreeMap<String, Double> 
        selectPlannedExpCurSumByAcctId(Connection connection, 
                Integer accountId) {
        if (accountId == null || accountId < 1) {
            return null;
        }

        TreeMap<String, Double> result = new TreeMap<>();
        String date;
        Double plannedCurSumVal;
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "mainScreen/select.plannedExpCurSum.byAcctId");
            preparedStatement.setInt(1, accountId);
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL: "
                    + "selectPlannedExpCurSumByAcctId() SQL "
                    + "PreparedStatement failure: " + ex.getMessage() + " ***");
            return null;
        }
        
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                date = resultSet.getString("DATE");
                plannedCurSumVal = resultSet.getDouble("PLANNED_CUR");
                result.put(date, plannedCurSumVal);
            }
        } catch (SQLException ex) {
            System.out.println("***PlannedVariableParamsSQL: "
                    + "selectPlannedExpCurSumByAcctId() Error "
                    + "while executing Select Query: " 
                    + ex.getMessage() + "***");
            return null;
        } finally {
            clear(preparedStatement);
        }
        return result;            
    }

    /**
     * {@inheritDoc} 
     */        
    @Override
    public boolean executeUpdateAll(Connection connection,
            String inputPlanningPeriodsFrequency) {

        // List of expense categories with calculated variable parameters
        // that need to be updated in the database.
        // Only updating records for those expenses in the database that
        // have parameters calculated. If parameters are null then doing
        // nothing. Set all calculated parameters to null after the database 
        // update.
        ArrayList<EntityExpense> list = expensesHandler.getEntityExpenseList();

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
                    "mainScreen/delete.allExpensesPlannedParams.byid");
            preparedStatementInsert = createPreparedStatement(connection,
                    "mainScreen/insert.allExpensesPlannedParams.byid");
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL - "
                    + "executeUpdateAll(): SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }

        // Performing old records delition for the Expenses that contain 
        // calculated variable parameters.
        try {
            //Setting Query Parameters and executing Query;
            for (EntityExpense expense : list) {
                if (expense.isCalculated()) {
                    int id = expense.getId();
                    preparedStatementDelete.setInt(1, id);
                    preparedStatementDelete.setString(2, currentPeriodDate);
                    preparedStatementDelete.addBatch();
                }
            }
            preparedStatementDelete.executeBatch();
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL "
                    + "- executeUpdateAll(): Error while setting query "
                    + "parameters or executing Update Query: "
                    + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatementDelete);
        }

        // Performing new records insertion for the Expenses that contain
        // calculated variable parameters.
        try {
            //Setting Query Parameters and executing Query;
            for (EntityExpense expense : list) {
                if (expense.isCalculated()) {
                    // Constant and Common Fixed Expense parameters for the DB 
                    // record.
                    int id = expense.getId();
                    String name = expense.getName();
                    // Variable Expense parameters for the DB record.
                    TreeMap<String, Double> plannedCur
                            = expense.getPlannedCur();
                    TreeMap<String, Double> actualCur
                            = expense.getActualCur();
                    TreeMap<String, Double> differenceCur
                            = expense.getDifferenceCur();
                    TreeMap<String, Double> consumptionPcs
                            = expense.getConsumptionPcs();
                    TreeMap<String, Double> consumptionCur
                            = expense.getConsumptionCur();
                    TreeMap<String, Double> stockPcs
                            = expense.getStockPcs();
                    TreeMap<String, Double> stockCur
                            = expense.getStockCur();
                    TreeMap<String, Double> requirementPcs
                            = expense.getRequirementPcs();
                    TreeMap<String, Double> requirementCur
                            = expense.getRequirementCur();
                    TreeMap<String, Double> plannedPcs
                            = expense.getPlannedPcs();
                    TreeMap<String, Double> actualPcs
                            = expense.getActualPcs();
                    TreeMap<String, Double> differencePcs
                            = expense.getDifferencePcs();
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

                        Double plannedCurVal = (double) 0;
                        if (plannedCur != null) {
                            plannedCurVal = plannedCur.get(date);
                            if (plannedCurVal == null) {
                                plannedCurVal = (double) 0;
                            }
                        }
                        
                        Double actualCurVal = (double) 0;
                        if (actualCur != null) {
                            actualCurVal = actualCur.get(date);
                            if (actualCurVal == null) {
                                actualCurVal = (double) 0;
                            }                            
                        }

                        Double differenceCurVal = (double) 0;
                        if (differenceCur != null) {
                            differenceCurVal = differenceCur.get(date);
                            if (differenceCurVal == null) {
                                differenceCurVal = (double) 0;
                            }                            
                        }

                        Double consumptionPcsVal = (double) 0;
                        if (consumptionPcs != null) {
                            consumptionPcsVal = consumptionPcs.get(date);
                            if (consumptionPcsVal == null) {
                                consumptionPcsVal = (double) 0;
                            }                            
                        }

                        Double consumptionCurVal = (double) 0;
                        if (consumptionCur != null) {
                            consumptionCurVal = consumptionCur.get(date);
                            if (consumptionCurVal == null) {
                                consumptionCurVal = (double) 0;
                            }                            
                        }

                        Double stockPcsVal = (double) 0;
                        if (stockPcs != null) {
                            stockPcsVal = stockPcs.get(date);
                            if (stockPcsVal == null) {
                                stockPcsVal = (double) 0;
                            }                            
                        }

                        Double stockCurVal = (double) 0;
                        if (stockCur != null) {
                            stockCurVal = stockCur.get(date);
                            if (stockCurVal == null) {
                                stockCurVal = (double) 0;
                            }                            
                        }
                        
                        Double requirementPcsVal = (double) 0;
                        if (requirementPcs != null) {
                            requirementPcsVal = requirementPcs.get(date);
                            if (requirementPcsVal == null) {
                                requirementPcsVal = (double) 0;
                            }                            
                        }

                        Double requirementCurVal = (double) 0;
                        if (requirementCur != null) {
                            requirementCurVal = requirementCur.get(date);
                            if (requirementCurVal == null) {
                                requirementCurVal = (double) 0;
                            }                            
                        }

                        Double plannedPcsVal = (double) 0;
                        if (plannedPcs != null) {
                            plannedPcsVal = plannedPcs.get(date);
                            if (plannedPcsVal == null) {
                                plannedPcsVal = (double) 0;
                            }                            
                        }

                        Double actualPcsVal = (double) 0;
                        if (actualPcs != null) {
                            actualPcsVal = actualPcs.get(date);
                            if (actualPcsVal == null) {
                                actualPcsVal = (double) 0;
                            }                            
                        }

                        Double differencePcsVal = (double) 0;
                        if (differencePcs != null) {
                            differencePcsVal = differencePcs.get(date);
                            if (differencePcsVal == null) {
                                differencePcsVal = (double) 0;
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
                        preparedStatementInsert.setDouble(10, plannedPcsVal);
                        preparedStatementInsert.setDouble(11, plannedCurVal);
                        preparedStatementInsert.setDouble(12, actualPcsVal);
                        preparedStatementInsert.setDouble(13, actualCurVal);
                        preparedStatementInsert.setDouble(14, differencePcsVal);
                        preparedStatementInsert.setDouble(15, differenceCurVal);
                        preparedStatementInsert.setDouble(16,
                                consumptionPcsVal);
                        preparedStatementInsert.setDouble(17,
                                consumptionCurVal);
                        preparedStatementInsert.setDouble(18, stockPcsVal);
                        preparedStatementInsert.setDouble(19, stockCurVal);
                        preparedStatementInsert.setDouble(20,
                                requirementPcsVal);
                        preparedStatementInsert.setDouble(21,
                                requirementCurVal);
                        preparedStatementInsert.setString(22, curpfl);

                        preparedStatementInsert.addBatch();
                    }
                }
            }
            preparedStatementInsert.executeBatch();
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL "
                    + "- executeUpdateAll(): Error while setting query "
                    + "parameters or executing Update Query: "
                    + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatementInsert);
        }

        // Clearing variable parameters for the Expenses with calculated
        // variable parameters.
        for (EntityExpense expense : list) {
            if (expense.isCalculated()) {
                expense.resetVariableParams();
            }
        }
        return true;
    }

    /**
     * {@inheritDoc} 
     */    
    @Override
    public boolean executeDeleteByExpenseId(Connection connection, String id) {
        /* Checking of input values. */
        Integer idInt = stringToInt(id);
        if (idInt == null) {
            return false;
        }
        
        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection, 
                    "mainScreen/delete.allExpensesPlannedParams.byid");       
        } catch (SQLException | IOException ex) {
            System.out.println("*** PlannedVariableParamsSQL "
                    + "- executeDeleteByExpenseId(): SQL PreparedStatement "
                    + "failure: " + ex.getMessage() + " ***");
            return false;
        }
        try {
            //Setting Query Parameters and executing Query;
            preparedStatement.setInt(1, idInt);
            preparedStatement.setString(2, "1970-01-01");
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("*** PlannedVariableParamsSQL "
                    + "- executeDeleteByExpenseId(): Error while setting query "
                    + "parameters or executing Update Query: " 
                    + ex.getMessage() + " ***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }
}
