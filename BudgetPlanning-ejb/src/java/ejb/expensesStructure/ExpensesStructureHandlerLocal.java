
package ejb.expensesStructure;

import ejb.entity.EntityExpense;
import java.util.ArrayList;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ExpensesStructureHandlerLocal {
   
    public ArrayList<EntityExpense> getEntityExpenseList();
    
    public void replaceEntityExpenseList(ArrayList<EntityExpense> list);
    
    public void addToEntityExpenseList(EntityExpense entity);
    
    public void removeFromEntityExpenseList(EntityExpense entity);
    
    public EntityExpense selectFromEntityExpenseListByName(String name);
    
    public EntityExpense selectFromEntityExpenseListById(Integer id);
}
