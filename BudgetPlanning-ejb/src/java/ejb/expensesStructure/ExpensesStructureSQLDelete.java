
package ejb.expensesStructure;

import ejb.calculation.ExpensesHandlerLocal;
import ejb.common.SQLAbstract;
import ejb.calculation.EntityExpense;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
public class ExpensesStructureSQLDelete extends SQLAbstract
        implements ExpensesStructureSQLDeleteLocal {

    @EJB
    private ExpensesStructureSQLSelectLocal select;

    @EJB
    private ExpensesHandlerLocal handler;

    @EJB
    private ExpensesStructureSQLUpdateLocal update;

    @Override
    public boolean executeDeleteById(Connection connection, String id) {
        Integer idInt = stringToInt(id);
        if (!inputCheckNullBlank(id) || idInt == null || idInt < 1) {
            return false;
        }

        PreparedStatement preparedStatement;
        try {
            preparedStatement = createPreparedStatement(connection,
                    "expensesStructure/delete.byid");
            preparedStatement.setInt(1, idInt);
        } catch (SQLException | IOException ex) {
            System.out.println("*** ExpensesStructureSQLDelete: "
                    + "executeDeleteById() SQL PreparedStatement failure: "
                    + ex.getMessage() + " ***");
            return false;
        }
        try {
            preparedStatement.executeUpdate();
            /* If COMPLEX_EXPENSES expense is being removed then setting to 0 
            all LINKED_TO_COMPLEX_ID fields that had that complex expense id.
            Removing the Expense from EntityExpenseList (regardless of type).*/
            EntityExpense expense = select.executeSelectById(connection, idInt);
            if (expense.getType().equals("COMPLEX_EXPENSES")) {
                ArrayList<EntityExpense> expenseList
                        = select.executeSelectAll(connection);
                for (EntityExpense e : expenseList) {
                    if (e.getLinkedToComplexId() == idInt) {
                        update.clearAssignmentToComplexExpense(connection,
                                e.getName());
                    }
                }
            }
            handler.removeFromEntityExpenseList(expense);
        } catch (SQLException ex) {
            System.out.println("***ExpensesStructureSQLDelete: "
                    + "executeDeleteById() Error while executing Delete Query: "
                    + ex.getMessage() + "***");
            return false;
        } finally {
            clear(preparedStatement);
        }
        return true;
    }
}
