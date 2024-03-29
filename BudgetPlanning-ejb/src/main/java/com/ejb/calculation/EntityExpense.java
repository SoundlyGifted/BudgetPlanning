
package com.ejb.calculation;

import com.ejb.common.EjbCommonMethods;
import com.ejb.expstructure.ExpensesTypes;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * EntityExpense class is used to perform calculations of attributes and 
 * parameters of a certain Expense database representation and hold the 
 * calculated values until they are placed into the database.
 */
public class EntityExpense extends EjbCommonMethods implements ExpensesTypes {

    // Constant parameters for each Expense Category.
    private final int id;      // fixed
    private final String type; // fixed
    
    // Common Fixed parameter variables for all Expense types.
    private String name;            // CHANGEABLE
    private int accountId;          // CHANGEABLE
    private String accountLinked;   // CHANGEABLE
    private int linkedToComplexId;  // CHANGEABLE
    
    // Fixed parameter variables for 'GOODS' Expense type only.
    private double price = 0;               // CHANGEABLE
    private double currentStockPcs = 0;     // CHANGEABLE and calculated
    private double currentStockCur = 0;     // calculated
    private double currentStockWscPcs = 0;  // calculated
    private double currentStockWscCur = 0;  // calculated
    private double safetyStockPcs = 0;      // CHANGEABLE
    private double safetyStockCur = 0;      // calculated
    private double orderQtyPcs = 1;         // CHANGEABLE
    private double orderQtyCur = 0;         // calculated
    
    // Variable parameter variables (values depend on time period dates).
    // Below apply to all Expense types.
    private TreeMap<String, Double> plannedCur;     /* CHANGEABLE 
                                                     * (calculated for Expense  
                                                     * type = 'GOODS') 
                                                     */
    private TreeMap<String, Double> actualCur;      // CHANGEABLE
    private TreeMap<String, Double> differenceCur;  // calculated
    // Below apply to Expense type = 'GOODS' only.
    private TreeMap<String, Double> consumptionPcs; // CHANGEABLE
    private TreeMap<String, Double> consumptionCur; // calculated
    private TreeMap<String, Double> stockPcs;       // calculated
    private TreeMap<String, Double> stockCur;       // calculated
    private TreeMap<String, Double> requirementPcs; // calculated
    private TreeMap<String, Double> requirementCur; // calculated
    private TreeMap<String, Double> plannedPcs;     // CHANGEABLE
    private TreeMap<String, Double> actualPcs;      // CHANGEABLE
    private TreeMap<String, Double> differencePcs;  // calculated

    /* Flag to indicate if Expense contains performed variable parameters 
     * calculations.
     */
    private boolean calculated = false;
    
    // Constructors for the case of selection of Expense from database tables.
    /**
     * Constructor of the EntityExpense class (setting all attributes for 
     * Expense paramter values calculation purpose).
     * 
     * @param id database Expense ID.
     * @param type type of the Expense.
     * @param name Expense name.
     * @param accountId Expense linked account database ID.
     * @param accountLinked Expense linked account name.
     * @param linkedToComplexId Expense linked Complex Expense database ID.
     * @param price Expense planning price (price for planning purpose).
     * @param currentStockPcs Expense Current Stock (PCS) attribute.
     * @param currentStockCur Expense Current Stock (CUR) attribute.
     * @param currentStockWscPcs Expense Current Stock With Safety Stock
     * Consideration (PCS) attribute.
     * @param currentStockWscCur Expense Current Stock With Safety Stock
     * Consideration (CUR) attribute.
     * @param safetyStockPcs Expense Safety Stock (PCS) attribute.
     * @param safetyStockCur Expense Safety Stock (CUR) attribute.
     * @param orderQtyPcs Expense Order Quantity (PCS) attribure.
     * @param orderQtyCur Expense Order Quantity (CUR) attribure.
     */
    public EntityExpense(int id, String type, String name, int accountId, 
            String accountLinked, int linkedToComplexId, Double price,
            Double currentStockPcs, Double currentStockCur,
            Double currentStockWscPcs, Double currentStockWscCur,
            Double safetyStockPcs, Double safetyStockCur, Double orderQtyPcs, 
            Double orderQtyCur) {
        
        if (!inputCheckType(type)) {
            throw new IllegalArgumentException("EntityExpense() : Unable to "
                    + "create EntityExpense object, wrong Expense type '" 
                    + type + "' entered");
        }        
        
        this.id = id;
        this.type = type;

        this.name = name;
        this.accountId = accountId;
        this.accountLinked = accountLinked;
        this.linkedToComplexId = linkedToComplexId;
        
        if (type.equals(GOODS_SUPPORTED_TYPE)) {
            this.price = price;
            this.currentStockPcs = currentStockPcs;
            this.currentStockCur = currentStockCur;
            this.currentStockWscPcs = currentStockWscPcs;
            this.currentStockWscCur = currentStockWscCur;
            this.safetyStockPcs = safetyStockPcs;
            this.safetyStockCur = safetyStockCur;
            this.orderQtyPcs = orderQtyPcs;
            this.orderQtyCur = orderQtyCur;            
        }
    }
    
    /**
     * Constructor of the EntityExpense class (setting only the attributes 
     * necessary to perform database operations).
     * 
     * @param id database Expense ID.
     * @param type type of the Expense.
     * @param name Expense name.
     * @param accountId Expense linked account database ID.
     * @param accountLinked Expense linked account name.
     * @param linkedToComplexId Expense linked Complex Expense database ID.
     * @param price Expense planning price (price for planning purpose).
     * @param currentStockPcs Expense Current Stock (PCS) attribute.
     * @param safetyStockPcs Expense Safety Stock (PCS) attribute.
     * @param orderQtyPcs Expense Order Quantity (PCS) attribure.
     */
    public EntityExpense(int id, String type, String name, int accountId, 
            String accountLinked, int linkedToComplexId, 
            Double price,
            Double currentStockPcs,
            Double safetyStockPcs, 
            Double orderQtyPcs) {
        
        if (!inputCheckType(type)) {
            throw new IllegalArgumentException("EntityExpense() : Unable to "
                    + "create EntityExpense object, wrong Expense type '" 
                    + type + "' entered");
        }    
        
        this.id = id;
        this.type = type;
        
        this.name = name;
        this.accountId = accountId;
        this.accountLinked = accountLinked;
        this.linkedToComplexId = linkedToComplexId;
        
        if (type.equals(GOODS_SUPPORTED_TYPE)) {
            this.price = price;
            this.currentStockPcs = currentStockPcs;
            this.safetyStockPcs = safetyStockPcs;
            this.orderQtyPcs = orderQtyPcs;
            calculateFixedParameters();
        }
    }    

    /**
     * Method calculates all application-calculated Fixed attributes within
     * the EntityExpense object based on the user-changeable Fixed parameters, 
     * only for Expenses with type = "GOODS".
     * 
     * @return "true" in case of success of the operation and "false" otherwise.
     */
    public final boolean calculateFixedParameters() {
        if (type.equals(GOODS_SUPPORTED_TYPE)) {
            currentStockCur = round(price * currentStockPcs, 2);
            currentStockWscPcs = currentStockPcs - safetyStockPcs;
            currentStockWscCur = round(price * currentStockWscPcs, 2);
            safetyStockCur = round(price * safetyStockPcs, 2);
            orderQtyCur = round(price * orderQtyPcs, 2);
        }
        return true;
    }
    
    /**
     * Calculates variable parameters of this EntityExpense object based on the
     * data within the object and the calls of private methods that use domain 
     * specific formulae for calculation.
     * 
     * @param timePeriodDates set of planning period dates.
     */
    public void calculateVariableParameters(TreeSet<String> 
            timePeriodDates) {
        if (type.equals(SIMPLE_EXPENSES_SUPPORTED_TYPE)) {
            calculateVariableParametersForSimpleExpenses(timePeriodDates);
            calculated = true;
        } else if (type.equals(COMPLEX_EXPENSES_SUPPORTED_TYPE)) {
            calculateVariableParametersForComplexExpenses(timePeriodDates);
            calculated = true;
        } else if (type.equals(GOODS_SUPPORTED_TYPE)) {
            calculateVariableParametersForGoods(timePeriodDates);
            calculated = true;
        }
    }
 
    /**
     * Initializes given variable parameter.
     * 
     * @param param variable parameter name.
     * @return new variable parameter object (if given parameter does not exist)
     * of parameter with all values cleared (if given parameter exists).
     */
    private TreeMap<String, Double>
            initializeVariableParam(TreeMap<String, Double> param) {
        if (param == null) {
            return new TreeMap<>();
        }
        param.clear();
        return param;
    }
   
    /**
     * Resets all variable parameters in this EntityExpense object which means
     * their values will be cleared (if parameter exists) and the "calculated"
     * flag of the object (representing the parameter calculation status of the
     * object) will be set to "false".
     */        
    public void resetVariableParams() {
        if (plannedCur != null && !plannedCur.isEmpty()) {
            plannedCur.clear();
        }
        if (actualCur != null && !actualCur.isEmpty()) {
            actualCur.clear();
        }
        if (differenceCur != null && !differenceCur.isEmpty()) {
            differenceCur.clear();
        }
        if (consumptionPcs != null && !consumptionPcs.isEmpty()) {
            consumptionPcs.clear();
        }
        if (consumptionCur != null && !consumptionCur.isEmpty()) {
           consumptionCur.clear();
        }
        if (stockPcs != null && !stockPcs.isEmpty()) {
            stockPcs.clear();
        }
        if (stockCur != null && !stockCur.isEmpty()) {
            stockCur.clear();
        }
        if (requirementPcs != null && !requirementPcs.isEmpty()) {
            requirementPcs.clear();
        }
        if (requirementCur != null && !requirementCur.isEmpty()) {
            requirementCur.clear();
        }
        if (plannedPcs != null && !plannedPcs.isEmpty()) {
            plannedPcs.clear();
        }
        if (actualPcs != null && !actualPcs.isEmpty()) {
            actualPcs.clear();
        }
        if (differencePcs != null && !differencePcs.isEmpty()) {
            differencePcs.clear();
        }
        calculated = false;
    }    

    /**
     * Calculates variable parameters of this EntityExpense object (with type =
     * 'GOODS') based on data within the object and the domain specific 
     * formulae.
     * 
     * @param timePeriodDates set of planning period dates.
     */
    private void 
        calculateVariableParametersForGoods(TreeSet<String> timePeriodDates) {
        plannedCur = initializeVariableParam(plannedCur);
        actualCur = initializeVariableParam(actualCur);
        differenceCur = initializeVariableParam(differenceCur);
        consumptionCur = initializeVariableParam(consumptionCur);
        stockPcs = initializeVariableParam(stockPcs);
        stockCur = initializeVariableParam(stockCur);
        requirementPcs = initializeVariableParam(requirementPcs);
        requirementCur = initializeVariableParam(requirementCur);
        differencePcs = initializeVariableParam(differencePcs);

        Double prevStockPcs = currentStockWscPcs;
        Double consumptionPcsSum = (double) 0;
        Double plannedPcsSum = (double) 0;
        Double requirementPcsCheck = (double) 0;
        Double requirementPcsSum = (double) 0;
        
        for (String date : timePeriodDates) {
            
            Double consumptionPcsVal = consumptionPcs.get(date);
            if (consumptionPcsVal == null) {
                consumptionPcsVal = (double) 0;
            }
            consumptionCur.put(date, round(price * consumptionPcsVal,2));
            
            Double plannedPcsVal = plannedPcs.get(date);
            if (plannedPcsVal == null) {
                plannedPcsVal = (double) 0;
            }
            plannedCur.put(date, round(price * plannedPcsVal,2));            
            
            Double actualPcsVal = actualPcs.get(date);
            if (actualPcsVal == null) {
                actualPcsVal = (double) 0;
            }
            actualCur.put(date, round(price * actualPcsVal,2));              
            
            Double differencePcsVal = actualPcsVal - plannedPcsVal;
            differencePcs.put(date, differencePcsVal);
            differenceCur.put(date, round(price * differencePcsVal, 2));
            
            Double stockPcsVal;
            stockPcsVal = prevStockPcs - consumptionPcsVal + plannedPcsVal;
            stockPcs.put(date, stockPcsVal);
            stockCur.put(date, round(price * stockPcsVal, 2));
            prevStockPcs = stockPcsVal;
            
            consumptionPcsSum = consumptionPcsSum + consumptionPcsVal;
            plannedPcsSum = plannedPcsSum + plannedPcsVal;
            Double requirementPcsVal;
            requirementPcsCheck = currentStockWscPcs + plannedPcsSum 
                    + requirementPcsSum - consumptionPcsSum;
            if (requirementPcsCheck < 0) {
                requirementPcsVal = Math.ceil(Math
                        .abs(requirementPcsCheck)/orderQtyPcs)*orderQtyPcs;
            } else {
                requirementPcsVal = (double) 0;
            }
            requirementPcs.put(date, requirementPcsVal);
            requirementPcsSum = requirementPcsSum + requirementPcsVal;
            requirementCur.put(date, round(price * requirementPcsVal, 2));
        }
    }
        
    /**
     * Calculates variable parameters of this EntityExpense object (with type =
     * 'SIMPLE_EXPENSES') based on data within the object and the domain 
     * specific formulae.
     * 
     * @param timePeriodDates set of planning period dates.
     */   
    private void calculateVariableParametersForSimpleExpenses(TreeSet<String> 
            timePeriodDates) {
        
        differenceCur = initializeVariableParam(differenceCur);
        
        Double actualCurVal;
        Double plannedCurVal;
        Double differenceCurVal;
        for (String date : timePeriodDates) {
            actualCurVal = actualCur.get(date);
            plannedCurVal = plannedCur.get(date);
            if (actualCurVal == null) {
                actualCurVal = (double) 0;
            }
            if (plannedCurVal == null) {
                plannedCurVal = (double) 0;
            }
            differenceCurVal = round(actualCurVal - plannedCurVal, 2);
            differenceCur.put(date, differenceCurVal);
        }
    }

    /**
     * Calculates variable parameters of this EntityExpense object (with type =
     * 'COMPLEX_EXPENSES') based on data within the object and the domain 
     * specific formulae.
     * 
     * @param timePeriodDates set of planning period dates.
     */
    private void calculateVariableParametersForComplexExpenses(TreeSet<String> 
            timePeriodDates) {
        plannedCur = initializeVariableParam(plannedCur);
        actualCur = initializeVariableParam(actualCur);
        differenceCur = initializeVariableParam(differenceCur);

        ArrayList<EntityExpense> list = EntityExpenseList
                .getEntityExpenseList();
        for (String date : timePeriodDates) {
            Double plannedCurVal = (double) 0;
            Double actualCurVal = (double) 0;
            Double differenceCurVal;
            for (EntityExpense expense : list) {
                if (expense.getLinkedToComplexId() == id) {
                    Double linkedPlannedCurVal = 
                            expense.getPlannedCur().get(date);
                    Double linkedActualCurVal = 
                            expense.getActualCur().get(date);
                    if (linkedPlannedCurVal == null) {
                        linkedPlannedCurVal = (double) 0;
                    }
                    if (linkedActualCurVal == null) {
                        linkedActualCurVal = (double) 0;
                    }
                    plannedCurVal = plannedCurVal + linkedPlannedCurVal;
                    actualCurVal = actualCurVal + linkedActualCurVal;
                }
            }
            differenceCurVal = actualCurVal - plannedCurVal;
            plannedCur.put(date, plannedCurVal);
            actualCur.put(date, actualCurVal);
            differenceCur.put(date, differenceCurVal);
        }
    }    
    
    @Override
    public String toString() {
        if (type.equals(GOODS_SUPPORTED_TYPE)) {
        return "EntityExpense{<br>Constant Parameters :<br>" 
                + "id=" + id + ", type=" + type 
                + "<br>Common Fixed Parameters :<br>"
                + " name=" + name + ", accountId=" + accountId 
                + ", accountLinked=" + accountLinked + ", linkedToComplexId=" 
                + linkedToComplexId 
                + "<br>Fixed Parameters for GOODS type only :<br>"
                + " price=" + price + ", currentStockPcs=" + currentStockPcs 
                + ", currentStockCur=" + currentStockCur 
                + ", currentStockWscPcs=" + currentStockWscPcs 
                + ", currentStockWscCur=" + currentStockWscCur 
                + ", safetyStockPcs=" + safetyStockPcs + ", safetyStockCur=" 
                + safetyStockCur + ", orderQtyPcs=" + orderQtyPcs 
                + ", orderQtyCur=" + orderQtyCur 
                + "<br>Variable Parameters : "
                + "<br>plannedCur=" + plannedCur 
                + "<br>actualCur=" + actualCur 
                + "<br>differenceCur=" + differenceCur 
                + "<br>consumptionPcs=" + consumptionPcs 
                + "<br>consumptionCur=" + consumptionCur 
                + "<br>stockPcs=" + stockPcs 
                + "<br>stockCur=" + stockCur 
                + "<br>requirementPcs=" + requirementPcs 
                + "<br>requirementCur=" + requirementCur 
                + "<br>plannedPcs=" + plannedPcs 
                + "<br>actualPcs=" + actualPcs 
                + "<br>differencePcs=" + differencePcs + '}';            
        } else {
                    return "EntityExpense{<br>Constant Parameters :<br>" 
                + "id=" + id + ", type=" + type 
                + "<br>Common Fixed Parameters :<br>"
                + " name=" + name + ", accountId=" + accountId 
                + ", accountLinked=" + accountLinked + ", linkedToComplexId=" 
                + linkedToComplexId 
                + "<br>Variable Parameters : "
                + "<br>plannedCur=" + plannedCur 
                + "<br>actualCur=" + actualCur 
                + "<br>differenceCur=" + differenceCur + '}';
        }
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.id;
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + this.accountId;
        hash = 89 * hash + Objects.hashCode(this.accountLinked);
        hash = 89 * hash + this.linkedToComplexId;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.price) 
                ^ (Double.doubleToLongBits(this.price) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.currentStockPcs) 
                ^ (Double.doubleToLongBits(this.currentStockPcs) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.currentStockCur) 
                ^ (Double.doubleToLongBits(this.currentStockCur) >>> 32));
        hash = 89 * hash 
                + (int) (Double.doubleToLongBits(this.currentStockWscPcs) 
                ^ (Double.doubleToLongBits(this.currentStockWscPcs) >>> 32));
        hash = 89 * hash 
                + (int) (Double.doubleToLongBits(this.currentStockWscCur) 
                ^ (Double.doubleToLongBits(this.currentStockWscCur) >>> 32));
        hash = 89 * hash 
                + (int) (Double.doubleToLongBits(this.safetyStockPcs) 
                ^ (Double.doubleToLongBits(this.safetyStockPcs) >>> 32));
        hash = 89 * hash 
                + (int) (Double.doubleToLongBits(this.safetyStockCur) 
                ^ (Double.doubleToLongBits(this.safetyStockCur) >>> 32));
        hash = 89 * hash 
                + (int) (Double.doubleToLongBits(this.orderQtyPcs) 
                ^ (Double.doubleToLongBits(this.orderQtyPcs) >>> 32));
        hash = 89 * hash 
                + (int) (Double.doubleToLongBits(this.orderQtyCur) 
                ^ (Double.doubleToLongBits(this.orderQtyCur) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntityExpense other = (EntityExpense) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.accountId != other.accountId) {
            return false;
        }
        if (this.linkedToComplexId != other.linkedToComplexId) {
            return false;
        }
        if (Double.doubleToLongBits(this.price) 
                != Double.doubleToLongBits(other.price)) {
            return false;
        }
        if (Double.doubleToLongBits(this.currentStockPcs) 
                != Double.doubleToLongBits(other.currentStockPcs)) {
            return false;
        }
        if (Double.doubleToLongBits(this.currentStockCur) 
                != Double.doubleToLongBits(other.currentStockCur)) {
            return false;
        }
        if (Double.doubleToLongBits(this.currentStockWscPcs) 
                != Double.doubleToLongBits(other.currentStockWscPcs)) {
            return false;
        }
        if (Double.doubleToLongBits(this.currentStockWscCur) 
                != Double.doubleToLongBits(other.currentStockWscCur)) {
            return false;
        }
        if (Double.doubleToLongBits(this.safetyStockPcs) 
                != Double.doubleToLongBits(other.safetyStockPcs)) {
            return false;
        }
        if (Double.doubleToLongBits(this.safetyStockCur) 
                != Double.doubleToLongBits(other.safetyStockCur)) {
            return false;
        }
        if (Double.doubleToLongBits(this.orderQtyPcs) 
                != Double.doubleToLongBits(other.orderQtyPcs)) {
            return false;
        }
        if (Double.doubleToLongBits(this.orderQtyCur) 
                != Double.doubleToLongBits(other.orderQtyCur)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.accountLinked, other.accountLinked)) {
            return false;
        }
        return true;
    }

    public double getCurrentStockPcs() {
        return currentStockPcs;
    }

    public void setCurrentStockPcs(double currentStockPcs) {
        this.currentStockPcs = currentStockPcs;
    }

    public double getCurrentStockCur() {
        return currentStockCur;
    }

    public void setCurrentStockCur(double currentStockCur) {
        this.currentStockCur = currentStockCur;
    }

    public double getCurrentStockWscPcs() {
        return currentStockWscPcs;
    }

    public void setCurrentStockWscPcs(double currentStockWscPcs) {
        this.currentStockWscPcs = currentStockWscPcs;
    }

    public double getCurrentStockWscCur() {
        return currentStockWscCur;
    }

    public void setCurrentStockWscCur(double currentStockWscCur) {
        this.currentStockWscCur = currentStockWscCur;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountLinked() {
        return accountLinked;
    }

    public void setAccountLinked(String accountLinked) {
        this.accountLinked = accountLinked;
    }

    public int getLinkedToComplexId() {
        return linkedToComplexId;
    }

    public void setLinkedToComplexId(int linkedToComplexId) {
        this.linkedToComplexId = linkedToComplexId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSafetyStockPcs() {
        return safetyStockPcs;
    }

    public void setSafetyStockPcs(double safetyStockPcs) {
        this.safetyStockPcs = safetyStockPcs;
    }

    public double getSafetyStockCur() {
        return safetyStockCur;
    }

    public void setSafetyStockCur(double safetyStockCur) {
        this.safetyStockCur = safetyStockCur;
    }

    public double getOrderQtyPcs() {
        return orderQtyPcs;
    }

    public void setOrderQtyPcs(double orderQtyPcs) {
        this.orderQtyPcs = orderQtyPcs;
    }

    public double getOrderQtyCur() {
        return orderQtyCur;
    }

    public void setOrderQtyCur(double orderQtyCur) {
        this.orderQtyCur = orderQtyCur;
    }

    public TreeMap<String, Double> getPlannedCur() {
        return plannedCur;
    }

    public void setPlannedCur(TreeMap<String, Double> plannedCur) {
        this.plannedCur = plannedCur;
    }

    public TreeMap<String, Double> getActualCur() {
        return actualCur;
    }

    public void setActualCur(TreeMap<String, Double> actualCur) {
        this.actualCur = actualCur;
    }

    public TreeMap<String, Double> getDifferenceCur() {
        return differenceCur;
    }

    public void setDifferenceCur(TreeMap<String, Double> differenceCur) {
        this.differenceCur = differenceCur;
    }

    public TreeMap<String, Double> getConsumptionPcs() {
        return consumptionPcs;
    }

    public void setConsumptionPcs(TreeMap<String, Double> consumptionPcs) {
        this.consumptionPcs = consumptionPcs;
    }

    public TreeMap<String, Double> getConsumptionCur() {
        return consumptionCur;
    }

    public void setConsumptionCur(TreeMap<String, Double> consumptionCur) {
        this.consumptionCur = consumptionCur;
    }

    public TreeMap<String, Double> getStockPcs() {
        return stockPcs;
    }

    public void setStockPcs(TreeMap<String, Double> stockPcs) {
        this.stockPcs = stockPcs;
    }

    public TreeMap<String, Double> getStockCur() {
        return stockCur;
    }

    public void setStockCur(TreeMap<String, Double> stockCur) {
        this.stockCur = stockCur;
    }

    public TreeMap<String, Double> getRequirementPcs() {
        return requirementPcs;
    }

    public void setRequirementPcs(TreeMap<String, Double> requirementPcs) {
        this.requirementPcs = requirementPcs;
    }

    public TreeMap<String, Double> getRequirementCur() {
        return requirementCur;
    }

    public void setRequirementCur(TreeMap<String, Double> requirementCur) {
        this.requirementCur = requirementCur;
    }

    public TreeMap<String, Double> getPlannedPcs() {
        return plannedPcs;
    }

    public void setPlannedPcs(TreeMap<String, Double> plannedPcs) {
        this.plannedPcs = plannedPcs;
    }

    public TreeMap<String, Double> getActualPcs() {
        return actualPcs;
    }

    public void setActualPcs(TreeMap<String, Double> actualPcs) {
        this.actualPcs = actualPcs;
    }

    public TreeMap<String, Double> getDifferencePcs() {
        return differencePcs;
    }

    public void setDifferencePcs(TreeMap<String, Double> differencePcs) {
        this.differencePcs = differencePcs;
    }

    public boolean isCalculated() {
        return calculated;
    }
}
