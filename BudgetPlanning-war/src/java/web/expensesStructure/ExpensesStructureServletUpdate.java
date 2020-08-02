
package web.expensesStructure;

import ejb.DBConnection.DBConnectionLocal;
import ejb.MainScreen.PlannedAccountsValuesSQLLocal;
import ejb.MainScreen.PlannedVariableParamsSQLLocal;
import ejb.calculation.AccountsHandlerLocal;
import ejb.calculation.EntityAccount;
import ejb.common.OperationResultLogLocal;
import ejb.calculation.EntityExpense;
import ejb.calculation.ExpensesHandlerLocal;
import ejb.expensesStructure.ExpensesStructureSQLSelectLocal;
import ejb.expensesStructure.ExpensesStructureSQLUpdateLocal;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author SoundlyGifted
 */
@WebServlet(name = "ExpensesStructureServletUpdate", urlPatterns = {"/ExpensesStructureServletUpdate"})
public class ExpensesStructureServletUpdate extends HttpServlet {

    @EJB
    private DBConnectionLocal connector;
    
    @EJB
    private ExpensesStructureSQLUpdateLocal update;
    
    @EJB
    private ExpensesStructureSQLSelectLocal select;
   
    @EJB
    private OperationResultLogLocal log;
    
    @EJB
    private ExpensesHandlerLocal eHandler;
    
    @EJB
    private AccountsHandlerLocal aHandler;
    
    @EJB
    private PlannedVariableParamsSQLLocal plannedParams;
    
    @EJB
    private PlannedAccountsValuesSQLLocal plannedAccountsValues;    

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String currentDateTime = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]").format(Calendar.getInstance().getTime());

        HttpSession session = request.getSession();
        Connection DBConnection = connector.connection(session, "expensesStructureDBConnection");

        /* EntityExpense Selected by User for the Update operation.*/
        EntityExpense expenseSelected = (EntityExpense) session.getAttribute("ExpensesStructure_ExpenseSelected");
        Integer expenseSelectedId = null;
        String currentName = null;
        String currentStockPcs = null;
        
        if (expenseSelected != null) {
            expenseSelectedId = expenseSelected.getId();
            currentStockPcs = String.valueOf(expenseSelected.getCurrentStockPcs());
            /* Setting Selected EntityExpense fields as reqeust attributes 
            for passing to the jsp-page. */
            currentName = expenseSelected.getName();
            request.setAttribute("currentName", currentName);
            selectedExpenseToRequestAttributes(DBConnection, request, expenseSelected);
        }     
        
        /* Processing Refresh the page user command. */
        if (request.getParameter("refresh") != null) {
            if (expenseSelectedId != null) {
                expenseSelected = select.executeSelectById(DBConnection, expenseSelectedId);
                session.setAttribute("ExpensesStructure_ExpenseSelected", expenseSelected);
                request.setAttribute("currentName", expenseSelected.getName());
            }
            log.add(session, currentDateTime + " Awaiting for user command...");
            request.getRequestDispatcher("ExpensesStructurePageUpdate.jsp").forward(request, response);
        }

        /* Clearing System message log. */
        if (request.getParameter("clearLog") != null) {
            if (expenseSelectedId != null) {
                expenseSelected = select.executeSelectById(DBConnection, expenseSelectedId);
                session.setAttribute("ExpensesStructure_ExpenseSelected", expenseSelected);
                request.setAttribute("currentName", expenseSelected.getName());
            }
            log.clear(session);
            log.add(session, "Awaiting for initial user command...");
            request.getRequestDispatcher("ExpensesStructurePageUpdate.jsp").forward(request, response);
        }

        /* Processing of Update user command. */
        if (request.getParameter("executeUpdate") != null) {
            /* Old values of the Expense (before change) */
            String linkedComplExpId = null;
            String accountId = null;
            Integer accountIdInt = null;
            String price = null;
            String safetyStockPcs = null;
            String orderQtyPcs = null;
            if (expenseSelected != null) {
                linkedComplExpId = Integer
                        .toString(expenseSelected.getLinkedToComplexId());
                accountIdInt = expenseSelected.getAccountId();
                accountId = Integer.toString(accountIdInt);
                price = Double.toString(expenseSelected.getPrice());
                safetyStockPcs = Double
                        .toString(expenseSelected.getSafetyStockPcs());
                orderQtyPcs = Double
                        .toString(expenseSelected.getOrderQtyPcs());
            }

            /* Getting values for update existing records in the system. */
            String updateNewName = request.getParameter("updateNewName");
            String updateAccountId = request.getParameter("accountIDSelected");
            if (updateAccountId == null || updateAccountId.trim().isEmpty()) {
                updateAccountId = (String) request.getAttribute("currentAccountId");
            }
            String updateLinkedComplExpId = request.getParameter("complexExpenseIDSelected");
            if (updateLinkedComplExpId == null || updateLinkedComplExpId.trim().isEmpty()) {
                updateLinkedComplExpId = (String) request.getAttribute("currentComplexExpenseId");
            }
            
            String updatePrice = request.getParameter("updatePrice");
            String updateSafetyStockPcs = request.getParameter("updateSafetyStockPcs");
            String updateOrderQtyPcs = request.getParameter("updateOrderQtyPcs");
            
            boolean updated = update.execute(DBConnection, currentName, updateNewName,
                    updateAccountId, updateLinkedComplExpId, updatePrice, 
                    currentStockPcs, updateSafetyStockPcs, updateOrderQtyPcs);
            
            if (updated) {
                expenseSelected = select.executeSelectById(DBConnection, expenseSelectedId);
                session.setAttribute("ExpensesStructure_ExpenseSelected", expenseSelected);
                request.setAttribute("currentName", expenseSelected.getName());
                selectedExpenseToRequestAttributes(DBConnection, request, expenseSelected);

                /* Updated values. */
                updateLinkedComplExpId = String
                        .valueOf(expenseSelected.getLinkedToComplexId());
                updateAccountId = String
                        .valueOf(expenseSelected.getAccountId());
                updatePrice = String.valueOf(expenseSelected.getPrice());
                updateSafetyStockPcs = String.valueOf(expenseSelected
                        .getSafetyStockPcs());
                updateOrderQtyPcs = String.valueOf(expenseSelected
                        .getOrderQtyPcs());
                
                // Recalculating and recording Expense and/or Account Plan
                // in case if any influencing parameter changed.
                if (linkedComplExpId != null && accountId != null 
                        && price != null && safetyStockPcs != null 
                        && orderQtyPcs != null) {
                    boolean someParamAndAccountUpdated = false;
                    boolean someParamNotAccountUpdated = false;
                    boolean onlyAccountUpdated = false;
                    if ((!linkedComplExpId.equals(updateLinkedComplExpId) 
                            || !price.equals(updatePrice) 
                            || !safetyStockPcs.equals(updateSafetyStockPcs) 
                            || !orderQtyPcs.equals(updateOrderQtyPcs)) 
                            && !accountId.equals(updateAccountId)) {
                        someParamAndAccountUpdated = true;
                    } else if (!linkedComplExpId.equals(updateLinkedComplExpId) 
                            || !price.equals(updatePrice) 
                            || !safetyStockPcs.equals(updateSafetyStockPcs) 
                            || !orderQtyPcs.equals(updateOrderQtyPcs)) {
                        someParamNotAccountUpdated = true;
                    } else if (!accountId.equals(updateAccountId)) {
                        onlyAccountUpdated = true;
                    }
                    if (someParamAndAccountUpdated) {
                        // Calculating Expense. 
                        eHandler.prepareEntityExpenseById(DBConnection, "W", 
                                        expenseSelectedId);
                        // Updating Expenses Plan.
                        plannedParams.executeUpdateAll(DBConnection, "W");
                        // Calculating newly assigned Account.
                        aHandler.prepareEntityAccountByExpenseId(DBConnection, 
                                        "W", expenseSelectedId);
                        // Calculating previously assigned Account as well.
                        aHandler.prepareEntityAccountById(DBConnection, 
                                "W", accountIdInt);
                        // Updating Accounts Plan.
                        plannedAccountsValues
                                .executeUpdateAll(DBConnection, "W");
                    } else if (someParamNotAccountUpdated) {
                        // Calculating Expense. 
                        eHandler.prepareEntityExpenseById(DBConnection, "W", 
                                        expenseSelectedId);
                        // Updating Expenses Plan.
                        plannedParams.executeUpdateAll(DBConnection, "W");
                        // Calculating Account.
                        aHandler.prepareEntityAccountByExpenseId(DBConnection, 
                                        "W", expenseSelectedId);
                        // Updating Accounts Plan.
                        plannedAccountsValues
                                .executeUpdateAll(DBConnection, "W");                     
                    } else if (onlyAccountUpdated) {
                        // Updating newly assigned Account.
                        aHandler.prepareEntityAccountByExpenseId(DBConnection, 
                                        "W", expenseSelectedId);
                        // Updating previously assigned Account as well.
                        aHandler.prepareEntityAccountById(DBConnection, 
                                "W", accountIdInt);
                        // Updating Accounts Plan.
                        plannedAccountsValues
                                .executeUpdateAll(DBConnection, "W");                     
                    }                    
                }
                log.add(session, currentDateTime + " [Update Expense command entered] : Expense attributes updated");
            } else {
                log.add(session, currentDateTime + " [Update Expense command entered] : Command declined");
            }
            request.getRequestDispatcher("ExpensesStructurePageUpdate.jsp").forward(request, response);
        }

        /* Processing Return to Expenses Structure page user command. */
        if (request.getParameter("return") != null) {
            session.removeAttribute("ExpensesStructure_ExpenseSelected");
            session.removeAttribute("ExpensesStructure_ExpenseSelectedType");
            request.getRequestDispatcher("ExpensesStructurePage.jsp").forward(request, response);
        }
    }

    private void selectedExpenseToRequestAttributes(Connection connection, HttpServletRequest request, EntityExpense expenseSelected) {
        int currentAccountId = expenseSelected.getAccountId();
        String currentAccount = expenseSelected.getAccountLinked();
        int linkedToComplexId = expenseSelected.getLinkedToComplexId();
        double currentPrice = expenseSelected.getPrice();
        double currentSafetyStockPcs = expenseSelected.getSafetyStockPcs();
        double currentOrderQtyPcs = expenseSelected.getOrderQtyPcs();   

        request.setAttribute("currentAccountId", Integer.toString(currentAccountId));
        request.setAttribute("currentAccount", currentAccount);
        request.setAttribute("currentComplexExpenseId", Integer.toString(linkedToComplexId));
        if (linkedToComplexId == 0) {
            request.setAttribute("currentLinkedToComplExpName", "NOT SET");
        } else {
            request.setAttribute("currentLinkedToComplExpName", 
                select.executeSelectById(connection, linkedToComplexId).getName());
        }
        request.setAttribute("currentPrice", Double.toString(currentPrice));
        request.setAttribute("currentSafetyStockPcs", Double.toString(currentSafetyStockPcs));
        request.setAttribute("currentOrderQtyPcs", Double.toString(currentOrderQtyPcs));    
    }
    
    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
