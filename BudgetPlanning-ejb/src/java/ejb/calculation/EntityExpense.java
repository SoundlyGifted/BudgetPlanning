
package ejb.calculation;

import ejb.common.EjbCommonMethods;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author SoundlyGifted
 */
public class EntityExpense extends EjbCommonMethods {

    /* Constant parameters for each Expense Category. */
    private final int id;      /* fixed */
    private final String type; /* fixed */
    
    /* Common Fixed parameter variables for all Expense types. */    
    private String name;            /* CHANGEABLE */
    private int accountId;          /* CHANGEABLE */
    private String accountLinked;   /* CHANGEABLE */
    private int linkedToComplexId;  /* CHANGEABLE */
    
    /* Fixed parameter variables for 'GOODS' Expense type only. */
    private double price = 0;               /* CHANGEABLE */
    private double currentStockPcs = 0;     /* CHANGEABLE and calculated */
    private double currentStockCur = 0;     /* calculated */
    private double currentStockWscPcs = 0;  /* calculated */
    private double currentStockWscCur = 0;  /* calculated */
    private double safetyStockPcs = 0;      /* CHANGEABLE */
    private double safetyStockCur = 0;      /* calculated */
    private double orderQtyPcs = 0;         /* CHANGEABLE */
    private double orderQtyCur = 0;         /* calculated */
    
    /* Variable parameter variables (values depend on time period dates). */
    /* Below apply to all Expense types. */
    private TreeMap<String, Double> plannedCur;     /* CHANGEABLE 
                                                 * (calculated for Expense type 
                                                 * = 'GOODS') 
                                                 */
    private TreeMap<String, Double> actualCur;      /* calculated */
    private TreeMap<String, Double> differenceCur;  /* calculated */
    /* Below apply to Expense type = 'GOODS' only. */
    private TreeMap<String, Double> consumptionPcs; /* CHANGEABLE */
    private TreeMap<String, Double> consumptionCur; /* calculated */
    private TreeMap<String, Double> stockPcs;       /* calculated */
    private TreeMap<String, Double> stockCur;       /* calculated */
    private TreeMap<String, Double> requirementPcs; /* calculated */
    private TreeMap<String, Double> requirementCur; /* calculated */
    private TreeMap<String, Double> plannedPcs;     /* CHANGEABLE */
    private TreeMap<String, Double> actualPcs;      /* calculated */
    private TreeMap<String, Double> differencePcs;  /* calculated */

    // Constructors for the case of selection of Expense from database tables.
    /**
     * EntityExpense Constructor for the case of selection from database tables, 
     * initializes Constant parameters and Fixed parameters only.
     * 
     * Constant parameters for each Expense Category:
     * @param id
     * @param type
     * 
     * Common Fixed parameter variables for all Expense types:
     * @param name
     * @param accountId
     * @param accountLinked
     * @param linkedToComplexId
     * 
     * Fixed parameter variables for 'GOODS' Expense type only:
     * @param price
     * @param currentStockPcs
     * @param currentStockCur
     * @param currentStockWscPcs
     * @param currentStockWscCur
     * @param safetyStockPcs
     * @param safetyStockCur
     * @param orderQtyPcs
     * @param orderQtyCur 
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
        
        if (type.equals("GOODS")) {
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
     * EntityExpense Constructor, initializes Constant parameters and Fixed 
     * parameters only (receives user-changeable Fixed parameters and 
     * calculates application-calculated Fixed parameters).
     * 
     * Constant parameters for each Expense Category:
     * @param id
     * @param type
     * 
     * Common Fixed parameter variables for all Expense types:
     * @param name
     * @param accountId
     * @param accountLinked
     * @param linkedToComplexId
     * 
     * User-changeable Fixed parameter variables for 'GOODS' Expense type only:
     * @param price
     * @param currentStockPcs
     * @param safetyStockPcs
     * @param orderQtyPcs
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
        
        if (type.equals("GOODS")) {
            this.price = price;
            this.currentStockPcs = currentStockPcs;
            this.safetyStockPcs = safetyStockPcs;
            this.orderQtyPcs = orderQtyPcs;
            calculateFixedParameters();
        }
    }    

    /**
     * EntityExpense Constructor for the case of selection from database tables, 
     * initializes Constant parameters, Fixed parameters and 
     * changeable Variable parameters.
     * Needed when changeable Variable parameters obtained from database for
     * further calculated Variable parameters calculation.
     * 
     * Constant parameters for each Expense Category:
     * @param id
     * @param type
     * 
     * Common Fixed parameter variables for all Expense types:
     * @param name
     * @param accountId
     * @param accountLinked
     * @param linkedToComplexId
     * 
     * Fixed parameter variables for 'GOODS' Expense type only:
     * @param price
     * @param currentStockPcs
     * @param currentStockCur
     * @param currentStockWscPcs
     * @param currentStockWscCur
     * @param safetyStockPcs
     * @param safetyStockCur
     * @param orderQtyPcs
     * @param orderQtyCur
     * 
     * Collection of time period dates (common for any type of Expenses):
     * @param timePeriodDates - time period dates (in ISO 8601 YYYY-MM-DD 
     *                          format) from database.
     * 
     * Changeable Variable parameters:
     * @param plannedCur - TreeMap<String, Double> of planned expenses (in currency)
     *                     mapped to time period dates (in ISO 8601 YYYY-MM-DD 
     *                     format).
     * @param consumptionPcs - TreeMap<String, Double> of consumption (pcs) mapped
     *                         to time period dates (in ISO 8601 YYYY-MM-DD 
     *                         format).
     * @param plannedPcs - TreeMap<String, Double> of planned expenses (in pcs)
     *                     mapped to time period dates (in ISO 8601 YYYY-MM-DD 
     *                     format).              
     */
    public EntityExpense(int id, String type, String name, int accountId, 
            String accountLinked, int linkedToComplexId, Double price,
            Double currentStockPcs, Double currentStockCur,
            Double currentStockWscPcs, Double currentStockWscCur,
            Double safetyStockPcs, Double safetyStockCur, Double orderQtyPcs, 
            Double orderQtyCur,
            TreeSet<String> timePeriodDates,
            TreeMap<String, Double> plannedCur, 
            TreeMap<String, Double> consumptionPcs,
            TreeMap<String, Double> plannedPcs) {
 
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
        
        if (type.equals("GOODS")) {
            this.price = price;
            this.currentStockPcs = currentStockPcs;
            this.currentStockCur = currentStockCur;
            this.currentStockWscPcs = currentStockWscPcs;
            this.currentStockWscCur = currentStockWscCur;
            this.safetyStockPcs = safetyStockPcs;
            this.safetyStockCur = safetyStockCur;
            this.orderQtyPcs = orderQtyPcs;
            this.orderQtyCur = orderQtyCur;
            
            this.consumptionPcs = consumptionPcs;
            this.plannedPcs = plannedPcs;              
        } else if (type.equals("SIMPLE_EXPENSES")) {
            this.plannedCur = plannedCur;
        }
    }

    /**
     * Method calculates all application-calculated Fixed parameters within
     * the calculational EntityExpense object based on the user-changeable 
     * Fixed parameters, only for Expenses with type = "GOODS".
     * User-changeable Fixed parameter list:
     * price - planning-purpose value of price (price as planning basis).
     * 
     * currentStockPcs - current stock in pcs.
     * safetyStockPcs - safety stock in pcs.
     * orderQtyPcs - normal order quantity in pcs.
     * 
     * Application-calculated Fixed parameter list:
     * currentStockCur - current stock in currency.
     * currentStockWscPcs - current stock with safety stock consideration in pcs.
     * currentStockWscCur - current stock with safety stock consideration in currency.
     * safetyStockCur - safety stock in currency.
     * orderQtyCur - normal order quantity in currency.
     * @return 
     */
    public final boolean calculateFixedParameters() {
        if (type.equals("GOODS")) {
            currentStockCur = round(price * currentStockPcs, 2);
            currentStockWscPcs = currentStockPcs - safetyStockPcs;
            currentStockWscCur = round(price * currentStockWscPcs, 2);
            safetyStockCur = round(price * safetyStockPcs, 2);
            orderQtyCur = round(price * orderQtyPcs, 2);
        }
        return true;
    }
    
    
//    public void calculateVariableParameters() {
//        if (type.equals("GOODS")) {
//            for (Map.Entry e : )
//        }
//    }
       
    @Override
    public String toString() {
        return "EntityExpense{" + "id=" + id + ", type=" + type + ", name=" 
                + name + ", accountId=" + accountId + ", accountLinked=" 
                + accountLinked + ", linkedToComplexId=" + linkedToComplexId 
                + ", price=" + price + ", currentStockPcs=" + currentStockPcs 
                + ", currentStockCur=" + currentStockCur 
                + ", currentStockWscPcs=" + currentStockWscPcs 
                + ", currentStockWscCur=" + currentStockWscCur 
                + ", safetyStockPcs=" + safetyStockPcs + ", safetyStockCur=" 
                + safetyStockCur + ", orderQtyPcs=" + orderQtyPcs 
                + ", orderQtyCur=" + orderQtyCur + '}';
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
}
