
package ejb.entity;

import java.util.Objects;

/**
 *
 * @author SoundlyGifted
 */
public class EntityExpense {
    
    private int id; /* Primary key */
    private String type;
    private String name;
    private int accountId;
    private String accountLinked;
    private int linkedToComplexId;
    private double price;
    private double safetyStockPcs;
    private double safetyStockCur;
    private double orderQtyPcs;
    private double orderQtyCur;

    public EntityExpense(int id, String type, String name, int accountId, 
            String accountLinked, int linkedToComplexId, double price, 
            double safetyStockPcs, double safetyStockCur, double orderQtyPcs, 
            double orderQtyCur) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.accountId = accountId;
        this.accountLinked = accountLinked;
        this.linkedToComplexId = linkedToComplexId;
        this.price = price;
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
                + ", price=" + price + ", safetyStockPcs=" + safetyStockPcs 
                + ", safetyStockCur=" + safetyStockCur + ", orderQtyPcs=" 
                + orderQtyPcs + ", orderQtyCur=" + orderQtyCur + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.id;
        hash = 47 * hash + Objects.hashCode(this.type);
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + this.accountId;
        hash = 47 * hash + Objects.hashCode(this.accountLinked);
        hash = 47 * hash + this.linkedToComplexId;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.price) 
                ^ (Double.doubleToLongBits(this.price) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.safetyStockPcs) 
                ^ (Double.doubleToLongBits(this.safetyStockPcs) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.safetyStockCur) 
                ^ (Double.doubleToLongBits(this.safetyStockCur) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.orderQtyPcs) 
                ^ (Double.doubleToLongBits(this.orderQtyPcs) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.orderQtyCur) 
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
