
package ejb.entityLists;

import ejb.entity.EntityExpense;
import java.util.ArrayList;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ExpensesHandlerLocal {
   
    public ArrayList<EntityExpense> getEntityExpenseList();
    
    public void replaceEntityExpenseList(ArrayList<EntityExpense> list);
    
    public void addToEntityExpenseList(EntityExpense entity);
    
    public void removeFromEntityExpenseList(EntityExpense entity);
    
    public EntityExpense selectFromEntityExpenseListByName(String name);
    
    public EntityExpense selectFromEntityExpenseListById(Integer id);
}
