
package ejb.expensesStructure;

import ejb.DBConnection.DBConnectionLocal;
import ejb.entity.EntityExpense;
import ejb.entity.EntityExpenseList;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
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

    private List<String> expenseTypeList;

    @EJB
    private ExpensesStructureSQLSelectLocal expensesStructureSelect;
    
    @EJB
    private DBConnectionLocal connector;
    
    @PostConstruct
    public void initialize() {
        System.out.println("*** ExpensesStructureHandler: initialize() called. ***");
        ExpensesTypes.ExpenseType[] expensesTypes
                = ExpensesTypes.ExpenseType.values();
        expenseTypeList = new ArrayList<>();
        for (ExpensesTypes.ExpenseType t : expensesTypes) {
            expenseTypeList.add(t.getType());
        }
        Connection connection = connector.connection();
        replaceEntityExpenseList(expensesStructureSelect.executeSelectAll(connection));
        connector.closeConnection(connection);
    }

    @Override
    public List<String> getExpenseTypeList() {
        return expenseTypeList;
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
    public EntityExpense selectFromEntityExpenseListByNameAndTitle(String name, 
            String title) {
        if (name == null || name.trim().isEmpty() || title == null) {
            return null;
        }
        ArrayList<EntityExpense> list = getEntityExpenseList();
        for (EntityExpense e : list) {
            if (name.equals(e.getName()) && title.equals(e.getTitle())) {
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
