
package com.ejb.expstructure;

import com.ejb.mainscreen.PlannedAccountsValuesSQLLocal;
import com.ejb.mainscreen.PlannedVariableParamsSQLLocal;
import com.ejb.actualexpenses.ActualExpensesSQLLocal;
import com.ejb.calculation.AccountsHandlerLocal;
import com.ejb.calculation.ExpensesHandlerLocal;
import com.ejb.common.SQLAbstract;
import com.ejb.calculation.EntityExpense;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

/**
 * EJB ExpensesStructureSQLDelete is used to perform delete operations on 
 * Expenses records in the database.
 */
@Stateless
public class ExpensesStructureSQLDelete extends SQLAbstract
        implements ExpensesStructureSQLDeleteLocal {

    @EJB
    private ExpensesStructureSQLSelectLocal select;

    @EJB
    private ExpensesHandlerLocal eHandler;

    @EJB
    private ExpensesStructureSQLUpdateLocal update;
    
    @EJB
    private PlannedVariableParamsSQLLocal plannedExpensesSQL;
    
    @EJB
    private AccountsHandlerLocal aHandler;
    
    @EJB
    private PlannedAccountsValuesSQLLocal plannedAccountsSQL;
    
    @EJB
    private ActualExpensesSQLLocal actualExpensesSQL;

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeDeleteById(Connection connection, String id) 
            throws GenericDBException, GenericDBOperationException {
        Integer idInt = stringToInt(id);
        if (!inputCheckNullBlank(id) || idInt == null || idInt < 1) {
            throw new GenericDBOperationException("Wrong database Expense ID "
                    + "provided (null or < 1).");
        }

        try (PreparedStatement preparedStatement 
                = createPreparedStatement(connection, 
                        "expensesStructure/delete.byid")) {
            preparedStatement.setInt(1, idInt);
            
            /* If COMPLEX_EXPENSES expense is being removed then setting to 0 
            all LINKED_TO_COMPLEX_ID fields that had that complex expense id.
            Removing the Expense from EntityExpenseList (regardless of type).*/
            EntityExpense expense = select.executeSelectById(connection, idInt);
            if (expense.getType().equals(COMPLEX_EXPENSES_SUPPORTED_TYPE)) {
                ArrayList<EntityExpense> expenseList
                        = select.executeSelectAll(connection);
                for (EntityExpense e : expenseList) {
                    if (e.getLinkedToComplexId() == idInt) {
                        update.clearAssignmentToComplexExpense(connection,
                                e.getName());
                    }
                }
            }
            /* Removing Expense from the EntityExpenseList. */
            eHandler.removeFromEntityExpenseList(expense);
                
            // Removing all Plan for the deleted Expense.
            plannedExpensesSQL.executeDeleteByExpenseId(connection, id);            
            
            // Updating Complex Expese Plan (if the deleted Expense was linked
            // to any Complex Expense).
            HashMap<Integer, HashMap<String, Integer>> allLinks 
                    = select.executeSelectAllLinks(connection);
            Integer linkedComplexIdInt = allLinks
                    .get(idInt).get("LINKED_TO_COMPLEX_ID");        
            if (linkedComplexIdInt != 0) {
                eHandler.prepareEntityExpenseById(connection, "W", 
                        linkedComplexIdInt);
                
                // eHandler operation created Expense object again during
                // calculation (because database record with the expense is not
                // removed yet at the moment.
                // Therefore removing Expense from the EntityExpenseList again.
                eHandler.removeFromEntityExpenseList(expense);                   
                
                plannedExpensesSQL.executeUpdateAll(connection, "W");
                
                aHandler.prepareEntityAccountByExpenseId(connection, "W", 
                        linkedComplexIdInt);
                plannedAccountsSQL.executeUpdateAll(connection, "W");
            } else {
                aHandler.prepareEntityAccountByExpenseId(connection, "W", 
                        idInt);
                plannedAccountsSQL.executeUpdateAll(connection, "W");
            }

            // Changing Expense ID in Actual Expenses database table to zero
            // to indicate that this Expense category was deleted (records of 
            // actual expenses for this category will remain in the database
            // for the analysis purposes).
            actualExpensesSQL.setExpenseToDeleted(connection, id);
            
            preparedStatement.executeUpdate();
            
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
    }
}
