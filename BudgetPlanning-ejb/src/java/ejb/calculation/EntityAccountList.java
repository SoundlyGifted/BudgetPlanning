
package ejb.calculation;

import java.util.ArrayList;

/**
 *
 * @author SoundlyGifted
 */
public class EntityAccountList {
        
    private EntityAccountList(){}
    
    static ArrayList<EntityAccount> accountList;

    static ArrayList<EntityAccount> getEntityAccountList() {
        if (accountList == null) {
            accountList = new ArrayList<>();
        }
        return accountList;
    }

    static void setEntityAccountList(ArrayList<EntityAccount> accountList) {
        EntityAccountList.accountList = accountList;
    }
    
    static void removeEntityAccountList(){
        if (accountList != null) {
            EntityAccountList.accountList.clear();
            EntityAccountList.accountList = null;
        }
    }
}
