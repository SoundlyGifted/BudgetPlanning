
package ejb.entity;

import java.util.ArrayList;

/**
 *
 * @author SoundlyGifted
 */
public class EntityExpenseList {
    
    private EntityExpenseList(){};
    
    public static ArrayList<EntityExpense> expenseList = null;

    public static ArrayList<EntityExpense> getEntityExpenseList() {
        return expenseList;
    }

    public static void setEntityExpenseList(ArrayList<EntityExpense> expenseList) {
        EntityExpenseList.expenseList = expenseList;
    }
    
    public static void removeEntityExpenseList(){
        if (expenseList != null) {
            EntityExpenseList.expenseList.clear();
            EntityExpenseList.expenseList = null;
        }
    }
}
