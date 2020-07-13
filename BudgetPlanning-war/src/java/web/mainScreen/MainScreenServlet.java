
package web.mainScreen;

import ejb.DBConnection.DBConnectionLocal;
import ejb.common.OperationResultLogLocal;
import web.common.WebServletCommonMethods;
import ejb.entity.EntityExpense;
import ejb.expensesStructure.ExpensesStructureSQLSelectLocal;
import ejb.expensesStructure.ExpensesStructureSQLUpdateLocal;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
@WebServlet(name = "MainScreenServlet", urlPatterns = {"/MainScreenServlet"})
public class MainScreenServlet extends HttpServlet {

    @EJB
    private DBConnectionLocal connector;
    
    @EJB
    private OperationResultLogLocal log;

    @EJB
    private ExpensesStructureSQLSelectLocal select;    

    @EJB
    private ExpensesStructureSQLUpdateLocal update;
    
    @EJB
    private WebServletCommonMethods commonMethods;    
    
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
        Connection DBConnection = connector.connection(session, "mainScreenDBConnection");
        
        ArrayList<Integer> expensesIdList = commonMethods.getIdList(DBConnection, "EXPENSES_STRUCTURE");
        
        EntityExpense selectedExpense = null;
        
        /* Processing current Stock adjust operation. */
        /* Defining ID of row which was selected for update and passing it 
        as request attribute. */
        for (Integer id : expensesIdList) {
            if (request.getParameter("update_CURRENT_STOCK_PCS_" + String.valueOf(id)) != null) {
                request.setAttribute("rowSelectedForCurrentStockUpdate", id);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
        to Bean for update operation. */
        for (Integer id : expensesIdList) {
            if (request.getParameter("submitUpdate_CURRENT_STOCK_PCS_" + String.valueOf(id)) != null) {
                String idToUpdate = String.valueOf(id);
                String updateCurrentStock = request.getParameter("updateCurrentStock");                
                
                selectedExpense = select.executeSelectById(DBConnection, Integer.parseInt(idToUpdate));
                String name = selectedExpense.getName();
                String accountId = String.valueOf(selectedExpense.getAccountId());
                String linkedToComplexId = String.valueOf(selectedExpense.getLinkedToComplexId());
                String price = String.valueOf(selectedExpense.getPrice());

                String safetyStockPcs = String.valueOf(selectedExpense.getSafetyStockPcs());
                String orderQtyPcs = String.valueOf(selectedExpense.getOrderQtyPcs());
                
                boolean updated = update.execute(DBConnection, name, name, 
                        accountId, linkedToComplexId, price, updateCurrentStock, 
                        safetyStockPcs, orderQtyPcs);
                if (updated) {
                    log.add(session, currentDateTime + " [Adjust Current Stock "
                            + "command entered] : Current Stock adjusted");
                } else {
                    log.add(session, currentDateTime + " [Adjust Current Stock "
                            + "command entered] : Command declined");
                }
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
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
