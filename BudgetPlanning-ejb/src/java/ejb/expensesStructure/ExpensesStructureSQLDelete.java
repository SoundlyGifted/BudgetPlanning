
package ejb.expensesStructure;

import ejb.entity.EntityExpense;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class ExpensesStructureSQLDelete extends ExpensesStructureSQLAbstract
        implements ExpensesStructureSQLDeleteLocal {

    @EJB
    ExpensesStructureSQLSelectLocal select;
    
    @EJB
    ExpensesStructureHandlerLocal handler;
    
    @EJB
    ExpensesStructureSQLUpdateLocal update;
    
    private PreparedStatement preparedStatement;
    
    @Override
    public boolean execute(Connection connection, String name, String title) {
        if (!inputCheckNullBlank(name) || !inputCheckLength(name) ||
                !inputCheckLength(title)) {
            return false;
        } 
        /* Check if both DB and Expense Object List has record / object with 
        corresponding name and title given. */
        EntityExpense expenseDB = 
                select.executeSelectByNameAndTitle(connection, name, title);
        EntityExpense expense = 
                handler.selectFromEntityExpenseListByNameAndTitle(name, title);
        if (expenseDB == null || expense == null) {
            return false;
        }
        Integer idInt = expense.getId();
        try {
            preparedStatement = createPreparedStatement(connection, "delete.expense.bynameandtitle");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, title);
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLDelete: execute()"
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }
        try {
            preparedStatement.executeUpdate();
            /* If COMPLEX_EXPENSES expense is removed then setting to 0 all 
            LINKED_TO_COMPLEX_ID fields that had that complex expense id. 
            If other type of expense is removed then simply removing it from 
            Object Expense List.*/
            if (expense.getType().equals("COMPLEX_EXPENSES")) {
                ArrayList<EntityExpense> expenseListDB = 
                        select.executeSelectAll(connection);
                ArrayList<EntityExpense> expenseList = handler.getEntityExpenseList();
                for (EntityExpense e : expenseListDB) {
                    if (e.getLinkedToComplexId() == idInt) {
                        update.clearAssignmentToComplexExpense(connection, 
                                e.getName(), e.getTitle());
                    }
                }
                for (EntityExpense e : expenseList) {
                    if (e.getLinkedToComplexId() == idInt) {
                        e.setLinkedToComplexId(0);
                    }
                }
            }
            handler.removeFromEntityExpenseList(expense);
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLDelete: "
                    + "execute() Error while executing Delete Query: "
                    + ex.getMessage() + "***");
            return false;
        } finally {
            clear();
        }
        return true;
    }
    
    @Override
    public boolean execute(Connection connection, String id) {
        Integer idInt = stringToInt(id);
        if (!inputCheckNullBlank(id) || idInt == null || idInt < 1) {
            return false;
        }
        /* Check if both DB and Expense Object List has record / object with 
        corresponding name and title given. */
        EntityExpense expenseDB = select.executeSelectById(connection, idInt);
        EntityExpense expense = handler.selectFromEntityExpenseListById(idInt);
        if (expenseDB == null || expense == null) {
            return false;
        }

        try {
            preparedStatement = createPreparedStatement(connection, 
                    "delete.expense.byid");
            preparedStatement.setInt(1, idInt);
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLDelete: execute()"
                    + "SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }
        try {
            preparedStatement.executeUpdate();
            /* If COMPLEX_EXPENSES expense is removed then setting to 0 all 
            LINKED_TO_COMPLEX_ID fields that had that complex expense id. 
            If other type of expense is removed then simply removing it from 
            Object Expense List.*/
            if (expense.getType().equals("COMPLEX_EXPENSES")) {
                ArrayList<EntityExpense> expenseListDB = 
                        select.executeSelectAll(connection);
                ArrayList<EntityExpense> expenseList = handler.getEntityExpenseList();
                for (EntityExpense e : expenseListDB) {
                    if (e.getLinkedToComplexId() == idInt) {
                        update.clearAssignmentToComplexExpense(connection, 
                                e.getName(), e.getTitle());
                    }
                }
                for (EntityExpense e : expenseList) {
                    if (e.getLinkedToComplexId() == idInt) {
                        e.setLinkedToComplexId(0);
                    }
                }
            }
            handler.removeFromEntityExpenseList(expense);
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLDelete: "
                    + "execute() Error while executing Delete Query: "
                    + ex.getMessage() + "***");
            return false;
        } finally {
            clear();
        }
        return true;
    }

    private void clear() {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                preparedStatement = null;
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    } 
    
}
