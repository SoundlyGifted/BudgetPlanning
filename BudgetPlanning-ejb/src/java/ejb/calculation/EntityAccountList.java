
package ejb.calculation;

import java.util.ArrayList;

/**
 * EntityAccountList class is used to hold collection of EntityAccount objects.
 */
public class EntityAccountList {
        
    private EntityAccountList(){}
    
    static ArrayList<EntityAccount> accountList;

    /**
     * Returns current collection of EntityAccount objects that is contained
     * within EntityAccountList class.
     * 
     * @return collection of EntityAccount objects of the EntityAccountList 
     * class.
     */
    static ArrayList<EntityAccount> getEntityAccountList() {
        if (accountList == null) {
            accountList = new ArrayList<>();
        }
        return accountList;
    }

    /**
     * Sets collection of EntityAccount objects of the EntityAccountList class
     * based on the given collection of EntityAccount objects.
     * 
     * @param accountList given collection of EntityAccount objects.
     */
    static void setEntityAccountList(ArrayList<EntityAccount> accountList) {
        EntityAccountList.accountList = accountList;
    }
    
    /**
     * Releases EntityAccountList collection and sets link to the collection
     * to null.
     */
    static void removeEntityAccountList(){
        if (accountList != null) {
            EntityAccountList.accountList.clear();
            EntityAccountList.accountList = null;
        }
    }
}
