
package ejb.entityLists;

import ejb.DBConnection.DBConnectionLocal;
import ejb.entity.EntityExpense;
import ejb.expensesStructure.ExpensesStructureSQLSelectLocal;
import java.sql.Connection;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author SoundlyGifted
 */
@Singleton
@Startup
public class ExpensesHandler implements ExpensesHandlerLocal {

    @EJB
    private ExpensesStructureSQLSelectLocal expensesStructureSelect;
    
    @EJB
    private DBConnectionLocal connector;
    
    
    /**
     * Method to add an EntityExpense element to the EntityExpenseList 
     * collection.
     * @param entity EntityExpense element to be added.
     */
    @Override
    public void addToEntityExpenseList(EntityExpense entity) {
        EntityExpenseList.getEntityExpenseList().add(entity);
    }
    
    /**
     * Method to remove an EntityExpense element from the EntityExpenseList
     * collection
     * @param entity EntityExpense element to be removed.
     */
    @Override
    public void removeFromEntityExpenseList(EntityExpense entity) {
        EntityExpenseList.getEntityExpenseList().remove(entity);
    }
    
    /**
     * Gets collection of EntityExpense elements (EntityExpenseList) for 
     * further usage (calculation of parameters).
     * @return EntityExpenseList collection.
     */
    @Override
    public ArrayList<EntityExpense> getEntityExpenseList() {
        return EntityExpenseList.getEntityExpenseList();
    }
    
    /**
     * Replaces current collection of EntityExpense elements (EntityExpenseList)
     * with the given EntityExpense list.
     * @param list EnitiyExpense list given for replacement.
     */
    @Override
    public void replaceEntityExpenseList(ArrayList<EntityExpense> list) {
        // Clear current expenses list.
        EntityExpenseList.removeEntityExpenseList(); 
        // Write to common Entity Objects List.
        EntityExpenseList.setEntityExpenseList(list); 
    }
    
    /**
     * Method to get a certain EntityExpense element from EntityExpenseList
     * collection by name.
     * @param name - the value of "name" variable of the EntityExpense to be 
     *               selected.
     * @return EntityExpense with the given name.
     */
    @Override
    public EntityExpense selectFromEntityExpenseListByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        ArrayList<EntityExpense> list = getEntityExpenseList();
        for (EntityExpense e : list) {
            if (name.equals(e.getName())) {
                return e;
            }
        }
        return null;
    }
    
    /**
     * Method to get a certain EntityExpense element from EntityExpenseList
     * collection by id.
     * @param id - the value of "id" variable of the EntityExpense to be 
     *             selected.
     * @return EntityExpense with the given id.
     */
    @Override
    public EntityExpense selectFromEntityExpenseListById(Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        ArrayList<EntityExpense> list = getEntityExpenseList();
        for (EntityExpense e : list) {
            if (id == e.getId()) {
                return e;
            }
        }
        return null;
    }
}
