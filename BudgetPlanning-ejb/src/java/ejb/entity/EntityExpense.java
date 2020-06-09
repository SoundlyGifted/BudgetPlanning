
package ejb.entity;

import java.util.Objects;

/**
 *
 * @author SoundlyGifted
 */
public class EntityExpense {
    
    private int id; /* Primary key */
    private String type; /* not null */
    private String name; /* not null, unique*/
    private String accountLinked;
    private int linkedToComplexId;
    private int price;
    private int safetyStock;
    private int orderQty;
    private String shopName;

    public EntityExpense(int id, String type, String name, String accountLinked, 
            int linkedToComplexId, int price, int safetyStock, 
            int orderQty, String shopName) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.accountLinked = accountLinked;
        this.linkedToComplexId = linkedToComplexId;
        this.price = price;
        this.safetyStock = safetyStock;
        this.orderQty = orderQty;
        this.shopName = shopName;
    }

    @Override
    public String toString() {
        return "EntityExpense{" + "id=" + id + ", type=" + type + ", name=" + 
                name + ", accountLinked=" + accountLinked + 
                ", linkedToComplexId=" + linkedToComplexId 
                + ", price=" + price + ", safetyStock=" + safetyStock + 
                ", orderQty=" + orderQty + ", shopName=" + shopName + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.id;
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.accountLinked);
        hash = 89 * hash + this.linkedToComplexId;
        hash = 89 * hash + this.price;
        hash = 89 * hash + this.safetyStock;
        hash = 89 * hash + this.orderQty;
        hash = 89 * hash + Objects.hashCode(this.shopName);
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
        if (this.price != other.price) {
            return false;
        }
        if (this.safetyStock != other.safetyStock) {
            return false;
        }
        if (this.orderQty != other.orderQty) {
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
        if (!Objects.equals(this.shopName, other.shopName)) {
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(int safetyStock) {
        this.safetyStock = safetyStock;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

}
