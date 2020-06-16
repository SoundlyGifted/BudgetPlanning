
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
    private String accountLinked;
    private int linkedToComplexId;
    private double price;
    private double safetyStock;
    private double orderQty;

    public EntityExpense(int id, String type, String name, String accountLinked, 
            int linkedToComplexId, double price, double safetyStock, 
            double orderQty) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.accountLinked = accountLinked;
        this.linkedToComplexId = linkedToComplexId;
        this.price = price;
        this.safetyStock = safetyStock;
        this.orderQty = orderQty;
    }

    @Override
    public String toString() {
        return "EntityExpense{" + "id=" + id + ", type=" + type + ", name=" + 
                name + ", accountLinked=" + accountLinked + 
                ", linkedToComplexId=" + linkedToComplexId 
                + ", price=" + price + ", safetyStock=" + safetyStock + 
                ", orderQty=" + orderQty + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.id;
        hash = 47 * hash + Objects.hashCode(this.type);
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Objects.hashCode(this.accountLinked);
        hash = 47 * hash + this.linkedToComplexId;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.price) ^ (Double.doubleToLongBits(this.price) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.safetyStock) ^ (Double.doubleToLongBits(this.safetyStock) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.orderQty) ^ (Double.doubleToLongBits(this.orderQty) >>> 32));
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
        if (this.linkedToComplexId != other.linkedToComplexId) {
            return false;
        }
        if (Double.doubleToLongBits(this.price) != Double.doubleToLongBits(other.price)) {
            return false;
        }
        if (Double.doubleToLongBits(this.safetyStock) != Double.doubleToLongBits(other.safetyStock)) {
            return false;
        }
        if (Double.doubleToLongBits(this.orderQty) != Double.doubleToLongBits(other.orderQty)) {
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

    public double getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(double safetyStock) {
        this.safetyStock = safetyStock;
    }

    public double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(double orderQty) {
        this.orderQty = orderQty;
    }
}
