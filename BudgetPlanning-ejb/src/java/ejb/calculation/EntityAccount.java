
package ejb.calculation;

import ejb.common.EjbCommonMethods;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * EntityAccount class is used to perform calculations of attributes and 
 * parameters of a certain Account database representation and hold the 
 * calculated values until they are placed into the database.
 */
public class EntityAccount extends EjbCommonMethods {
    
    /* Constant parameter. */
    private int id;
    /* Fixed (user-changeable) parameters. */
    private String name;
    private double currentRemainderCur;
    
    /* Variable parameter variables (values depend on time period dates). */    
    private TreeMap<String, Double> plannedRemainderCur; /* calculated */
    private TreeMap<String, Double> incomeCur;           /* CHANGEABLE */
    private TreeMap<String, Double> plannedSumCur;       /* CHANGEABLE */

    // Flag to indicate if Account contains performed variable parameters 
    // calculations.
    private boolean calculated = false;    

    /**
     * Constructor of the EntityAccount class.
     * 
     * @param id database Account ID.
     * @param name Account name.
     * @param currentRemainderCur Current Remainder (CUR) attribute of the 
     * Account.
     */
    public EntityAccount(int id, String name, double currentRemainderCur) {
        this.id = id;
        this.name = name;
        this.currentRemainderCur = currentRemainderCur;
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
     * Resets all variable parameters in this EntityAccount object which means
     * their values will be cleared (if parameter exists) and the "calculated"
     * flag of the object (representing the parameter calculation status of the
     * object) will be set to "false".
     */        
    public void resetVariableParams() {
        if (plannedRemainderCur != null && !plannedRemainderCur.isEmpty()) {
            plannedRemainderCur.clear();
        }
        if (incomeCur != null && !incomeCur.isEmpty()) {
            incomeCur.clear();
        }        
        if (plannedSumCur != null && !plannedSumCur.isEmpty()) {
            plannedSumCur.clear();
        }
        calculated = false;
    }              

    /**
     * Calculates variable parameters of this EntityAccount object based on
     * data within the object and the domain specific formulae.
     * 
     * @param timePeriodDates set of planning period dates.
     */
    public void calculateVariableParameters(TreeSet<String> 
            timePeriodDates) {
        
        plannedRemainderCur = initializeVariableParam(plannedRemainderCur);
        
        Double plannedRemainderCurVal = currentRemainderCur;
        Double incomeCurVal;
        Double plannedSumCurVal;
        for (String date : timePeriodDates) {
            incomeCurVal = incomeCur.get(date);
            plannedSumCurVal = plannedSumCur.get(date);
            if (incomeCurVal == null) {
                incomeCurVal = (double) 0;
            }
            if (plannedSumCurVal == null) {
                plannedSumCurVal = (double) 0;
            }
            plannedRemainderCurVal = round(plannedRemainderCurVal 
                    - plannedSumCurVal + incomeCurVal, 2);
            plannedRemainderCur.put(date, plannedRemainderCurVal);
        }
        calculated = true;
    }
    
    @Override
    public String toString() {
        return "EntityAccount{<br>" 
                + "Constant parameter :<br>"
                + "id=" + id
                + "<br>Fixed (user-changeable) parameters :<br>"
                + "name=" + name 
                + ", currentRemainderCur=" + currentRemainderCur
                + "<br>Variable parameters :<br>"
                + "plannedRemainderCur=" + plannedRemainderCur 
                + "<br>incomeCur=" + incomeCur 
                + "<br>plannedSumCur=" + plannedSumCur
                + "<br>"
                + "calculated=" + calculated + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + (int) (Double
                .doubleToLongBits(this.currentRemainderCur) 
                ^ (Double.doubleToLongBits(this.currentRemainderCur) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.plannedRemainderCur);
        hash = 97 * hash + Objects.hashCode(this.incomeCur);
        hash = 97 * hash + Objects.hashCode(this.plannedSumCur);
        hash = 97 * hash + (this.calculated ? 1 : 0);
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
        final EntityAccount other = (EntityAccount) obj;
        if (this.id != other.id) {
            return false;
        }
        if (Double.doubleToLongBits(this.currentRemainderCur) 
                != Double.doubleToLongBits(other.currentRemainderCur)) {
            return false;
        }
        if (this.calculated != other.calculated) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.plannedRemainderCur, other
                .plannedRemainderCur)) {
            return false;
        }
        if (!Objects.equals(this.incomeCur, other.incomeCur)) {
            return false;
        }
        if (!Objects.equals(this.plannedSumCur, other.plannedSumCur)) {
            return false;
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCurrentRemainderCur() {
        return currentRemainderCur;
    }

    public void setCurrentRemainderCur(double currentRemainderCur) {
        this.currentRemainderCur = currentRemainderCur;
    }

    public TreeMap<String, Double> getPlannedRemainderCur() {
        return plannedRemainderCur;
    }

    public TreeMap<String, Double> getIncomeCur() {
        return incomeCur;
    }

    public void setIncomeCur(TreeMap<String, Double> incomeCur) {
        this.incomeCur = incomeCur;
    }

    public TreeMap<String, Double> getPlannedSumCur() {
        return plannedSumCur;
    }

    public void setPlannedSumCur(TreeMap<String, Double> plannedSumCur) {
        this.plannedSumCur = plannedSumCur;
    }

    public boolean isCalculated() {
        return calculated;
    }

}
