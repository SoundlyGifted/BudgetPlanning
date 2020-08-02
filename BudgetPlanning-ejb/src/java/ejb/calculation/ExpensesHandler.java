
package ejb.calculation;

import ejb.MainScreen.PlannedVariableParamsSQLLocal;
import ejb.actualExpenses.ActualExpensesSQLLocal;
import ejb.expensesStructure.ExpensesStructureSQLSelectLocal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author SoundlyGifted
 */
@Singleton
@Startup
public class ExpensesHandler implements ExpensesHandlerLocal {

    @EJB
    private ExpensesStructureSQLSelectLocal select;
   
    @EJB
    private ActualExpensesSQLLocal actualExpenses;
    
    @EJB
    private PlannedVariableParamsSQLLocal plannedParams;
    
    @EJB
    private TimePeriodsHandlerLocal timePeriods;
    
    /**
     * Method to remove an EntityExpense element from the EntityExpenseList
     * collection
     * 
     * @param entity EntityExpense element to be removed.
     */
    @Override
    public void removeFromEntityExpenseList(EntityExpense entity) {
        EntityExpenseList.getEntityExpenseList().remove(entity);
    }
    
    /**
     * Gets collection of EntityExpense elements (EntityExpenseList) for 
     * further usage (calculation of parameters).
     * 
     * @return EntityExpenseList collection.
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
     * Method to get a certain EntityExpense element from EntityExpenseList
     * collection by id.If EntityExpense with the given id does not exist in the 
     * EntityExpenseList then it is created and placed to the collection 
     * based on the database record.
     * If exists in the EntityExpenseList then
     * it's parameters are updated based on the database record. If no such 
     * record in the database then returns null.
     * Method also checks whether any Complex EntityExpense linked to the
     * EntityExpense with given id exists in the EntityExpenseList in the way 
     * like described above.
     * 
     * @param connection database connection.
     * @param inputPlanningPeriodsFrequency
     * @param id the value of "id" variable of the EntityExpense to be 
     *           selected.
     * @return EntityExpense with the given id if exists in the 
     *         EntityExpenseList and database record exists, null otherwise.
     */
    @Override
    public EntityExpense prepareEntityExpenseById(Connection connection,
            String inputPlanningPeriodsFrequency, Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        //Set with complete list of Expense IDs and mapped Types from DB.
        HashMap<Integer, String> typesMap = 
                select.executeSelectAllTypes(connection);
        if (typesMap.containsKey(id) == false) {
            return null;
        }
        //Set with complete list of Expense IDs and mapped Maps of fixed
        //planning parameters and their values from DB.
        HashMap<Integer, HashMap<String, Double>> valuesMap =
                select.executeSelectAllValues(connection);
        //Set with complete list of Expense IDs and mapped Maps of linkage
        //parameter names and their values from DB.
        HashMap<Integer, HashMap<String, Integer>> linksMap =
                select.executeSelectAllLinks(connection);        
        
        /* Time Period Dates. */
        // calculates or returns timePeriodDates from TimePeriods class.
        TreeSet<String> timePeriodDates = timePeriods
                .calculateTimePeriodDates(connection, 
                        inputPlanningPeriodsFrequency);        

        /* EntityExpenseList (where calculation objects stored). */
        ArrayList<EntityExpense> list = getEntityExpenseList();

        // Checking if there is a Complex Expense to which Expense with given
        // id is linked.
        int complexIdLinked = linksMap.get(id).get("LINKED_TO_COMPLEX_ID");
        if (complexIdLinked != 0) {
            prepareByIdWOComplexExpenseLinkCheck (connection, 
                            inputPlanningPeriodsFrequency, timePeriodDates, 
                            complexIdLinked);
            for (EntityExpense expense : list) {
                if (expense.getId() == id) {
                    return expense;
                }
            }
        }

        for (EntityExpense e : list) {
            if (id == e.getId()) {
                /* EntityExpense with given id exists in the EntityExpense list 
                   - Updating it's parameters based on the database record. */
                String type = typesMap.get(id);
                if (type.equals("GOODS")) {
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
           - adding EntityExpense with given id to this collection based on
           the database record. */
        EntityExpense expenseDB = select.executeSelectById(connection, id);
        String type = expenseDB.getType();
        if (type.equals("GOODS")) {
            expenseDB.calculateFixedParameters();
        }
        obtainChangeableVarParamsForEntityExpense(connection,
                inputPlanningPeriodsFrequency, timePeriodDates, expenseDB);
        expenseDB.calculateVariableParameters(timePeriodDates);
        list.add(expenseDB);
        return expenseDB;
    }
    
    
    private void prepareByIdWOComplexExpenseLinkCheck (Connection connection,
            String inputPlanningPeriodsFrequency, 
            TreeSet<String> timePeriodDates, Integer id) {
        if (id == null || id < 1) {
            return;
        }
        //Map with complete list of Expense IDs and Types from DB.
        HashMap<Integer, String> typesMap = 
                select.executeSelectAllTypes(connection);
        if (typesMap.containsKey(id) == false) {
            return;
        }
        //Map with complete list of Expense IDs and Maps of fixed
        //planning parameters and their values from DB.
        HashMap<Integer, HashMap<String, Double>> valuesMap =
                select.executeSelectAllValues(connection);
        //Map with complete list of Expense IDs and Maps of linkage
        //parameter names and their values from DB.
        HashMap<Integer, HashMap<String, Integer>> linksMap =
                select.executeSelectAllLinks(connection);        
        
        /* EntityExpenseList (where calculation objects stored). */
        ArrayList<EntityExpense> list = getEntityExpenseList();

        // Checking if Expense with given id is a Complex Expense itself,
        // then getting the list of Expense IDs that are linked to this
        // Complex Expense, and performing calculations for each of the linked 
        // Expense using method without checking if they are linked to any
        // Complex ID.
        if (typesMap.get(id).equals("COMPLEX_EXPENSES")) {
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
                   - Updating it's parameters based on the database record. */
                String type = typesMap.get(id);
                if (type.equals("GOODS")) {
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
           - adding EntityExpense with given id to this collection based on
           the database record. */
        EntityExpense expenseDB = select.executeSelectById(connection, id);
        String type = expenseDB.getType();
        if (type.equals("GOODS")) {
            expenseDB.calculateFixedParameters();
        }
        obtainChangeableVarParamsForEntityExpense(connection,
                inputPlanningPeriodsFrequency, timePeriodDates, expenseDB);
        expenseDB.calculateVariableParameters(timePeriodDates);
        list.add(expenseDB);   
    }
    
    private void obtainChangeableVarParamsForEntityExpense(Connection 
            connection, String inputPlanningPeriodsFrequency, 
            TreeSet<String> timePeriodDates, EntityExpense expense) {
        int id = expense.getId();
        String type = expense.getType();
        
        TreeMap<String, Double> actualExpensesPcsOrCur;
        TreeMap<String, Double> plannedExpensesPcsOrCur;
        TreeMap<String, Double> consumptionPcs;
        
        if (type.equals("SIMPLE_EXPENSES") || type.equals("GOODS")) {
                    
            actualExpensesPcsOrCur = actualExpenses
                    .calculateActualExpenses(connection, 
                            timePeriodDates, inputPlanningPeriodsFrequency, id);
            
            plannedExpensesPcsOrCur = plannedParams
                    .selectPlannedExpensesById(connection, id);
            
            if (type.equals("SIMPLE_EXPENSES")) {
                
                expense.setActualCur(actualExpensesPcsOrCur);
                expense.setPlannedCur(plannedExpensesPcsOrCur);    
                
            } else if (type.equals("GOODS")) {
                
                consumptionPcs = 
                    plannedParams.selectConsumptionPcsById(connection, id);
                
                expense.setActualPcs(actualExpensesPcsOrCur);
                expense.setPlannedPcs(plannedExpensesPcsOrCur);
                expense.setConsumptionPcs(consumptionPcs);                
            }
        }
    }
    
    /**
     * Actualizes EntityExpenseList (replaces it with the list obtained from
     * the database).
     * 
     * @param connection database connection.
     * @return EntityExpenseList obtained based on the database records.
     */
    @Override
    public ArrayList<EntityExpense> actualizeEntityExpenseList(Connection 
            connection) {
        ArrayList<EntityExpense> expenseListDB = 
                select.executeSelectAll(connection);
        replaceEntityExpenseList(expenseListDB);
        return expenseListDB;
    }
    
}
