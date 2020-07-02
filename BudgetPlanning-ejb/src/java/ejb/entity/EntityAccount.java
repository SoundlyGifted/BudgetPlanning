
package ejb.entity;

import java.util.Objects;

/**
 *
 * @author SoundlyGifted
 */
public class EntityAccount {
    
    private int id;
    private String name;
    private double currentRemainder;

    public EntityAccount(int id, String name, double currentRemainder) {
        this.id = id;
        this.name = name;
        this.currentRemainder = currentRemainder;
    }

    @Override
    public String toString() {
        return "EntityAccount{" + "id=" + id + ", name=" + name 
                + ", currentRemainder=" + currentRemainder + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + this.id;
        hash = 19 * hash + Objects.hashCode(this.name);
        hash = 19 * hash + (int) (Double.doubleToLongBits(this.currentRemainder) 
                ^ (Double.doubleToLongBits(this.currentRemainder) >>> 32));
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
        if (Double.doubleToLongBits(this.currentRemainder) 
                != Double.doubleToLongBits(other.currentRemainder)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
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

    public double getCurrentRemainder() {
        return currentRemainder;
    }

    public void setCurrentRemainder(double currentRemainder) {
        this.currentRemainder = currentRemainder;
    }

}
