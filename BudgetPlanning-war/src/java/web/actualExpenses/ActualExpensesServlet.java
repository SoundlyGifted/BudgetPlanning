
package web.actualExpenses;

import ejb.DBConnection.DBConnectionLocal;
import ejb.MainScreen.PlannedVariableParamsSQLLocal;
import ejb.actualExpenses.ActualExpensesSQLLocal;
import ejb.calculation.ExpensesHandlerLocal;
import ejb.common.OperationResultLogLocal;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import web.common.WebServletCommonMethods;
import org.json.*;

/**
 * ActualExpensesServlet Servlet processes commands that come from user form 
 * on the Actual Expenses page.
 */
@WebServlet(name = "ActualExpensesServlet", 
        urlPatterns = {"/ActualExpensesServlet"})
public class ActualExpensesServlet extends HttpServlet {

    @EJB
    private DBConnectionLocal connector;
    
    @EJB
    private OperationResultLogLocal log;
    
    @EJB
    private ActualExpensesSQLLocal sql;
    
    @EJB
    private ExpensesHandlerLocal eHandler;
    
    @EJB
    private PlannedVariableParamsSQLLocal plannedParams;
    
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
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String currentDateTime = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]")
                .format(Calendar.getInstance().getTime());
        
        HttpSession session = request.getSession();
        Connection DBConnection = connector
                .connection(session, "actualExpensesDBConnection");
        
        ArrayList<Integer> actualExpensesIdList = commonMethods
                .getIdList(DBConnection, "ACTUAL_EXPENSES");        
        ArrayList<Integer> expensesIdList = commonMethods
                .getIdList(DBConnection, "EXPENSES_STRUCTURE");   
        
        /* Processing Add operation. */
        if (request.getParameter("addActualExpense") != null) {
            String inputDate = request.getParameter("inputDate");

            String inputIdAndName = request.getParameter("inputIdAndName");
            JSONObject obj = new JSONObject(inputIdAndName);
            String inputId = obj.getString("id");
            String inputName = obj.getString("name");

            String inputTitle = request.getParameter("inputTitle");
            String inputShop = request.getParameter("inputShop");
            String inputPrice = request.getParameter("inputPrice");
            String inputQty = request.getParameter("inputQty");
            String inputComment = request.getParameter("inputComment");
            boolean added = sql.executeInsert(DBConnection, inputDate,
                    inputName, inputTitle, inputShop, inputPrice, inputQty,
                    inputComment);
            if (added) {
                int inputIdint = Integer.parseInt(inputId);
                // Calculating Expense. 
                eHandler.prepareEntityExpenseById(DBConnection, "W", 
                        inputIdint);
                // Updating Expenses Plan.
                plannedParams.executeUpdateAll(DBConnection, "W");

                log.add(session, currentDateTime + " [Add Actual Expense "
                        + "command entered] : Actual Expense added");
            } else {
                log.add(session, currentDateTime + " [Add Actual Expense "
                        + "command entered] : Command declined");
            }
            request.getRequestDispatcher("ActualExpensesPage.jsp")
                    .forward(request, response);
        }

        /* Processing Update operation. */
        /* Defining ID of row which was selected for update and passing it 
        as request attribute. */
        for (Integer id : actualExpensesIdList) {
            if (request.getParameter("update_" + String.valueOf(id)) != null) {
                request.setAttribute("rowSelectedForUpdate", id);
                request.getRequestDispatcher("ActualExpensesPage.jsp")
                        .forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
        to Bean for update operation. */
        for (Integer id : actualExpensesIdList) {
            for (Integer expenseId : expensesIdList) {
                if (request.getParameter("submitUpdate_" + String.valueOf(id) 
                        + "_" + String.valueOf(expenseId)) != null) {
                    String idToUpdate = String.valueOf(id);
                    String updateDate = request.getParameter("updateDate");

                    String updateName = request.getParameter("updateName");
                    String updateId = String.valueOf(expenseId);                

                    String updateTitle = request.getParameter("updateTitle");
                    String updateShop = request.getParameter("updateShop");
                    String updatePrice = request.getParameter("updatePrice");
                    String updateQty = request.getParameter("updateQty");
                    String updateComment = request
                            .getParameter("updateComment");
                    boolean updated = sql.executeUpdate(DBConnection, 
                            idToUpdate, updateDate, updateName, updateTitle, 
                            updateShop, updatePrice, updateQty, updateComment);
                    if (updated) {
                        int updateIdInt = Integer.parseInt(updateId);
                        // Calculating Expense. 
                        eHandler.prepareEntityExpenseById(DBConnection, "W", 
                                updateIdInt);
                        // Updating Expenses Plan.
                        plannedParams.executeUpdateAll(DBConnection, "W");                    

                        log.add(session, currentDateTime 
                                + " [Update Actual Expense command entered] : "
                                        + "Actual Expense updated");
                    } else {
                        log.add(session, currentDateTime 
                                + " [Update Actual Expense command entered] : "
                                        + "Command declined");
                    }
                    request.getRequestDispatcher("ActualExpensesPage.jsp")
                            .forward(request, response);
                }                
            }
        }
        /* Defining ID of row which was cancelled for update and passing it 
        as request attribute. */
        for (Integer id : actualExpensesIdList) {
            if (request.getParameter("cancelUpdate_" 
                    + String.valueOf(id)) != null) {
                request.getRequestDispatcher("ActualExpensesPage.jsp")
                        .forward(request, response);
            }
        }
        
        /* Processing Delete operation. */
        /* Defining ID of row which was selected for delete and passing it 
        to Bean for delete operation. */
        for (Integer id : actualExpensesIdList) {
            for (Integer expenseId : expensesIdList) {
                if (request.getParameter("delete_" + String.valueOf(id) 
                        + "_" + String.valueOf(expenseId)) != null) {
                    boolean deleted = sql.executeDelete(DBConnection,
                            String.valueOf(id));
                    if (deleted) {
                        // Calculating Expense. 
                        eHandler.prepareEntityExpenseById(DBConnection, "W", 
                                expenseId);
                        // Updating Expenses Plan.
                        plannedParams.executeUpdateAll(DBConnection, "W");    

                        log.add(session, currentDateTime 
                                + " [Delete Actual Expense command entered] : "
                                        + "Actual Expense deleted");
                    } else {
                        log.add(session, currentDateTime 
                                + " [Delete Actual Expense command entered] : "
                                        + "Command declined");
                    }
                    request.getRequestDispatcher("ActualExpensesPage.jsp")
                            .forward(request, response);
                }
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
