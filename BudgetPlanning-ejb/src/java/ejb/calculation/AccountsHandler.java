
package ejb.calculation;

import ejb.MainScreen.PlannedAccountsValuesSQLLocal;
import ejb.MainScreen.PlannedVariableParamsSQLLocal;
import ejb.accountsStructure.AccountsStructureSQLLocal;
import ejb.expensesStructure.ExpensesStructureSQLSelectLocal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 *
 * @author SoundlyGifted
 */
@Singleton
public class AccountsHandler implements AccountsHandlerLocal {

    @EJB
    private AccountsStructureSQLLocal aSelect;
    
    @EJB
    private ExpensesStructureSQLSelectLocal eSelect;

    @EJB
    private PlannedVariableParamsSQLLocal plannedExpenses;
    
    @EJB
    private PlannedAccountsValuesSQLLocal plannedAccounts;
    
    @EJB
    private TimePeriodsHandlerLocal timePeriods;
    
    /**
     * Method to remove an EntityAccount element from the EntityAccountList
     * collection
     * 
     * @param entity EntityAccount element to be removed.
     */
    @Override
    public void removeFromEntityAccountList(EntityAccount entity) {
        EntityAccountList.getEntityAccountList().remove(entity);
    }
    
    /**
     * Gets collection of EntityAccount elements (EntityAccountList) for 
     * further usage (calculation of parameters).
     * 
     * @return EntityAccountList collection.
     */
    @Override
    public ArrayList<EntityAccount> getEntityAccountList() {
        return EntityAccountList.getEntityAccountList();
    }
    
    /**
     * Replaces current collection of EntityAccount elements (EntityAccountList)
     * with the given EntityAccount list.
     * 
     * @param list EntityAccount list given for replacement.
     */
    private void replaceEntityAccountList(ArrayList<EntityAccount> list) {
        // Clear current expenses list.
        EntityAccountList.removeEntityAccountList(); 
        // Write to common Entity Objects List.
        EntityAccountList.setEntityAccountList(list); 
    }
    
    /**
     * Actualizes EntityAccountList (replaces it with the list obtained from
     * the database).
     * 
     * @param connection database connection.
     * @return EntityAccountList obtained based on the database records.
     */
    @Override
    public ArrayList<EntityAccount> actualizeEntityAccountList(Connection 
            connection) {
        ArrayList<EntityAccount> accountListDB = 
                aSelect.executeSelectAll(connection);
        replaceEntityAccountList(accountListDB);
        return accountListDB;
    }

    @Override
    public EntityAccount prepareEntityAccountByExpenseId(Connection connection,
            String inputPlanningPeriodsFrequency, Integer inputExpenseId) {
        if (inputExpenseId == null || inputExpenseId < 1) {
            return null;
        }

        /* Time Period Dates. */
        // calculates or returns timePeriodDates from TimePeriods class.
        TreeSet<String> timePeriodDates = timePeriods
                .calculateTimePeriodDates(connection, 
                        inputPlanningPeriodsFrequency); 
        
        //EntityAccountList (where calculation objects stored).
        ArrayList<EntityAccount> list = EntityAccountList
                .getEntityAccountList();

        //Map with complete list of Account IDs and Maps of fixed
        //planning parameters and their values from DB.
        HashMap<Integer, HashMap<String, Double>> valuesMap =
                aSelect.executeSelectAllValues(connection);        
        
        // Selecting all links of all Expenses 
        // (links to Complex Expenses and links to Accounts).
        HashMap<Integer, HashMap<String, Integer>> allLinks = eSelect
                .executeSelectAllLinks(connection);
        // Links of input Expense (expense id).
        HashMap<String, Integer> links = allLinks.get(inputExpenseId);

        int linkedAccountId = links.get("ACCOUNT_ID");
        // Checking if this Expense is not linked to any Account.
        // In such case checking if the Expense is linked to any Complex Expense
        // and checking it's links to any Acccount respectively.
        if (linkedAccountId == 0) {
            int linkedComplexExpenseId = links.get("LINKED_TO_COMPLEX_ID");
            if (linkedComplexExpenseId == 0) {
                // There is no Account linked to this Expense.
                return null;
            }
            // Attempt to find linked Account for any of 
            // including Complex Expenses.
            prepareEntityAccountByExpenseId(connection,
                    inputPlanningPeriodsFrequency, linkedComplexExpenseId);
        }

        // Additional check of linked Account id for not-equality to zero to 
        // complete the current recursion branch.
        if (linkedAccountId != 0) {
            // Checking if EntityAccount with this linked account id exists in
            // EntityAccountList.
            for (EntityAccount a : list) {
                if (linkedAccountId == a.getId()) {
                    // Such EntityAccount exists in EntityAccountList.
                    // No need to create object again, just getting it from 
                    // there.
                    // Preparing Account for calculation.
                    a.setCurrentRemainderCur(valuesMap.get(linkedAccountId)
                            .get("CURRENT_REMAINDER_CUR"));
                    obtainChangeableVarParamsForEntityAccount(connection, a);
                    a.calculateVariableParameters(timePeriodDates);
                    return a;
                }
            }
            // EntityAccount with such id does not exist in the 
            // EntityExpenseList so adding EntityAccount with this id to 
            // the EntityExpenseList based on the database record.
            EntityAccount account = aSelect
                    .executeSelectById(connection, linkedAccountId);
            // Preparing Account for calculation.
            account.setCurrentRemainderCur(valuesMap.get(linkedAccountId)
                            .get("CURRENT_REMAINDER_CUR"));
            obtainChangeableVarParamsForEntityAccount(connection, account);
            account.calculateVariableParameters(timePeriodDates);
            list.add(account);
            return account;            
        } else {
            // There is no Account linked to this Expense.
            return null;
        }
    }
    
    @Override
    public EntityAccount prepareEntityAccountById(Connection connection,
            String inputPlanningPeriodsFrequency, Integer id) {
        if (id == null || id < 1) {
            return null;
        }

        //EntityAccountList (where calculation objects stored).
        ArrayList<EntityAccount> list = EntityAccountList
                .getEntityAccountList();        
 
        /* Time Period Dates. */
        // calculates or returns timePeriodDates from TimePeriods class.
        TreeSet<String> timePeriodDates = timePeriods
                .calculateTimePeriodDates(connection, 
                        inputPlanningPeriodsFrequency);         
        
        //Map with complete list of Account IDs and Maps of fixed
        //planning parameters and their values from DB.
        HashMap<Integer, HashMap<String, Double>> valuesMap =
                aSelect.executeSelectAllValues(connection);        
        
        // Checking if EntityAccount with given id exists in
        // EntityAccountList.
        for (EntityAccount a : list) {
            if (id == a.getId()) {
                // Such EntityAccount exists in EntityAccountList.
                // No need to create object again, just getting it from 
                // there.
                // Preparing Account for calculation.
                a.setCurrentRemainderCur(valuesMap.get(id)
                        .get("CURRENT_REMAINDER_CUR"));
                obtainChangeableVarParamsForEntityAccount(connection, a);
                a.calculateVariableParameters(timePeriodDates);
                return a;
            }
        }
        // EntityAccount with such id does not exist in the 
        // EntityExpenseList so adding EntityAccount with this id to 
        // the EntityExpenseList based on the database record.
        EntityAccount account = aSelect
                .executeSelectById(connection, id);
        // Preparing Account for calculation.
        account.setCurrentRemainderCur(valuesMap.get(id)
                .get("CURRENT_REMAINDER_CUR"));
        obtainChangeableVarParamsForEntityAccount(connection, account);
        account.calculateVariableParameters(timePeriodDates);        
        list.add(account);
        return account;
    }
    
    private void 
        obtainChangeableVarParamsForEntityAccount(Connection connection, 
                EntityAccount account) {
              
        // Selecting all links of all Expenses 
        // (links to Complex Expenses and links to Accounts).
        HashMap<Integer, HashMap<String, Integer>> allLinks = eSelect
                .executeSelectAllLinks(connection);
        
        int id = account.getId();
        
        // Array to collect IDs of Expenses that are linked to the given 
        // Account.
        ArrayList<Integer> expensesIDsWithAcctLinkedList = new ArrayList<>();
        
        for (Map.Entry<Integer, HashMap<String, Integer>> entry 
                : allLinks.entrySet()) {
            HashMap<String, Integer> links = entry.getValue();
            int linkedAccountId = links.get("ACCOUNT_ID");
            if (linkedAccountId == id) {
                expensesIDsWithAcctLinkedList.add(entry.getKey());
            }
        }
        
        // Getting planned expenses (CUR) for each Expense that is linked to
        // given Account.
        TreeMap<String, Double> plannedSumCur = new TreeMap<>();
        TreeMap<String, Double> plannedCurValues;
        for (Integer expenseId : expensesIDsWithAcctLinkedList) {
            plannedCurValues = plannedExpenses
                    .selectPlannedExpensesById(connection, expenseId);
            if (plannedCurValues != null) {
                if (plannedSumCur.isEmpty()) {
                    plannedSumCur = plannedCurValues;
                } else {
                    for (Map.Entry<String, Double> entry 
                            : plannedSumCur.entrySet()) {
                        String key = entry.getKey();
                        Double value1 = entry.getValue();
                        if (value1 == null) {
                            value1 = (double) 0;
                        }
                        Double value2 = plannedCurValues.get(key);
                        if (value2 == null) {
                            value2 = (double) 0;
                        }
                        Double sum = value1 + value2;
                        plannedSumCur.put(key, sum);
                    }
                }                
            }
        }

        // Getting planned income (CUR) for the Account.
        TreeMap<String, Double> incomeCurValues = plannedAccounts
                .selectPlannedAccountsValuesById(connection, id, 
                        "PLANNED_INCOME_CUR");
        
        account.setPlannedSumCur(plannedSumCur);
        account.setIncomeCur(incomeCurValues);
    }    
    
    
}
