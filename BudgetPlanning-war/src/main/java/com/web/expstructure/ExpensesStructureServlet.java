
package com.web.expstructure;

import com.ejb.database.DBConnectionLocal;
import com.ejb.mainscreen.PlannedVariableParamsSQLLocal;
import com.ejb.common.OperationResultLogLocal;
import com.ejb.calculation.EntityExpense;
import com.ejb.expstructure.ExpensesStructureSQLInsertLocal;
import java.io.IOException;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.ejb.expstructure.ExpensesStructureSQLDeleteLocal;
import com.ejb.expstructure.ExpensesStructureSQLSelectLocal;
import java.sql.Connection;
import jakarta.servlet.http.HttpSession;
import com.ejb.calculation.ExpensesHandlerLocal;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.util.HashMap;

/**
 * ExpensesStructureServlet Servlet processes commands that come from user form 
 * on the Expenses Structure page.
 */
@WebServlet(name = "ExpensesStructureServlet", 
        urlPatterns = {"/ExpensesStructureServlet"})
public class ExpensesStructureServlet extends HttpServlet {
    
    @EJB
    private DBConnectionLocal connector;
    
    @EJB
    private ExpensesHandlerLocal handler;
    
    @EJB
    private PlannedVariableParamsSQLLocal plannedParams;
    
    @EJB
    private ExpensesStructureSQLInsertLocal insert;

    @EJB
    private ExpensesStructureSQLDeleteLocal delete;
    
    @EJB
    private ExpensesStructureSQLSelectLocal select;
 
    @EJB
    private OperationResultLogLocal log;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession();
        
        Connection DBConnection = null;
        try {
            DBConnection = connector.connection(session, 
                    "expensesStructureDBConnection");
        } catch (GenericDBException ex) {
            log.add(session, ex.getMessage());
        }

        // Handling dropdown "NAME" expense category list for Update operation.
        if (request.getParameter("selectForUpdate") != null) {
            // Getting ID of the selected expense from the form.
            String updateExpenseUserSelected = request
                    .getParameter("updateExpenseUserSelected");
            if (updateExpenseUserSelected != null && !updateExpenseUserSelected
                    .trim().isEmpty()) {
                try {
                    // EntityExpense Selected by User for the Update operation.
                    EntityExpense expenseSelected = select
                            .executeSelectById(DBConnection, Integer
                                    .valueOf(updateExpenseUserSelected));

                    /* Setting selected expense as an Attribute to pass to another 
                     * Servlet.
                     */
                    session.setAttribute("ExpensesStructure_ExpenseSelected",
                            expenseSelected);
                    session.setAttribute("ExpensesStructure_ExpenseSelectedType",
                            expenseSelected.getType());

                    String currentName = expenseSelected.getName();
                    int currentAccountId = expenseSelected.getAccountId();
                    String currentAccount = expenseSelected.getAccountLinked();
                    int linkedToComplexId = expenseSelected.getLinkedToComplexId();
                    double currentPrice = expenseSelected.getPrice();
                    double currentSafetyStockPcs = expenseSelected
                            .getSafetyStockPcs();
                    double currentOrderQtyPcs = expenseSelected.getOrderQtyPcs();

                    /* Setting Selected EntityExpense fields as reqeust attributes 
                     * for passing to the jsp-page. 
                     */
                    request.setAttribute("currentName", currentName);
                    request.setAttribute("currentAccountId", Integer
                            .toString(currentAccountId));
                    request.setAttribute("currentAccount", currentAccount);
                    request.setAttribute("currentComplexExpenseId", Integer
                            .toString(linkedToComplexId));
                    if (linkedToComplexId == 0) {
                        request.setAttribute("currentLinkedToComplExpName",
                                "NOT SET");
                    } else {
                        String currentLinkedToComplExpName
                                = select.executeSelectById(DBConnection,
                                        linkedToComplexId).getName();
                        request.setAttribute("currentLinkedToComplExpName",
                                currentLinkedToComplExpName);
                    }
                    request.setAttribute("currentPrice", Double
                            .toString(currentPrice));
                    request.setAttribute("currentSafetyStockPcs", Double
                            .toString(currentSafetyStockPcs));
                    request.setAttribute("currentOrderQtyPcs", Double
                            .toString(currentOrderQtyPcs));
                    request.getRequestDispatcher("ExpensesStructurePageUpdate.jsp")
                            .forward(request, response);
                } catch (GenericDBException | GenericDBOperationException ex) {
                    log.add(session, "[Select Expense command entered] : " 
                            + ex.getMessage());
                }
            } else {
                log.add(session, "[Select Expense command entered] : "
                        + "Expense select error");
                request.getRequestDispatcher("ExpensesStructurePage.jsp")
                        .forward(request, response);
            }
        }
        
        // Processing Delete user command.
        if (request.getParameter("delete") != null) {
            // Getting ID of the selected expense from the form.
            String updateExpenseUserSelected = request
                    .getParameter("updateExpenseUserSelected");
            if (updateExpenseUserSelected != null && 
                    !updateExpenseUserSelected.trim().isEmpty()) {
                try {
                    /* Getting ID of Complex Expense that is the Expense being 
                     * deleted is linked to.
                     */
                    HashMap<Integer, HashMap<String, Integer>> allLinks = select
                            .executeSelectAllLinks(DBConnection);
                    Integer linkedComplexId = allLinks.get(Integer
                            .valueOf(updateExpenseUserSelected))
                            .get("LINKED_TO_COMPLEX_ID");
                    delete.executeDeleteById(DBConnection, 
                            updateExpenseUserSelected);
                    /* If there is any Complex Expense that the Expense being 
                     * deleted is linked to then recalculating its Plan and updating 
                     * database.
                     */
                    if (linkedComplexId != 0) {
                        // Calculating Expense. 
                        handler.prepareEntityExpenseById(DBConnection, "W",
                                linkedComplexId);
                        // Updating Expenses Plan.
                        plannedParams.executeUpdateAll(DBConnection, "W");
                    }
                    log.add(session, "[Delete Expense command entered] : "
                            + "Expense deleted");                 
                } catch (GenericDBException | GenericDBOperationException ex) {
                    log.add(session, "[Delete Expense command entered] : " 
                            + ex.getMessage());
                }
            }
            request.getRequestDispatcher("ExpensesStructurePage.jsp")
                    .forward(request, response);
        }

        // Processing Insert user command.
        if (request.getParameter("executeInsert") != null) {
            // Getting values for input to the system.
            String inputType = request.getParameter("inputType");
            String inputName = request.getParameter("inputName");
            String inputAccountId = request.getParameter("accountIDSelected");
            String inputPrice = request.getParameter("inputPrice");
            String inputSafetyStockPcs = request
                    .getParameter("inputSafetyStockPcs");
            String inputOrderQtyPcs = request.getParameter("inputOrderQtyPcs");
            
            try {
                insert.execute(DBConnection, inputType,
                        inputName, inputAccountId, inputPrice, inputSafetyStockPcs,
                        inputOrderQtyPcs);
                log.add(session, "[Add Expense command entered] : "
                        + "Expense added");             
            } catch (GenericDBException | GenericDBOperationException ex) {
                log.add(session, "[Add Expense command entered] : "
                        + ex.getMessage());
            }
            request.getRequestDispatcher("ExpensesStructurePage.jsp")
                    .forward(request, response);
        }
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
