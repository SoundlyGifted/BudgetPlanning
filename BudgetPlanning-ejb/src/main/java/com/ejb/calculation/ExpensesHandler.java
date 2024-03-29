
package com.ejb.calculation;

import com.ejb.mainscreen.PlannedVariableParamsSQLLocal;
import com.ejb.actualexpenses.ActualExpensesSQLLocal;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import com.ejb.expstructure.ExpensesStructureSQLSelectLocal;
import com.ejb.expstructure.ExpensesStructureSQLUpdateLocal;
import com.ejb.expstructure.ExpensesTypes;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

/**
 * EJB ExpensesHandler is used to perform operations on EntityExpense objects 
 * in the EntityExpenseList collection.
 */
@Singleton
@Startup
public class ExpensesHandler implements ExpensesHandlerLocal, ExpensesTypes {

    @EJB
    private ExpensesStructureSQLSelectLocal select;
    
    @EJB
    private ExpensesStructureSQLUpdateLocal update;
   
    @EJB
    private ActualExpensesSQLLocal actualExpenses;
    
    @EJB
    private PlannedVariableParamsSQLLocal plannedParams;
    
    @EJB
    private TimePeriodsHandlerLocal timePeriods;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromEntityExpenseList(EntityExpense entity) {
        EntityExpenseList.getEntityExpenseList().remove(entity);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<EntityExpense> getEntityExpenseList() {
        return EntityExpenseList.getEntityExpenseList();
    }
    
    /**
     * Replaces current collection of EntityExpense elements (EntityExpenseList)
     * with the given EntityExpense list.
     * 
     * @param list EnitiyExpense list given for replacement.
     */
    private void replaceEntityExpenseList(ArrayList<EntityExpense> list) {
        // Clear current expenses list.
        EntityExpenseList.removeEntityExpenseList(); 
        // Write to common Entity Objects List.
        EntityExpenseList.setEntityExpenseList(list); 
    }
       
    /**
     * {@inheritDoc}
     */
    @Override
    public EntityExpense prepareEntityExpenseById(Connection connection,
            String inputPlanningPeriodsFrequency, Integer id) 
            throws GenericDBOperationException, GenericDBException {
        if (id == null || id < 1) {
            return null;
        }
        // Complete list of Expense IDs and mapped Types from DB.
        HashMap<Integer, String> typesMap = 
                select.executeSelectAllTypes(connection);
        if (typesMap.containsKey(id) == false) {
            throw new GenericDBOperationException("The database Expenses "
                    + "Structure table does not contain Expense with ID '" 
                    + id + "'.");
        }
        /* Complete list of Expense IDs and mapped Maps of fixed planning 
         * parameters and their values from DB.
         */
        HashMap<Integer, HashMap<String, Double>> valuesMap =
                select.executeSelectAllValues(connection);
        /* Complete list of Expense IDs and mapped Maps of linkage parameter 
         * names and their values from DB.
         */
        HashMap<Integer, HashMap<String, Integer>> linksMap =
                select.executeSelectAllLinks(connection);        
        
        // Time Period Dates.
        // Calculates or returns timePeriodDates from TimePeriods class.
        TreeSet<String> timePeriodDates = timePeriods
                .calculateTimePeriodDates(connection, 
                        inputPlanningPeriodsFrequency);        

        // EntityExpenseList (where calculation objects stored).
        ArrayList<EntityExpense> list = getEntityExpenseList();

        /* Checking if there is a Complex Expense to which Expense with given 
         * id is linked.
         */
        int complexIdLinked = linksMap.get(id).get("LINKED_TO_COMPLEX_ID");
        if (complexIdLinked != 0) {
            prepareByIdWOComplexExpenseLinkCheck (connection, 
                            inputPlanningPeriodsFrequency, timePeriodDates, 
                            complexIdLinked);
        }

        /* Checking if Expense with given id is a Complex Expense itself,
         * then getting the list of Expense IDs that are linked to this
         * Complex Expense, and performing calculations for each of the linked 
         * Expense using method without checking if they are linked to any
         * Complex ID.
         */
        if (typesMap.get(id).equals(COMPLEX_EXPENSES_SUPPORTED_TYPE)) {
            ArrayList<Integer> linkedIdList = new ArrayList<>();
            for (Map.Entry<Integer, HashMap<String, Integer>> entry 
                    : linksMap.entrySet()) {
                if (Objects.equals(entry.getValue()
                        .get("LINKED_TO_COMPLEX_ID"), id)) {
                    linkedIdList.add(entry.getKey());
                }
            }
            if (!linkedIdList.isEmpty()) {
                for (Integer linkedId : linkedIdList) {
                    prepareByIdWOComplexExpenseLinkCheck (connection, 
                            inputPlanningPeriodsFrequency, timePeriodDates, 
                            linkedId);
                }
            }
        }

        for (EntityExpense e : list) {
            if (id == e.getId()) {
                /* EntityExpense with given id exists in the EntityExpense list 
                 * - Updating it's parameters based on the database record. 
                 */
                String type = typesMap.get(id);
                if (type.equals(GOODS_SUPPORTED_TYPE)) {
                    e.setPrice(valuesMap.get(id).get("PRICE"));
                    e.setSafetyStockPcs(valuesMap.get(id)
                            .get("SAFETY_STOCK_PCS"));
                    e.setOrderQtyPcs(valuesMap.get(id).get("ORDER_QTY_PCS"));
                    e.setCurrentStockPcs(valuesMap.get(id)
                            .get("CURRENT_STOCK_PCS"));
                    e.calculateFixedParameters();
                }
                obtainChangeableVarParamsForEntityExpense(connection,
                        inputPlanningPeriodsFrequency, timePeriodDates, e);
                e.calculateVariableParameters(timePeriodDates);
                return e;
            }
        }
        /* EntityExpnese with given id does not exist in the EntityExpenseList
         * - adding EntityExpense with given id to this collection based on
         * the database record.
         */
        EntityExpense expenseDB = select.executeSelectById(connection, id);
        String type = expenseDB.getType();
        if (type.equals(GOODS_SUPPORTED_TYPE)) {
            expenseDB.calculateFixedParameters();
        }
        obtainChangeableVarParamsForEntityExpense(connection,
                inputPlanningPeriodsFrequency, timePeriodDates, expenseDB);
        expenseDB.calculateVariableParameters(timePeriodDates);
        list.add(expenseDB);
        return expenseDB;
    }
    
    /**
     * Method is doing the same job as 
     * {@link ExpensesHandler#prepareEntityExpenseById(java.sql.Connection, 
     * java.lang.String, java.lang.Integer)} but it does not
     * check whether Expense with given database ID is linked to any Complex
     * Expense.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @param timePeriodDates set of planning period dates.
     * @param id database Expense ID.
     */
    private void prepareByIdWOComplexExpenseLinkCheck (Connection connection,
            String inputPlanningPeriodsFrequency, 
            TreeSet<String> timePeriodDates, Integer id) 
            throws GenericDBOperationException, GenericDBException {
        if (id == null || id < 1) {
            return;
        }
        // Map with complete list of Expense IDs and Types from DB.
        HashMap<Integer, String> typesMap = 
                select.executeSelectAllTypes(connection);
        if (typesMap.containsKey(id) == false) {
            throw new GenericDBOperationException("The database Expenses "
                    + "Structure table does not contain Expense with ID '" 
                    + id + "'.");
        }
        /* Map with complete list of Expense IDs and Maps of fixed planning 
         * parameters and their values from DB.
         */
        HashMap<Integer, HashMap<String, Double>> valuesMap =
                select.executeSelectAllValues(connection);
        /* Map with complete list of Expense IDs and Maps of linkage parameter 
         * names and their values from DB.
         */
        HashMap<Integer, HashMap<String, Integer>> linksMap =
                select.executeSelectAllLinks(connection);        
        
        // EntityExpenseList (where calculation objects stored).
        ArrayList<EntityExpense> list = getEntityExpenseList();

        /* Checking if Expense with given id is a Complex Expense itself,
         * then getting the list of Expense IDs that are linked to this
         * Complex Expense, and performing calculations for each of the linked 
         * Expense using method without checking if they are linked to any
         * Complex ID.
         */
        if (typesMap.get(id).equals(COMPLEX_EXPENSES_SUPPORTED_TYPE)) {
            ArrayList<Integer> linkedIdList = new ArrayList<>();
            for (Map.Entry<Integer, HashMap<String, Integer>> entry 
                    : linksMap.entrySet()) {
                if (Objects.equals(entry.getValue()
                        .get("LINKED_TO_COMPLEX_ID"), id)) {
                    linkedIdList.add(entry.getKey());
                }
            }
            if (!linkedIdList.isEmpty()) {
                for (Integer linkedId : linkedIdList) {
                    prepareByIdWOComplexExpenseLinkCheck (connection, 
                            inputPlanningPeriodsFrequency, timePeriodDates, 
                            linkedId);
                }
            }
        }

        for (EntityExpense e : list) {
            if (id == e.getId()) {
                /* EntityExpense with given id exists in the EntityExpense list 
                 * - Updating it's parameters based on the database record. 
                 */
                String type = typesMap.get(id);
                if (type.equals(GOODS_SUPPORTED_TYPE)) {
                    e.setPrice(valuesMap.get(id).get("PRICE"));
                    e.setSafetyStockPcs(valuesMap.get(id)
                            .get("SAFETY_STOCK_PCS"));
                    e.setOrderQtyPcs(valuesMap.get(id).get("ORDER_QTY_PCS"));
                    e.setCurrentStockPcs(valuesMap.get(id)
                            .get("CURRENT_STOCK_PCS"));
                    e.calculateFixedParameters();
                }
                obtainChangeableVarParamsForEntityExpense(connection,
                        inputPlanningPeriodsFrequency, timePeriodDates, e);
                e.calculateVariableParameters(timePeriodDates);
                return;
            }
        }
        /* EntityExpnese with given id does not exist in the EntityExpenseList
         * - adding EntityExpense with given id to this collection based on
         * the database record. 
         */
        EntityExpense expenseDB = select.executeSelectById(connection, id);
        String type = expenseDB.getType();
        if (type.equals(GOODS_SUPPORTED_TYPE)) {
            expenseDB.calculateFixedParameters();
        }
        obtainChangeableVarParamsForEntityExpense(connection,
                inputPlanningPeriodsFrequency, timePeriodDates, expenseDB);
        expenseDB.calculateVariableParameters(timePeriodDates);
        list.add(expenseDB);
    }
    
    /**
     * Obtains changeable parameter values from the database into a given
     * EntityExpense object.
     * 
     * @param connection database Connection.
     * @param inputPlanningPeriodsFrequency frequency of the planning time 
     * periods.
     * @param timePeriodDates set of planning period dates.
     * @param expense EntityExpense object.
     */
    private void obtainChangeableVarParamsForEntityExpense(Connection 
            connection, String inputPlanningPeriodsFrequency, 
            TreeSet<String> timePeriodDates, EntityExpense expense) 
            throws GenericDBOperationException, GenericDBException {
        int id = expense.getId();
        String type = expense.getType();
        
        TreeMap<String, Double> actualExpensesPcsOrCur;
        TreeMap<String, Double> plannedExpensesPcsOrCur;
        TreeMap<String, Double> consumptionPcs;
        
        if (type.equals(SIMPLE_EXPENSES_SUPPORTED_TYPE) 
                || type.equals(GOODS_SUPPORTED_TYPE)) {
                    
            actualExpensesPcsOrCur = actualExpenses
                    .calculateActualExpenses(connection, 
                            timePeriodDates, inputPlanningPeriodsFrequency, id);
            
            plannedExpensesPcsOrCur = plannedParams
                    .selectPlannedExpensesById(connection, id);
            
            if (type.equals(SIMPLE_EXPENSES_SUPPORTED_TYPE)) {
                
                expense.setActualCur(actualExpensesPcsOrCur);
                expense.setPlannedCur(plannedExpensesPcsOrCur);    
                
            } else if (type.equals(GOODS_SUPPORTED_TYPE)) {
                
                consumptionPcs = 
                    plannedParams.selectConsumptionPcsById(connection, id);
                
                expense.setActualPcs(actualExpensesPcsOrCur);
                expense.setPlannedPcs(plannedExpensesPcsOrCur);
                expense.setConsumptionPcs(consumptionPcs);                
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<EntityExpense> actualizeEntityExpenseList(Connection connection) 
            throws GenericDBOperationException, GenericDBException {
        ArrayList<EntityExpense> expenseListDB = 
                select.executeSelectAll(connection);
        replaceEntityExpenseList(expenseListDB);
        return expenseListDB;
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void calculateAllCurrentStockPcsForNextPeriod(Connection connection) 
            throws GenericDBOperationException, GenericDBException {
        
        HashMap<Integer, String> allTypes 
                = select.executeSelectAllTypes(connection);
        HashMap<Integer, HashMap<String, Double>> allValues 
                = select.executeSelectAllValues(connection);
        
        String currentPeriodDate 
                = plannedParams.getCurrentPeriodDate(connection);
        
        Integer id;
        Double currentStock;
        
        TreeMap<String, Double> consumptionPcs;
        TreeMap<String, Double> plannedPcs;
        TreeMap<String, Double> differencePcs;

        Double consumptionPcsVal;
        Double plannedPcsVal;
        Double differencePcsVal;

        for (Map.Entry<Integer, String> entry : allTypes.entrySet()) {
            String type = entry.getValue();
            if (type.equals(GOODS_SUPPORTED_TYPE)) {
                id = entry.getKey();
                currentStock = allValues.get(id).get("CURRENT_STOCK_PCS");
                
                consumptionPcs = plannedParams
                        .selectConsumptionPcsById(connection, id);
                plannedPcs = plannedParams
                        .selectPlannedExpensesById(connection, id);
                differencePcs = plannedParams
                        .selectDifferencePcsById(connection, id);
                
                if (consumptionPcs == null || consumptionPcs.isEmpty()) {
                    consumptionPcsVal = (double) 0;
                } else {
                    consumptionPcsVal = consumptionPcs.get(currentPeriodDate);
                    if (consumptionPcsVal == null) {
                        consumptionPcsVal = (double) 0;
                    }
                }
                
                if (plannedPcs == null || plannedPcs.isEmpty()) {
                    plannedPcsVal = (double) 0;
                } else {
                    plannedPcsVal = plannedPcs.get(currentPeriodDate);
                    if (plannedPcsVal == null) {
                        plannedPcsVal = (double) 0;
                    }
                }                
                
                if (differencePcs == null || differencePcs.isEmpty()) {
                    differencePcsVal = (double) 0;
                } else {
                    differencePcsVal = differencePcs.get(currentPeriodDate);
                    if (differencePcsVal == null) {
                        differencePcsVal = (double) 0;
                    }
                }
                
                /* Recalculating Current Stock value for the Next Planning 
                 * Period.
                 */
                currentStock = currentStock - consumptionPcsVal + plannedPcsVal 
                        + differencePcsVal /*+ safetyStock*/;
                /* Updating the value of Current Stock for this Expense Id in 
                 * the database.
                 */
                update.updateCurrentStockById(connection, id, currentStock);
            }
        }  
    }

    /**
     * {@inheritDoc}
     */        
    @Override
    public void calculateAllCurrentStockPcsForPreviousPeriod(Connection connection) 
            throws GenericDBOperationException, GenericDBException {
        
        HashMap<Integer, String> allTypes 
                = select.executeSelectAllTypes(connection);
        HashMap<Integer, HashMap<String, Double>> allValues 
                = select.executeSelectAllValues(connection);
        
        String currentPeriodDate 
                = plannedParams.getCurrentPeriodDate(connection);
        
        Integer id;
        Double currentStock;
        
        TreeMap<String, Double> consumptionPcs;
        TreeMap<String, Double> plannedPcs;
        TreeMap<String, Double> differencePcs;

        Double consumptionPcsVal;
        Double plannedPcsVal;
        Double differencePcsVal;

        for (Map.Entry<Integer, String> entry : allTypes.entrySet()) {
            String type = entry.getValue();
            if (type.equals(GOODS_SUPPORTED_TYPE)) {
                id = entry.getKey();
                currentStock = allValues.get(id).get("CURRENT_STOCK_PCS");
                
                consumptionPcs = plannedParams
                        .selectConsumptionPcsById(connection, id);
                plannedPcs = plannedParams
                        .selectPlannedExpensesById(connection, id);
                differencePcs = plannedParams
                        .selectDifferencePcsById(connection, id);
                
                if (consumptionPcs == null || consumptionPcs.isEmpty()) {
                    consumptionPcsVal = (double) 0;
                } else {
                    consumptionPcsVal = consumptionPcs.get(currentPeriodDate);
                    if (consumptionPcsVal == null) {
                        consumptionPcsVal = (double) 0;
                    }
                }
                
                if (plannedPcs == null || plannedPcs.isEmpty()) {
                    plannedPcsVal = (double) 0;
                } else {
                    plannedPcsVal = plannedPcs.get(currentPeriodDate);
                    if (plannedPcsVal == null) {
                        plannedPcsVal = (double) 0;
                    }
                }                
                
                if (differencePcs == null || differencePcs.isEmpty()) {
                    differencePcsVal = (double) 0;
                } else {
                    differencePcsVal = differencePcs.get(currentPeriodDate);
                    if (differencePcsVal == null) {
                        differencePcsVal = (double) 0;
                    }
                }
                
                /* Recalculating Current Stock value for the Previous Planning 
                 * Period.
                 */
                currentStock = currentStock + consumptionPcsVal - plannedPcsVal 
                        - differencePcsVal /*- safetyStock*/;
                /* Updating the value of Current Stock for this Expense Id in 
                 * the database.
                 */
                update.updateCurrentStockById(connection, id, currentStock);
            }
        }
    } 
}