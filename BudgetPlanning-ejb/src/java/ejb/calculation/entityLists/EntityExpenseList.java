
package ejb.calculation.entityLists;

import ejb.calculation.EntityExpense;
import java.util.ArrayList;

/**
 *
 * @author SoundlyGifted
 */
public class EntityExpenseList {
    
    private EntityExpenseList(){}
    
    static ArrayList<EntityExpense> expenseList;

    static ArrayList<EntityExpense> getEntityExpenseList() {
        if (expenseList == null) {
            expenseList = new ArrayList<>();
        }
        return expenseList;
    }

    static void setEntityExpenseList(ArrayList<EntityExpense> expenseList) {
        EntityExpenseList.expenseList = expenseList;
    }
    
    static void removeEntityExpenseList(){
        if (expenseList != null) {
            EntityExpenseList.expenseList.clear();
            EntityExpenseList.expenseList = null;
        }
    }
}
