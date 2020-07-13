
package ejb.entity;

import java.util.Objects;

/**
 *
 * @author SoundlyGifted
 */
public class EntityExpense {
    
    /* Constant parameters for each Expense Category. */
    private final int id;      /* fixed */
    private final String type; /* fixed */
    
    /* Common Fixed parameter variables for all Expense types. */    
    private String name;            /* changeable */
    private int accountId;          /* changeable */
    private String accountLinked;   /* changeable */
    private int linkedToComplexId;  /* changeable */
    
    /* Fixed parameter variables for 'GOODS' Expense type only. */
    private double price;               /* changeable */
    private double currentStockPcs;     /* changeable and calculated */
    private double currentStockCur;     /* calculated */
    private double currentStockWscPcs;  /* calculated */
    private double currentStockWscCur;  /* calculated */
    private double safetyStockPcs;      /* changeable */
    private double safetyStockCur;      /* calculated */
    private double orderQtyPcs;         /* changeable */
    private double orderQtyCur;         /* calculated */

    /* Constructor for Selection from Database case. */
    public EntityExpense(int id, String type, String name, int accountId, 
            String accountLinked, int linkedToComplexId, double price,
            double currentStockPcs, double currentStockCur,
            double currentStockWscPcs, double currentStockWscCur,
            double safetyStockPcs, double safetyStockCur, double orderQtyPcs, 
            double orderQtyCur) {
        
        this.id = id;
        this.type = type;
        this.name = name;
        this.accountId = accountId;
        this.accountLinked = accountLinked;
        this.linkedToComplexId = linkedToComplexId;
        
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
}
