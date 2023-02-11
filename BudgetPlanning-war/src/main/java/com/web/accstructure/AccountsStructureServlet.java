
package com.web.accstructure;

import com.ejb.database.DBConnectionLocal;
import com.ejb.mainscreen.PlannedAccountsValuesSQLLocal;
import com.ejb.accstructure.AccountsStructureSQLLocal;
import com.ejb.calculation.AccountsHandlerLocal;
import com.ejb.common.OperationResultLogLocal;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.web.common.WebServletCommonMethods;

/**
 * AccountsStructureServlet Servlet processes commands that come from user form 
 * on the Accounts Structure page. 
 */
@WebServlet(name = "AccountsStructureServlet", 
        urlPatterns = {"/AccountsStructureServlet"})
public class AccountsStructureServlet extends HttpServlet {

    @EJB
    private DBConnectionLocal connector;
    
    @EJB
    private OperationResultLogLocal log;
    
    @EJB
    private AccountsStructureSQLLocal sql;    
    
    @EJB
    private AccountsHandlerLocal aHandler;
    
    @EJB
    private PlannedAccountsValuesSQLLocal plannedAccountsValues;
    
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
        
        HttpSession session = request.getSession();
        
        Connection DBConnection = null;
        try {
            DBConnection = connector.connection(session,
                    "accountsStructureDBConnection");      
        } catch (GenericDBException ex) {
            log.add(session, ex.getMessage());
        }
        
        ArrayList<Integer> accountsIdList = commonMethods
                .getIdList(DBConnection, "ACCOUNTS_STRUCTURE");
                     
         // Processing Add operation.
        if (request.getParameter("addAccount") != null) {
            String inputName = request.getParameter("inputName");
            String inputCurrentRemainder = request
                    .getParameter("inputCurrentRemainder");
            try {
                sql.executeInsert(DBConnection, inputName, inputCurrentRemainder);
                log.add(session, "[Add Account command entered] : Account added");                
            } catch (GenericDBException | GenericDBOperationException ex) {
                log.add(session, "[Add Account command entered] " 
                        + ex.getMessage());
            }
            request.getRequestDispatcher("AccountsStructurePage.jsp")
                    .forward(request, response);
        }
        
        // Processing Update operation.
        /* Defining ID of row which was selected for update and passing it as 
         * request attribute.
         */
        for (Integer id : accountsIdList) {
            if (request.getParameter("update_" + String.valueOf(id)) != null) {
                request.setAttribute("rowSelectedForUpdate", id);
                request.getRequestDispatcher("AccountsStructurePage.jsp")
                        .forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
         * to EJB for update operation.
         */
        for (Integer id : accountsIdList) {
            if (request.getParameter("submitUpdate_"
                    + String.valueOf(id)) != null) {
                try {
                    String idToUpdate = String.valueOf(id);

                    // Current value of currentRemainderCur. 
                    HashMap<Integer, HashMap<String, Double>> values
                            = sql.executeSelectAllValues(DBConnection);
                    String currentRemainderCur = String.valueOf(values.get(id)
                            .get("CURRENT_REMAINDER_CUR"));

                    // Updated values. 
                    String updateName = request.getParameter("updateName");
                    String updateCurrentRemainder = request
                            .getParameter("updateCurrentRemainder");

                    sql.executeUpdate(DBConnection, idToUpdate, updateName,
                            updateCurrentRemainder);

                    if (!currentRemainderCur.equals(updateCurrentRemainder)) {
                        // Calculating Account.
                        aHandler.prepareEntityAccountById(DBConnection, "W", id);
                        // Updating Accounts Plan.
                        plannedAccountsValues.executeUpdateAll(DBConnection, "W");
                    }
                    log.add(session, "[Update Account command entered] : "
                            + "Account updated");                   
                } catch (GenericDBException | GenericDBOperationException ex) {
                    log.add(session, "[Update Account command entered] : "
                            + ex.getMessage());
                }
                request.getRequestDispatcher("AccountsStructurePage.jsp")
                        .forward(request, response);
            }
        }
        /* Defining ID of row which was cancelled for update and passing it 
         * as request attribute.
         */
        for (Integer id : accountsIdList) {
            if (request.getParameter("cancelUpdate_" 
                    + String.valueOf(id)) != null) {
                request.getRequestDispatcher("AccountsStructurePage.jsp")
                        .forward(request, response);
            }
        }
        
        // Processing Delete operation.
        /* Defining ID of row which was selected for delete and passing it 
         * to EJB for delete operation.
         */
        for (Integer id : accountsIdList) {
            if (request.getParameter("delete_" + String.valueOf(id)) != null) {
                try {
                    sql.executeDelete(DBConnection, String.valueOf(id));
                    log.add(session, "[Delete Account command entered] : "
                            + "Account deleted");                  
                } catch (GenericDBException | GenericDBOperationException ex) {
                    log.add(session, "[Delete Account command entered] : "
                            + ex.getMessage());
                }
                request.getRequestDispatcher("AccountsStructurePage.jsp")
                        .forward(request, response);
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
