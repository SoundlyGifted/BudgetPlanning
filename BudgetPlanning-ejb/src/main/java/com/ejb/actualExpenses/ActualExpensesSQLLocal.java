
package com.ejb.actualExpenses;

import java.sql.Connection;
import java.util.TreeMap;
import java.util.TreeSet;
import jakarta.ejb.Local;

/**
 * EJB ActualExpensesSQL Local interface contains methods to perform 
 * operations on Actual Expenses records in the database.
 */
@Local
public interface ActualExpensesSQLLocal {

    /**
     * Inserts record of Actual Expense into the database
     * 
     * @param connection database Connection.
     * @param date String date of Actual Expense in ISO8601 format.
     * @param expenseName name of Expense from the database. 
     * @param expenseTitle expense title.
     * @param shopName name of shop or supplier.
     * @param price price of actual purchase.
     * @param qty quantity purchased.
     * @param comment comment about Actual Expense.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean executeInsert(Connection connection, String date,
            String expenseName, String expenseTitle, String shopName,
            String price, String qty, String comment);

    /**
     * Updates record of Actual Expense in the database.
     * 
     * @param connection database Connection.
     * @param idForUpdate Actual Expense database record ID to be updated.
     * @param date String date of Actual Expense in ISO8601 format.
     * @param expenseName name of Expense from the database. 
     * @param expenseTitle expense title.
     * @param shopName name of shop or supplier.
     * @param price price of actual purchase.
     * @param qty quantity purchased.
     * @param comment comment about Actual Expense.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean executeUpdate(Connection connection, String idForUpdate,
            String date, String expenseName, String expenseTitle,
            String shopName, String price, String qty, String comment);

    /**
     * Deletes record of Actual Expense from the database.
     * 
     * @param connection database Connection.
     * @param id Actual Expense database record ID to be deleted.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean executeDelete(Connection connection, String id);

    /**
     * For the given set of planning time periods calculates values of Actual 
     * Expenses mapped to each of the planning time period date for a certain
     * Expense.
     * 
     * @param connection database Connection.
     * @param timePeriodDates set of planning time period dates.
     * @param planningPeriodsFrequency frequency of the planning time periods.
     * @param expenseId database Expense ID to calculate Actual Expenses for.
     * @return values of Actual Expenses mapped to each of the planning time 
     * period date.
     */
    public TreeMap<String, Double> calculateActualExpenses(Connection connection,
            TreeSet<String> timePeriodDates, String planningPeriodsFrequency,
            Integer expenseId);
    /**
     * Sets Expense in the database to the "deleted" state (Expense ID becomes
     * equal "-1" which is a reserved database value of Expense ID for the 
     * deleted Actual Expenses).
     * 
     * @param connection database Connection.
     * @param id database Expense ID.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean setExpenseToDeleted (Connection connection, String id);
    
    /**
     * Recovers Actual Expense in the database from the "deleted" state by 
     * setting the appropriate Expense ID.
     * 
     * @param connection database Connection.
     * @param expenseId database Expense ID to be set for the Actual Expense 
     * in the "deleted" state with given expenseName.
     * @param expenseName Expense name used to find necessary Actual Expense in
     * the "deleted" state in the database.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean recoverDeletedExpenseId (Connection connection, 
            Integer expenseId, String expenseName);
}
