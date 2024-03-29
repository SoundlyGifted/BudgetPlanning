
package com.ejb.calculation;

import com.ejb.mainscreen.PlannedAccountsValuesSQLLocal;
import com.ejb.mainscreen.PlannedVariableParamsSQLLocal;
import com.ejb.accstructure.AccountsStructureSQLLocal;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import com.ejb.expstructure.ExpensesStructureSQLSelectLocal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

/**
 * EJB AccountsHandler is used to perform operations on EntityAccount objects 
 * in the EntityAccountList collection.
 */
@Singleton
@Startup
public class AccountsHandler implements AccountsHandlerLocal {

    @EJB
    private AccountsStructureSQLLocal accountsStructureSQL;
    
    @EJB
    private ExpensesStructureSQLSelectLocal expensesStructureSQLSelect;

    @EJB
    private PlannedVariableParamsSQLLocal plannedExpenses;
    
    @EJB
    private PlannedAccountsValuesSQLLocal plannedAccounts;
    
    @EJB
    private TimePeriodsHandlerLocal timePeriods;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromEntityAccountList(EntityAccount entity) {
        EntityAccountList.getEntityAccountList().remove(entity);
    }
    
    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public ArrayList<EntityAccount> actualizeEntityAccountList(Connection 
            connection) throws GenericDBOperationException, GenericDBException {
        ArrayList<EntityAccount> accountListDB = 
                accountsStructureSQL.executeSelectAll(connection);
        replaceEntityAccountList(accountListDB);
        return accountListDB;
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public EntityAccount prepareEntityAccountByExpenseId(Connection connection,
            String inputPlanningPeriodsFrequency, Integer inputExpenseId) 
            throws GenericDBOperationException, GenericDBException {
        if (inputExpenseId == null || inputExpenseId < 1) {
            return null;
        }

        // Time Period Dates.
        // Calculates or returns timePeriodDates from TimePeriods class.
        TreeSet<String> timePeriodDates = timePeriods
                .calculateTimePeriodDates(connection, 
                        inputPlanningPeriodsFrequency); 
        
        // EntityAccountList (where calculation objects stored).
        ArrayList<EntityAccount> list = EntityAccountList
                .getEntityAccountList();

        /* Map with complete list of Account IDs and Maps of fixed planning 
         * parameters and their values from DB.
         */
        HashMap<Integer, HashMap<String, Double>> valuesMap =
                accountsStructureSQL.executeSelectAllValues(connection);        
        
        /* Selecting all links of all Expenses (links to Complex Expenses and 
         * links to Accounts).
         */
        HashMap<Integer, HashMap<String, Integer>> allLinks 
                = expensesStructureSQLSelect.executeSelectAllLinks(connection);
        // Links of input Expense (expense id).
        HashMap<String, Integer> links = allLinks.get(inputExpenseId);

        int linkedAccountId = links.get("ACCOUNT_ID");
        /* Checking if this Expense is not linked to any Account.
         * In such case checking if the Expense is linked to any Complex Expense
         * and checking it's links to any Acccount respectively.
         */
        if (linkedAccountId == 0) {
            int linkedComplexExpenseId = links.get("LINKED_TO_COMPLEX_ID");
            if (linkedComplexExpenseId == 0) {
                // There is no Account linked to this Expense.
                return null;
            }
            /* Attempt to find linked Account for any of 
             * including Complex Expenses.
             */
            prepareEntityAccountByExpenseId(connection,
                    inputPlanningPeriodsFrequency, linkedComplexExpenseId);
        }

        /* Additional check of linked Account id for not-equality to zero to 
         * complete the current recursion branch.
         */
        if (linkedAccountId != 0) {
            /* Checking if EntityAccount with this linked account id exists in
             * EntityAccountList.
             */
            for (EntityAccount a : list) {
                if (linkedAccountId == a.getId()) {
                    /* Such EntityAccount exists in EntityAccountList.
                     * No need to create object again, just getting it from 
                     * there.
                     * Preparing Account for calculation.
                     */
                    a.setCurrentRemainderCur(valuesMap.get(linkedAccountId)
                            .get("CURRENT_REMAINDER_CUR"));
                    obtainChangeableVarParamsForEntityAccount(connection, a);
                    a.calculateVariableParameters(timePeriodDates);
                    return a;
                }
            }
            /* EntityAccount with such id does not exist in the 
             * EntityExpenseList so adding EntityAccount with this id to 
             * the EntityExpenseList based on the database record.
             */
            EntityAccount account = accountsStructureSQL
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

    /**
     * {@inheritDoc}
     */     
    @Override
    public EntityAccount prepareEntityAccountById(Connection connection,
            String inputPlanningPeriodsFrequency, Integer id) 
            throws GenericDBOperationException, GenericDBException {
        if (id == null || id < 1) {
            return null;
        }

        // EntityAccountList (where calculation objects stored).
        ArrayList<EntityAccount> list = EntityAccountList
                .getEntityAccountList();        
 
        // Time Period Dates.
        // Calculates or returns timePeriodDates from TimePeriods class.
        TreeSet<String> timePeriodDates = timePeriods
                .calculateTimePeriodDates(connection, 
                        inputPlanningPeriodsFrequency);         
        
        /* Map with complete list of Account IDs and Maps of fixed planning 
         * parameters and their values from DB.
         */
        HashMap<Integer, HashMap<String, Double>> valuesMap =
                accountsStructureSQL.executeSelectAllValues(connection);        
        
        // Checking if EntityAccount with given id exists in EntityAccountList.
        for (EntityAccount a : list) {
            if (id == a.getId()) {
                /* Such EntityAccount exists in EntityAccountList.
                 * No need to create object again, just getting it from 
                 * there.
                 * Preparing Account for calculation.
                 */
                a.setCurrentRemainderCur(valuesMap.get(id)
                        .get("CURRENT_REMAINDER_CUR"));
                obtainChangeableVarParamsForEntityAccount(connection, a);
                a.calculateVariableParameters(timePeriodDates);
                return a;
            }
        }
        /* EntityAccount with such id does not exist in the 
         * EntityExpenseList so adding EntityAccount with this id to 
         * the EntityExpenseList based on the database record.
         */
        EntityAccount account = accountsStructureSQL
                .executeSelectById(connection, id);
        // Preparing Account for calculation.
        account.setCurrentRemainderCur(valuesMap.get(id)
                .get("CURRENT_REMAINDER_CUR"));
        obtainChangeableVarParamsForEntityAccount(connection, account);
        account.calculateVariableParameters(timePeriodDates);        
        list.add(account);
        return account;
    }

    /**
     * Obtains changeable parameter values from the database into a given
     * EntityAccount object.
     * 
     * @param connection database Connection
     * @param account EntityAccount object the changeable parameter values to be
     * obtained into.
     */
    private void 
        obtainChangeableVarParamsForEntityAccount(Connection connection, 
                EntityAccount account) 
                throws GenericDBOperationException, GenericDBException {
            
        int id = account.getId();
        
        /* Getting Sum of Planned Expenses values (CUR) of all Expenses that 
         * are linked to the given Account.
         */
        TreeMap<String, Double> plannedSumCur = plannedExpenses
                .selectPlannedExpCurSumByAcctId(connection, id);
        
        // Getting planned income (CUR) for the Account.
        TreeMap<String, Double> incomeCurValues = plannedAccounts
                .selectPlannedAccountsValuesById(connection, id, 
                        "PLANNED_INCOME_CUR");
        
        /* Setting Planned parameters to the object of Account calculation 
         * class.
         */
        account.setPlannedSumCur(plannedSumCur);
        account.setIncomeCur(incomeCurValues);
    }

    /**
     * {@inheritDoc}
     */         
    @Override
    public void calculateAllCurrentRemainderCurForNextPeriod(Connection connection) 
            throws GenericDBOperationException, GenericDBException {  
        // The date of Current Period from the database.
        String currentPeriodDate = plannedExpenses
                .getCurrentPeriodDate(connection);
        
        // All Accounts user-changeable parameter values.
        HashMap<Integer, HashMap<String, Double>> accountsAllValues
                = accountsStructureSQL.executeSelectAllValues(connection);
        
        // Variable Identifier of an Account.
        Integer accountId;     

        // Variables for calculation of Accounts Current Remainders.
        Double currentRemainderCur;
        TreeMap<String, Double> incomeCur;
        TreeMap<String, Double> linkedExpPlannedParamValues;   
        Double incomeCurVal;
        Double plannedCurSum;
        Double differenceCurSum;
        
        for (Map.Entry<Integer, HashMap<String, Double>> entryAccount
                : accountsAllValues.entrySet()) {
            accountId = entryAccount.getKey();
            currentRemainderCur = entryAccount.getValue()
                    .get("CURRENT_REMAINDER_CUR");

            incomeCur = plannedAccounts
                    .selectPlannedAccountsValuesById(connection, accountId,
                            "PLANNED_INCOME_CUR");
            if (incomeCur == null || incomeCur.isEmpty()) {
                incomeCurVal = (double) 0;
            } else {
                incomeCurVal = incomeCur.get(currentPeriodDate);
                if (incomeCurVal == null) {
                    incomeCurVal = (double) 0;
                }
            }
            
            linkedExpPlannedParamValues = plannedExpenses
                    .selectPlannedExpAndDiffCurSumByAcctIdAndDate(connection, 
                            accountId, currentPeriodDate);
            if (linkedExpPlannedParamValues == null 
                    || linkedExpPlannedParamValues.isEmpty()) {
                plannedCurSum = (double) 0;
                differenceCurSum = (double) 0;
            } else {
                plannedCurSum = linkedExpPlannedParamValues.get("PLANNED_CUR");
                differenceCurSum = linkedExpPlannedParamValues
                        .get("DIFFERENCE_CUR");                
            }
            
            /* Recalculating Current Remainder value for the Next Planning 
             * Period.
             */
            currentRemainderCur = currentRemainderCur - plannedCurSum 
                    + incomeCurVal - differenceCurSum;
            
            // Now write to the database.
            accountsStructureSQL.updateCurrentRemainderById(connection, 
                    accountId, currentRemainderCur);
        }
    }   

    /**
     * {@inheritDoc}
     */            
    @Override
    public void calculateAllCurrentRemainderCurForPreviousPeriod(Connection connection) 
            throws GenericDBOperationException, GenericDBException {
        // The date of Current Period from the database.
        String currentPeriodDate = plannedExpenses
                .getCurrentPeriodDate(connection);
        
        // All Accounts user-changeable parameter values.
        HashMap<Integer, HashMap<String, Double>> accountsAllValues
                = accountsStructureSQL.executeSelectAllValues(connection);
        
        // Variable Identifier of an Account.
        Integer accountId;

        // Variables for calculation of Accounts Current Remainders.
        Double currentRemainderCur;
        TreeMap<String, Double> incomeCur;
        TreeMap<String, Double> linkedExpPlannedParamValues;   
        Double incomeCurVal;
        Double plannedCurSum;
        Double differenceCurSum;
        
        for (Map.Entry<Integer, HashMap<String, Double>> entryAccount
                : accountsAllValues.entrySet()) {
            accountId = entryAccount.getKey();
            currentRemainderCur = entryAccount.getValue()
                    .get("CURRENT_REMAINDER_CUR");

            incomeCur = plannedAccounts
                    .selectPlannedAccountsValuesById(connection, accountId,
                            "PLANNED_INCOME_CUR");
            if (incomeCur == null || incomeCur.isEmpty()) {
                incomeCurVal = (double) 0;
            } else {
                incomeCurVal = incomeCur.get(currentPeriodDate);
                if (incomeCurVal == null) {
                    incomeCurVal = (double) 0;
                }
            }
            
            linkedExpPlannedParamValues = plannedExpenses
                    .selectPlannedExpAndDiffCurSumByAcctIdAndDate(connection, 
                            accountId, currentPeriodDate);
            if (linkedExpPlannedParamValues == null 
                    || linkedExpPlannedParamValues.isEmpty()) {
                plannedCurSum = (double) 0;
                differenceCurSum = (double) 0;
            } else {
                plannedCurSum = linkedExpPlannedParamValues.get("PLANNED_CUR");
                differenceCurSum = linkedExpPlannedParamValues
                        .get("DIFFERENCE_CUR");                
            }

            /* Recalculating Current Remainder value for the Previous Planning 
             * Period.
             */
            currentRemainderCur = currentRemainderCur + plannedCurSum 
                    - incomeCurVal + differenceCurSum;
            
            // Now write to the database.
            accountsStructureSQL.updateCurrentRemainderById(connection, 
                    accountId, currentRemainderCur);
        }
    }
}
