
package web.expensesStructure;

import ejb.DBConnection.DBConnectionLocal;
import ejb.common.OperationResultLogLocal;
import ejb.entity.EntityExpense;
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
        int expenseSelectedId = expenseSelected.getId();
        
        /* Setting Selected EntityExpense fields as reqeust attributes 
        for passing to the jsp-page. */
        String currentName = expenseSelected.getName();
        request.setAttribute("currentName", currentName);
        selectedExpenseToRequestAttributes(DBConnection, request, expenseSelected);

        /* Processing Refresh the page user command. */
        if (request.getParameter("refresh") != null) {
            expenseSelected = select.executeSelectById(DBConnection, expenseSelectedId);
            session.setAttribute("ExpensesStructure_ExpenseSelected", expenseSelected);
            request.setAttribute("currentName", expenseSelected.getName());
            log.add(session, currentDateTime + " Awaiting for user command...");
            request.getRequestDispatcher("ExpensesStructurePageUpdate.jsp").forward(request, response);
        }
        
        /* Clearing System message log. */
        if (request.getParameter("clearLog") != null) {
            expenseSelected = select.executeSelectById(DBConnection, expenseSelectedId);
            session.setAttribute("ExpensesStructure_ExpenseSelected", expenseSelected);
            request.setAttribute("currentName", expenseSelected.getName());
            log.clear(session);
            log.add(session, "Awaiting for initial user command..."); 
            request.getRequestDispatcher("ExpensesStructurePageUpdate.jsp").forward(request, response);
        }        
        
        /* Processing of Update user command. */
        if (request.getParameter("executeUpdate") != null) {
            /* Getting values for update existing records in the system. */
            String updateNewName = request.getParameter("updateNewName");
            String updateAccountName = request.getParameter("updateAccountName");
            String updateLinkedComplExpName = request.getParameter("updateLinkedComplExpName");
            String updatePrice = request.getParameter("updatePrice");
            String updateSafetyStock = request.getParameter("updateSafetyStock");
            String updateOrderQty = request.getParameter("updateOrderQty");
            
            boolean updated = update.execute(DBConnection, currentName, updateNewName,
                    updateAccountName, updateLinkedComplExpName,
                    updatePrice, updateSafetyStock, updateOrderQty);
            if (updated) {
                expenseSelected = select.executeSelectById(DBConnection, expenseSelectedId);
                session.setAttribute("ExpensesStructure_ExpenseSelected", expenseSelected);
                request.setAttribute("currentName", expenseSelected.getName());
                selectedExpenseToRequestAttributes(DBConnection, request, expenseSelected);
                log.add(session, currentDateTime + " [Update Expense command entered] : Expense attributes updated");
            } else {
                log.add(session, currentDateTime + " [Update Expense command entered] : Command declined");
            }
            request.getRequestDispatcher("ExpensesStructurePageUpdate.jsp").forward(request, response);
        }

        /* Processing Clear Assignment to Complex Expense user command. */
        if (request.getParameter("clearAssignmentToComplExp") != null) {
            boolean cleared
                    = update.clearAssignmentToComplexExpense(DBConnection, currentName);
            if (cleared) {
                expenseSelected = select.executeSelectById(DBConnection, expenseSelectedId);
                session.setAttribute("ExpensesStructure_ExpenseSelected", expenseSelected);
                request.setAttribute("currentName", expenseSelected.getName());
                selectedExpenseToRequestAttributes(DBConnection, request, expenseSelected);
                log.add(session, currentDateTime + " [Update Expense command entered] : Assignment to Complex Expense cleared");
            } else {
                log.add(session, currentDateTime + " [Update Expense command entered] : Command declined");
            }
            request.getRequestDispatcher("ExpensesStructurePageUpdate.jsp").forward(request, response);
        }

        /* Processing Clear Assignment to Account user command.*/
        if (request.getParameter("clearAssignmentToAccount") != null) {
            boolean cleared
                    = update.clearAssignmentToAccount(DBConnection, currentName);
            if (cleared) {
                expenseSelected = select.executeSelectById(DBConnection, expenseSelectedId);
                session.setAttribute("ExpensesStructure_ExpenseSelected", expenseSelected);
                request.setAttribute("currentName", expenseSelected.getName());
                selectedExpenseToRequestAttributes(DBConnection, request, expenseSelected);
                log.add(session, currentDateTime + " [Update Expense command entered] : Assignment to Account cleared");
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
        String currentAccount = expenseSelected.getAccountLinked();
        int linkedToComplexId = expenseSelected.getLinkedToComplexId();
        int currentPrice = expenseSelected.getPrice();
        int currentSafetyStock = expenseSelected.getSafetyStock();
        int currentOrderQty = expenseSelected.getOrderQty();   

        request.setAttribute("currentAccount", currentAccount);
        if (linkedToComplexId == 0) {
            request.setAttribute("currentLinkedToComplExpName", "");
        } else {
            request.setAttribute("currentLinkedToComplExpName", 
                select.executeSelectById(connection, linkedToComplexId).getName());
        }
        request.setAttribute("currentPrice", Integer.toString(currentPrice));
        request.setAttribute("currentSafetyStock", Integer.toString(currentSafetyStock));
        request.setAttribute("currentOrderQty", Integer.toString(currentOrderQty));    
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
