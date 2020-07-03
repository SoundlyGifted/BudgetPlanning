
package web.expensesStructure;

import ejb.DBConnection.DBConnectionLocal;
import ejb.accountsStructure.AccountsStructureSQLLocal;
import ejb.common.OperationResultLogLocal;
import ejb.entity.EntityAccount;
import ejb.entity.EntityExpense;
import ejb.expensesStructure.ExpensesStructureHandlerLocal;
import ejb.expensesStructure.ExpensesStructureSQLInsertLocal;
import java.io.IOException;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ejb.expensesStructure.ExpensesStructureSQLDeleteLocal;
import ejb.expensesStructure.ExpensesStructureSQLSelectLocal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.http.HttpSession;

/**
 *
 * @author SoundlyGifted
 */
@WebServlet(name = "ExpensesStructureServlet", urlPatterns = {"/ExpensesStructureServlet"})
public class ExpensesStructureServlet extends HttpServlet {
    
    @EJB
    private DBConnectionLocal connector;
    
    @EJB
    private ExpensesStructureHandlerLocal handler;
    
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        String currentDateTime = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]").format(Calendar.getInstance().getTime());
        
        HttpSession session = request.getSession();
        Connection DBConnection = connector.connection(session, "expensesStructureDBConnection");
        
        /* Refreshing the page. */
        if (request.getParameter("refresh") != null) {
            log.add(session, currentDateTime + " Awaiting for user command...");       
            request.getRequestDispatcher("ExpensesStructurePage.jsp").forward(request, response);
        }
        
        /* Clearing System message log. */
        if (request.getParameter("clearLog") != null) {
            log.clear(session);
            log.add(session, "Awaiting for initial user command..."); 
            request.getRequestDispatcher("ExpensesStructurePage.jsp").forward(request, response);
        }

        /* Handling dropdown "NAME" expense category list for Update operation. */
        if (request.getParameter("selectForUpdate") != null) {
            /* Getting ID of the selected expense from the form. */
            String updateExpenseUserSelected = request.getParameter("updateExpenseUserSelected");
            if (updateExpenseUserSelected != null && !updateExpenseUserSelected.trim().isEmpty()) {
                   
                /* EntityExpense Selected by User for the Update operation.*/
                EntityExpense expenseSelected = select.executeSelectById(DBConnection, Integer.parseInt(updateExpenseUserSelected));

                /* Setting selected expense as an Attribute to pass to another Servlet. */
                session.setAttribute("ExpensesStructure_ExpenseSelected", expenseSelected);
                session.setAttribute("ExpensesStructure_ExpenseSelectedType", expenseSelected.getType());

                String currentName = expenseSelected.getName();
                int currentAccountId = expenseSelected.getAccountId();
                String currentAccount = expenseSelected.getAccountLinked();
                int linkedToComplexId = expenseSelected.getLinkedToComplexId();
                double currentPrice = expenseSelected.getPrice();
                double currentSafetyStockPcs = expenseSelected.getSafetyStockPcs();
                double currentOrderQtyPcs = expenseSelected.getOrderQtyPcs();

                /* Setting Selected EntityExpense fields as reqeust attributes for passing to the jsp-page. */
                request.setAttribute("currentName", currentName);
                request.setAttribute("currentAccountId", Integer.toString(currentAccountId));
                request.setAttribute("currentAccount", currentAccount);
                if (linkedToComplexId == 0) {
                    request.setAttribute("currentLinkedToComplExpName", "");
                } else {
                String currentLinkedToComplExpName
                        = select.executeSelectById(DBConnection, linkedToComplexId).getName();
                    request.setAttribute("currentLinkedToComplExpName",
                            currentLinkedToComplExpName);
                }
                request.setAttribute("currentPrice", Double.toString(currentPrice));
                request.setAttribute("currentSafetyStockPcs", Double.toString(currentSafetyStockPcs));
                request.setAttribute("currentOrderQtyPcs", Double.toString(currentOrderQtyPcs));
                request.getRequestDispatcher("ExpensesStructurePageUpdate.jsp").forward(request, response);
            } else {
                log.add(session, currentDateTime + " [Select Expense command entered] : Expense select error");
                request.getRequestDispatcher("ExpensesStructurePage.jsp").forward(request, response);
            }
        }
        
        /* Processing Delete user command. */
        if (request.getParameter("delete") != null) {
            /* Getting ID of the selected expense from the form. */
            String updateExpenseUserSelected = request.getParameter("updateExpenseUserSelected");
            boolean deleted = false;
            if (updateExpenseUserSelected != null && !updateExpenseUserSelected.trim().isEmpty()) {
                deleted = delete.executeDeleteById(DBConnection, updateExpenseUserSelected);                
            }
            if (deleted) {
                log.add(session, currentDateTime + " [Delete Expense command entered] : Expense deleted");                 
                request.getRequestDispatcher("ExpensesStructurePage.jsp").forward(request, response);                  
            } else {
                log.add(session, currentDateTime + " [Delete Expense command entered] : Command declined");                     
                request.getRequestDispatcher("ExpensesStructurePage.jsp").forward(request, response);                 
            }
        }

        /* Processing Insert user command. */
        if (request.getParameter("executeInsert") != null) {

            /* Getting values for input to the system. */
            String inputType = request.getParameter("inputType");
            String inputName = request.getParameter("inputName");
            String inputAccountId = request.getParameter("accountIDSelected");
            String inputPrice = request.getParameter("inputPrice");
            String inputSafetyStockPcs = request.getParameter("inputSafetyStockPcs");
            String inputOrderQtyPcs = request.getParameter("inputOrderQtyPcs");

            boolean inserted = insert.execute(DBConnection, inputType, inputName,
                    inputAccountId, inputPrice, inputSafetyStockPcs,
                    inputOrderQtyPcs);
            if (inserted) {
                log.add(session, currentDateTime + " [Add Expense command entered] : Expense added");
            } else {
                log.add(session, currentDateTime + " [Add Expense command entered] : Command declined");
            }
            request.getRequestDispatcher("ExpensesStructurePage.jsp").forward(request, response);
        }
    }

    private String EntityExpenseListString() {
        ArrayList<EntityExpense> list = handler.getEntityExpenseList();
        StringBuilder sb = new StringBuilder();
        for (EntityExpense e : list) {
            sb.append("<li>").append(e.toString()).append("</li>");
        }
        return sb.toString();
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
