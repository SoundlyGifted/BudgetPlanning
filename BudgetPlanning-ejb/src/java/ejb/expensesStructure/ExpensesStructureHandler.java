
package ejb.expensesStructure;

import ejb.DBConnection.DBConnectionLocal;
import ejb.entity.EntityExpense;
import ejb.entity.EntityExpenseList;
import java.sql.Connection;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author SoundlyGifted
 */
@Singleton
@Startup
public class ExpensesStructureHandler implements ExpensesStructureHandlerLocal {

    @EJB
    private ExpensesStructureSQLSelectLocal expensesStructureSelect;
    
    @EJB
    private DBConnectionLocal connector;
    
    @PostConstruct
    public void initialize() {
        System.out.println("*** ExpensesStructureHandler: initialize() called. ***");
        Connection connection = connector.connection();
        replaceEntityExpenseList(expensesStructureSelect.executeSelectAll(connection));
        connector.closeConnection(connection);
    }
    
    @Override
    public void addToEntityExpenseList(EntityExpense entity) {
        EntityExpenseList.getEntityExpenseList().add(entity);
    }
    
    @Override
    public void removeFromEntityExpenseList(EntityExpense entity) {
        EntityExpenseList.getEntityExpenseList().remove(entity);
    }
    
    @Override
    public ArrayList<EntityExpense> getEntityExpenseList() {
        return EntityExpenseList.getEntityExpenseList();
    }
    
    /* Write List<EntityExpense> from SQL select query to common Entity Objects List.
     * Currently existing Entity Objects List is completely replaced.
     */
    @Override
    public void replaceEntityExpenseList(ArrayList<EntityExpense> list) {
        EntityExpenseList.removeEntityExpenseList(); /* Clear current expenses list. */
        EntityExpenseList.setEntityExpenseList(list); /* Write to common Entity Objects List*/
    }
    
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
