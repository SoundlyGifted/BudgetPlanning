
package ejb.calculation;

import java.sql.Connection;
import java.util.ArrayList;
import jakarta.ejb.Local;

/**
 * EJB AccountsHandler Local interface contains methods to perform 
 * operations on EntityAccount objects in the EntityAccountList collection.
 */
@Local
public interface AccountsHandlerLocal {

    /**
     * Removes EntityAccount element from the EntityAccountList collection.
     * 
     * @param entity EntityAccount element to be removed.
     */
    public void removeFromEntityAccountList(EntityAccount entity);

    /**
     * Gets collection of EntityAccount elements (EntityAccountList).
     * 
     * @return EntityAccountList collection.
     */
    public ArrayList<EntityAccount> getEntityAccountList();

    /**
     * Actualizes EntityAccountList (replaces it with the list obtained from
     * the database).
     * 
     * @param connection database Connection.
     * @return EntityAccountList obtained based on the database records.
     */
    public ArrayList<EntityAccount> 
        actualizeEntityAccountList(Connection connection);

    /**
     * Prepares the corresponding EntityAccount object (calculates values of 
     * it's parameters based on the current data in the database) based on the
     * given database Expense ID (with check of it's link to a certain Account).
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @param inputExpenseId given database Expense ID to check for the linked
     * Account and making corresponding EntityAccount object preparation.
     * @return prepared EntityAccount object (with calculated parameters) if
     * Expense with given ID has any linked Account, or null otherwise.
     */
    public EntityAccount prepareEntityAccountByExpenseId(Connection connection,
            String inputPlanningPeriodsFrequency, Integer inputExpenseId);

    /**
     * Prepares EntityAccount object (calculates values of it's parameters 
     * based on the current data in the database) based on the given database 
     * Account ID.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @param id database Account ID.
     * @return prepared EntityAccount object (with calculated parameters).
     */
    public EntityAccount prepareEntityAccountById(Connection connection,
            String inputPlanningPeriodsFrequency, Integer id); 

    /**
     * Method is applied to operation of shifting of Current Planning Period
     * towards increasing of period date on one period and re-calculates
     * "Current Remainder (CUR)" attributes of all Accounts currently present
     * in the database based on domain specific formula.
     * 
     * @param connection database Connection.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean calculateAllCurrentRemainderCurForNextPeriod(Connection 
            connection);
    
    /**
     * Method is applied to operation of shifting of Current Planning Period
     * towards decreasing of period date on one period and re-calculates
     * "Current Remainder (CUR)" attributes of all Accounts currently present
     * in the database based on domain specific formula.
     * 
     * @param connection database Connection.
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public boolean
            calculateAllCurrentRemainderCurForPreviousPeriod(Connection 
                    connection);
}
