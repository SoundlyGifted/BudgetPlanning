
package com.ejb.actualexpenses;

import com.ejb.common.SQLAbstract;
import com.ejb.calculation.EntityExpense;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import com.ejb.expstructure.ExpensesStructureSQLSelectLocal;
import com.ejb.expstructure.ExpensesTypes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.TreeSet;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

/**
 * EJB ActualExpensesSQL is used to perform operations on Actual Expenses 
 * records in the database.
 */
@Stateless
public class ActualExpensesSQL extends SQLAbstract 
        implements ActualExpensesSQLLocal {

    @EJB
    private ExpensesStructureSQLSelectLocal select;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void executeInsert(Connection connection, String date, 
            String expenseName, String expenseTitle, String shopName, 
            String price, String qty, String comment) 
            throws GenericDBOperationException, GenericDBException {
        // Checking of input values.
        if (!inputCheckNullBlank(date) || !inputCheckNullBlank(expenseName)
                || !inputCheckNullBlank(price) || stringToDouble(price) == null
                || !inputCheckLength(expenseName) 
                || !inputCheckLength(expenseTitle) 
                || !inputCheckLength(shopName) || !inputCheckLength(comment)) {
            throw new GenericDBOperationException("Unable to add the actual "
                    + "expense, empty or wrong parameters provided.");
        }
        
        String week;
        int dayN;
        String dayC;
        int monthN;
        String monthC;
        int year;
        int expenseIdInt;
        double priceDouble = stringToDouble(price);
        double qtyDouble;
        double costDouble;
        
        /* Checking if Expense with given name exists in the database 
         * (EXPENSES_STRUCTURE table). If exists:
         * 1) getting Expense ID. 
         * 2) checking it's type (only SIMPLE_EXPENSES and GOODS can be 
         * populated in the ACTUAL_EXPENSES table).
         */
        EntityExpense expenseGiven = select.executeSelectByName(connection, expenseName);
        if (expenseGiven == null) {
            throw new GenericDBOperationException("Unable to add the actual "
                    + "expense, the provided Expense '" + expenseName
                    + "' is not present in the Expenses Structure database "
                    + "table.");
        } else {
            expenseIdInt = expenseGiven.getId();
            String type = expenseGiven.getType();
            if (!ExpensesTypes.ExpenseType.SIMPLE_EXPENSES.getType()
                    .equals(type) && !ExpensesTypes.ExpenseType.GOODS.getType()
                            .equals(type)) {
                throw new GenericDBOperationException("Unable to add the actual "
                        + "expense, the provided Expense '" + expenseName
                        + "' is not a Simple Expense or Good.");
            }
        }
        
        // Setting QTY to 1 if user did not enter QTY.
        if (qty == null || qty.trim().isEmpty()) {
            qtyDouble = (double) 1;
        } else {
            qtyDouble = stringToDouble(qty);
        }
        
        costDouble = round(priceDouble * qtyDouble, 2);

        // Calculating time parameters.
        String[] partOfDate = date.split("\\-");
        dayN = Integer.parseInt(partOfDate[2]);
        monthN = Integer.parseInt(partOfDate[1]);
        year = Integer.parseInt(partOfDate[0]);
        
        Calendar c = Calendar.getInstance();
        c.set(year, monthN-1, dayN);
        dayC = getDay(c.get(Calendar.DAY_OF_WEEK));
        monthC = getMonth(monthN);
        week = getWeek(c.get(Calendar.WEEK_OF_YEAR));
        
        try (PreparedStatement preparedStatement 
                = createPreparedStatement(connection, "actualExpenses/insert")) {
            // Setting Query Parameters and executing Query.
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, week);
            preparedStatement.setInt(3, dayN);
            preparedStatement.setString(4, dayC);
            preparedStatement.setInt(5, monthN);
            preparedStatement.setString(6, monthC);
            preparedStatement.setInt(7, year);
            preparedStatement.setInt(8, expenseIdInt);
            preparedStatement.setString(9, expenseName);
            preparedStatement.setString(10, expenseTitle);
            preparedStatement.setString(11, shopName);
            preparedStatement.setDouble(12, priceDouble);
            preparedStatement.setDouble(13, qtyDouble);
            preparedStatement.setDouble(14, costDouble);
            preparedStatement.setString(15, comment);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeUpdate(Connection connection, String idForUpdate, 
            String date, String expenseName, String expenseTitle, String shopName, 
            String price, String qty, String comment) 
            throws GenericDBOperationException, GenericDBException {
        // Checking of input values.
        if (stringToInt(idForUpdate) == null || !inputCheckNullBlank(date) 
                || !inputCheckNullBlank(expenseName)
                || !inputCheckNullBlank(price) || stringToDouble(price) == null
                || !inputCheckLength(expenseName) 
                || !inputCheckLength(expenseTitle) 
                || !inputCheckLength(shopName) || !inputCheckLength(comment)) {
            throw new GenericDBOperationException("Unable to update the actual "
                    + "expense, empty or wrong parameters provided.");
        }        
        
        String week;
        int dayN;
        String dayC;
        int monthN;
        String monthC;
        int year;
        int expenseIdInt;
        double priceDouble = stringToDouble(price);
        double qtyDouble;
        double costDouble;
        int idForUpdateInt = stringToInt(idForUpdate);
        
        /* Checking if Expense with given name exists in the database 
         * (EXPENSES_STRUCTURE table). If exists:
         * 1) getting Expense ID. 
         * 2) checking it's type (only SIMPLE_EXPENSES and GOODS can be populated 
         * in ACTUAL_EXPENSES table.
         */
        EntityExpense expenseGiven = select.executeSelectByName(connection, expenseName);
        if (expenseGiven == null) {
            throw new GenericDBOperationException("Unable to add the actual "
                    + "expense, the provided Expense '" + expenseName
                    + "' is not present in the Expenses Structure database "
                    + "table.");
        } else {
            expenseIdInt = expenseGiven.getId();
            String type = expenseGiven.getType();
            if (!ExpensesTypes.ExpenseType.SIMPLE_EXPENSES.getType()
                    .equals(type) && !ExpensesTypes.ExpenseType.GOODS.getType()
                            .equals(type)) {
                throw new GenericDBOperationException("Unable to add the actual "
                        + "expense, the provided Expense '" + expenseName
                        + "' is not a Simple Expense or Good.");
            }
        }

        // Setting QTY to 1 if user did not enter QTY.
        if (qty == null || qty.trim().isEmpty()) {
            qtyDouble = (double) 1;
        } else {
            qtyDouble = stringToDouble(qty);
        }        
        
        costDouble = round(priceDouble * qtyDouble, 2);

        // Calculating time parameters.
        String[] partOfDate = date.split("\\-");
        dayN = Integer.parseInt(partOfDate[2]);
        monthN = Integer.parseInt(partOfDate[1]);
        year = Integer.parseInt(partOfDate[0]);
        
        Calendar c = Calendar.getInstance();
        c.set(year, monthN-1, dayN);
        dayC = getDay(c.get(Calendar.DAY_OF_WEEK));
        monthC = getMonth(monthN);
        week = getWeek(c.get(Calendar.WEEK_OF_YEAR));
        
        try (PreparedStatement preparedStatement 
                = createPreparedStatement(connection, "actualExpenses/update")) {
            // Setting Query Parameters and executing Query.
            preparedStatement.setString(1, date);
            preparedStatement.setString(2, week);
            preparedStatement.setInt(3, dayN);
            preparedStatement.setString(4, dayC);
            preparedStatement.setInt(5, monthN);
            preparedStatement.setString(6, monthC);
            preparedStatement.setInt(7, year);
            preparedStatement.setInt(8, expenseIdInt);
            preparedStatement.setString(9, expenseName);
            preparedStatement.setString(10, expenseTitle);
            preparedStatement.setString(11, shopName);
            preparedStatement.setDouble(12, priceDouble);
            preparedStatement.setDouble(13, qtyDouble);
            preparedStatement.setDouble(14, costDouble);
            preparedStatement.setString(15, comment);
            preparedStatement.setInt(16, idForUpdateInt);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }       
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void executeDelete(Connection connection, String id) 
            throws GenericDBOperationException, GenericDBException {
        if (stringToInt(id) == null) {
            throw new GenericDBOperationException("Unable to delete the actual "
                    + "expense, empty or wrong record id '" + id + "' provided.");
        }
        int idInt = stringToInt(id);
        
        try (PreparedStatement preparedStatement 
                = createPreparedStatement(connection, "actualExpenses/delete")) {
            // Setting Query Parameters and executing Query.
            preparedStatement.setInt(1, idInt);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public TreeMap<String, Double> calculateActualExpenses(Connection 
            connection, TreeSet<String> timePeriodDates, 
            String planningPeriodsFrequency, Integer expenseId) 
            throws GenericDBOperationException, GenericDBException {
        if (!inputCheckFrequency(planningPeriodsFrequency)) {
            throw new GenericDBOperationException("Unable to calculate values "
                    + "of actual expenses for Expense ID '" + expenseId + "' "
                    + "for each of the Planning time Period: provided Planning "
                    + "Periods frequency '" + planningPeriodsFrequency + "' is "
                    + "invalid.");
        }
        
        TreeMap<String, Double> result = new TreeMap<>();
        
        Calendar c = Calendar.getInstance();
        
        String currentPeriodDate = timePeriodDates.first();
        
        String[] partOfDate;
        int dayN;
        int monthN;
        int year;
        int weekN;
        
        partOfDate = currentPeriodDate.split("\\-");
        dayN = Integer.parseInt(partOfDate[2]);
        monthN = Integer.parseInt(partOfDate[1]);
        year = Integer.parseInt(partOfDate[0]);
        c.set(year, monthN-1, dayN);
        weekN = c.get(Calendar.WEEK_OF_YEAR);
        
        PreparedStatement preparedStatement = null;
        try {
            switch (planningPeriodsFrequency) {
                case "W":
                    preparedStatement = createPreparedStatement(connection,
                            "actualExpenses/select.calculateActualExpenses"
                            + ".byExpenseId.weekly");
                    preparedStatement.setInt(1, expenseId);
                    preparedStatement.setInt(2, weekN);
                    break;
                case "M":
                    preparedStatement = createPreparedStatement(connection,
                            "actualExpenses/select.calculateActualExpenses"
                            + ".byExpenseId.monthly");
                    preparedStatement.setInt(1, expenseId);
                    preparedStatement.setInt(2, monthN);
                    break;
                case "D":
                    preparedStatement = createPreparedStatement(connection,
                            "actualExpenses/select.calculateActualExpenses"
                            + ".byExpenseId.daily");
                    preparedStatement.setInt(1, expenseId);
                    preparedStatement.setString(2, currentPeriodDate);
                    break;
                default:
                    throw new GenericDBOperationException("Unable to calculate "
                            + "values of actual expenses for Expense ID '" 
                            + expenseId + "' for each of the Planning time "
                            + "Period: provided Planning Periods frequency '" 
                            + planningPeriodsFrequency + "' is invalid.");
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    /* Defining which value (in PCS or CUR) extract from the 
                     * resultSet based on the Expense type.
                     */
                    Double value = (double) 0;
                    String type = resultSet.getString("TYPE");
                    if (type.equals(ExpensesTypes.ExpenseType.SIMPLE_EXPENSES
                            .getType())) {
                        value = resultSet.getDouble("ACTUAL_CUR");
                    } else if (type.equals(ExpensesTypes.ExpenseType.GOODS
                            .getType())) {
                        value = resultSet.getDouble("ACTUAL_PCS");
                    }

                    /* Loop over time period dates.
                     * Put into result the resultSet actual expense values that
                     * match the corresponding time period based on the input
                     * frequency.
                     */
                    for (String date : timePeriodDates) {
                        partOfDate = date.split("\\-");
                        dayN = Integer.parseInt(partOfDate[2]);
                        monthN = Integer.parseInt(partOfDate[1]);
                        year = Integer.parseInt(partOfDate[0]);
                        c.set(year, monthN - 1, dayN);
                        weekN = c.get(Calendar.WEEK_OF_YEAR);
                        switch (planningPeriodsFrequency) {
                            case "W":
                                if (weekN == resultSet.getInt("WEEK")) {
                                    result.put(date, value);
                                } else {
                                    result.put(date, (double) 0);
                                }
                                break;
                            case "M":
                                if (monthN == resultSet.getInt("MONTH_N")) {
                                    result.put(date, value);
                                } else {
                                    result.put(date, (double) 0);
                                }
                                break;
                            case "D":
                                if (date.equals(resultSet.getString("DATE"))) {
                                    result.put(date, value);
                                } else {
                                    result.put(date, (double) 0);
                                }
                                break;
                            default:
                                throw new GenericDBOperationException("Unable "
                                        + "to calculate values of actual "
                                        + "expenses for Expense ID '"
                                        + expenseId + "' for each of the "
                                        + "Planning time Period: provided "
                                        + "Planning Periods frequency '"
                                        + planningPeriodsFrequency + "' is "
                                        + "invalid.");
                        }
                    }
                }
            } catch (SQLException sqlex) {
                throw new GenericDBOperationException(sqlex.getMessage() == null
                        ? "" : sqlex.getMessage(), sqlex);
            }
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null
                    ? "" : sqlex.getMessage(), sqlex);
        } finally {
            clear(preparedStatement);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void setExpenseToDeleted (Connection connection, String id) 
            throws GenericDBOperationException, GenericDBException {
        if (stringToInt(id) == null) {
            throw new GenericDBOperationException("Unable to set Expense to "
                    + "deleted state in the Actual Expenses database table, "
                    + "provided Expense ID '" + id + "' is invalid.");
        }
        int idInt = stringToInt(id);

        try (PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "actualExpenses/update.setExpenseToDeleted")) {
            // Setting Query Parameters and executing Query.
            preparedStatement.setInt(1, idInt);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null
                    ? "" : sqlex.getMessage(), sqlex);
        }    
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void recoverDeletedExpenseId (Connection connection, 
            Integer expenseId, String expenseName) 
            throws GenericDBOperationException, GenericDBException {
        if (expenseId == null || !inputCheckNullBlank(expenseName)) {
            throw new GenericDBOperationException("Unable to recover Expense "
                    + "from deleted state in the Actual Expenses database "
                    + "table, inalid input parameter(s) provided.");
        }

        try (PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "actualExpenses/update.recoverDeletedExpenseId")) {
            //Setting Query Parameters and executing Query;
                preparedStatement.setInt(1, expenseId);
                preparedStatement.setString(2, expenseName);
                preparedStatement.executeUpdate();
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null
                    ? "" : sqlex.getMessage(), sqlex);
        } 
    }
}
