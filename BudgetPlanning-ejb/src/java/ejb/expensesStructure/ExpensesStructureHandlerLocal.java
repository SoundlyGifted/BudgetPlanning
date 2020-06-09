
package ejb.expensesStructure;

import ejb.entity.EntityExpense;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ExpensesStructureHandlerLocal {
    
    public List<String> getExpenseTypeList();
    
    public ArrayList<EntityExpense> getEntityExpenseList();
    
    public void replaceEntityExpenseList(ArrayList<EntityExpense> list);
    
    public void addToEntityExpenseList(EntityExpense entity);
    
    public void removeFromEntityExpenseList(EntityExpense entity);
    
    public EntityExpense selectFromEntityExpenseListByNameAndTitle(String name, 
            String title);
    
    public EntityExpense selectFromEntityExpenseListById(Integer id);
}
