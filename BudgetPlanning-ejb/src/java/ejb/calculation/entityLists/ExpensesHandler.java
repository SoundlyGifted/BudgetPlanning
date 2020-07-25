
package ejb.calculation.entityLists;

import ejb.calculation.EntityExpense;
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
    private ExpensesStructureSQLSelectLocal select;
          
    /**
     * Method to remove an EntityExpense element from the EntityExpenseList
     * collection
     * 
     * @param entity EntityExpense element to be removed.
     */
    @Override
    public void removeFromEntityExpenseList(EntityExpense entity) {
        EntityExpenseList.getEntityExpenseList().remove(entity);
    }
    
    /**
     * Gets collection of EntityExpense elements (EntityExpenseList) for 
     * further usage (calculation of parameters).
     * 
     * @return EntityExpenseList collection.
     */
    @Override
    public ArrayList<EntityExpense> getEntityExpenseList() {
        return EntityExpenseList.getEntityExpenseList();
    }
    
    /**
     * Replaces current collection of EntityExpense elements (EntityExpenseList)
     * with the given EntityExpense list.
     * 
     * @param list EnitiyExpense list given for replacement.
     */
    private void replaceEntityExpenseList(ArrayList<EntityExpense> list) {
        // Clear current expenses list.
        EntityExpenseList.removeEntityExpenseList(); 
        // Write to common Entity Objects List.
        EntityExpenseList.setEntityExpenseList(list); 
    }
    
    /**
     * Method to get a certain EntityExpense element from EntityExpenseList
     * collection by name.
     * If EntityExpense with the given name does not exist in the 
     * EntityExpenseList then it is created and placed to the collection based 
     * on the database record. If exists in the EntityExpenseList then it's 
     * parameters are updated based on the database record. If no such record 
     * in the database then returns null.
     * Method also checks whether any Complex EntityExpense linked to the
     * EntityExpense with given name exists in the EntityExpenseList in the way 
     * like described above.
     * 
     * @param connection database connection.
     * @param name the value of "name" variable of the EntityExpense to be 
     *             selected.
     * @return EntityExpense with the given name if exists in the 
     *         EntityExpenseList and database record exists, null otherwise.
     */
    @Override
    public EntityExpense selectFromEntityExpenseListByName(
            Connection connection, String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        /* EntityExpenseList (where calculation objects stored). */
        ArrayList<EntityExpense> list = getEntityExpenseList();
        
        /* EntityExpense based on the database record. */
        EntityExpense expenseDB = select.executeSelectByName(connection, name);
        if (expenseDB == null) {
            return null;
        }        
        
        /* Checking if linked Complex Expense exists in the 
           EntityExpenseList and add if it does not exist. */
        int complexId = expenseDB.getLinkedToComplexId();
        if (complexId != 0) {
            selectFromEntityExpenseListById(connection, complexId);
        }        
        
        for (EntityExpense e : list) {
            if (name.equals(e.getName())) {
                /* EntityExpense with given name exists in the EntityExpense 
                   list - Updating it's parameters based on the database 
                   record. */
                int i = list.indexOf(e);
                list.set(i, expenseDB);
                return expenseDB;
            }
        }
        /* EntityExpnese with given name does not exist in the EntityExpenseList
           - adding EntityExpense with given id to this collection based on
           the database record. */
        list.add(expenseDB);
        return expenseDB;
    }
    
    /**
     * Method to get a certain EntityExpense element from EntityExpenseList
     * collection by id.
     * If EntityExpense with the given id does not exist in the 
     * EntityExpenseList then it is created and placed to the collection 
     * based on the database record. If exists in the EntityExpenseList then
     * it's parameters are updated based on the database record. If no such 
     * record in the database then returns null.
     * Method also checks whether any Complex EntityExpense linked to the
     * EntityExpense with given id exists in the EntityExpenseList in the way 
     * like described above.
     * 
     * @param connection database connection.
     * @param id the value of "id" variable of the EntityExpense to be 
     *           selected.
     * @return EntityExpense with the given id if exists in the 
     *         EntityExpenseList and database record exists, null otherwise.
     */
    @Override
    public EntityExpense selectFromEntityExpenseListById(Connection connection,
            Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        /* EntityExpenseList (where calculation objects stored). */
        ArrayList<EntityExpense> list = getEntityExpenseList();

        /* EntityExpense based on the database record. */
        EntityExpense expenseDB = select.executeSelectById(connection, id);
        if (expenseDB == null) {
            return null;
        }

        /* Checking if linked Complex Expense exists in the 
           EntityExpenseList and add if it does not exist. */
        int complexId = expenseDB.getLinkedToComplexId();
        if (complexId != 0) {
            selectFromEntityExpenseListById(connection, complexId);
        }

        for (EntityExpense e : list) {
            if (id == e.getId()) {
                /* EntityExpense with given id exists in the EntityExpense list 
                   - Updating it's parameters based on the database record. */
                int i = list.indexOf(e);
                list.set(i, expenseDB);
                return expenseDB;
            }
        }
        /* EntityExpnese with given id does not exist in the EntityExpenseList
           - adding EntityExpense with given id to this collection based on
           the database record. */
        list.add(expenseDB);
        return expenseDB;
    }
    
    /**
     * Actualizes EntityExpenseList (replaces it with the list obtained from
     * the database).
     * 
     * @param connection database connection.
     * @return EntityExpenseList obtained based on the database records.
     */
    @Override
    public ArrayList<EntityExpense> actualizeEntityExpenseList(Connection 
            connection) {
        ArrayList<EntityExpense> expenseListDB = 
                select.executeSelectAll(connection);
        replaceEntityExpenseList(expenseListDB);
        return expenseListDB;
    }
    
}
