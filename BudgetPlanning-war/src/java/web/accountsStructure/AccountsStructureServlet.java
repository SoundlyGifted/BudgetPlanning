
package web.accountsStructure;

import ejb.DBConnection.DBConnectionLocal;
import ejb.MainScreen.PlannedAccountsValuesSQLLocal;
import ejb.accountsStructure.AccountsStructureSQLLocal;
import ejb.calculation.AccountsHandlerLocal;
import ejb.common.OperationResultLogLocal;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import web.common.WebServletCommonMethods;

/**
 *
 * @author SoundlyGifted
 */
@WebServlet(name = "AccountsStructureServlet", urlPatterns = {"/AccountsStructureServlet"})
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String currentDateTime = new SimpleDateFormat("[dd/MM/yyyy HH:mm:ss]").format(Calendar.getInstance().getTime());
        
        HttpSession session = request.getSession();
        Connection DBConnection = connector.connection(session, "accountsStructureDBConnection");
        
        ArrayList<Integer> accountsIdList = commonMethods.getIdList(DBConnection, "ACCOUNTS_STRUCTURE");
                     
         /* Processing Add operation. */
        if (request.getParameter("addAccount") != null) {
            String inputName = request.getParameter("inputName");
            String inputCurrentRemainder = request.getParameter("inputCurrentRemainder");
            
            boolean added = sql.executeInsert(DBConnection, inputName, 
                    inputCurrentRemainder);
            if (added) {
                log.add(session, currentDateTime + " [Add Account "
                        + "command entered] : Account added");
            } else {
                log.add(session, currentDateTime + " [Add Account "
                        + "command entered] : Command declined");
            }
            request.getRequestDispatcher("AccountsStructurePage.jsp").forward(request, response);
        }       
        
        /* Processing Update operation. */
        /* Defining ID of row which was selected for update and passing it 
        as request attribute. */
        for (Integer id : accountsIdList) {
            if (request.getParameter("update_" + String.valueOf(id)) != null) {
                request.setAttribute("rowSelectedForUpdate", id);
                request.getRequestDispatcher("AccountsStructurePage.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
        to Bean for update operation. */
        for (Integer id : accountsIdList) {
            if (request.getParameter("submitUpdate_" + String.valueOf(id)) != null) {
                String idToUpdate = String.valueOf(id);
                
                // Current value of currentRemainderCur. 
                HashMap<Integer, HashMap<String, Double>> values 
                        = sql.executeSelectAllValues(DBConnection);
                String currentRemainderCur = String.valueOf(values.get(id)
                        .get("CURRENT_REMAINDER_CUR"));

                // Updated values. 
                String updateName = request.getParameter("updateName");
                String updateCurrentRemainder = request.getParameter("updateCurrentRemainder");
                boolean updated = sql.executeUpdate(DBConnection, idToUpdate, 
                        updateName, updateCurrentRemainder);
                if (updated) {
                    if (!currentRemainderCur.equals(updateCurrentRemainder)) {
                        // Calculating Account.
                        aHandler.prepareEntityAccountById(DBConnection, 
                                "W", id);
                        // Updating Accounts Plan.
                        plannedAccountsValues
                                .executeUpdateAll(DBConnection, "W");                         
                    }
                    log.add(session, currentDateTime + " [Update Account "
                            + "command entered] : Account updated");
                } else {
                    log.add(session, currentDateTime + " [Update Account "
                            + "command entered] : Command declined");
                }
                request.getRequestDispatcher("AccountsStructurePage.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was cancelled for update and passing it 
        as request attribute. */
        for (Integer id : accountsIdList) {
            if (request.getParameter("cancelUpdate_" + String.valueOf(id)) != null) {
                request.getRequestDispatcher("AccountsStructurePage.jsp").forward(request, response);
            }
        }        
        
        /* Processing Delete operation. */
        /* Defining ID of row which was selected for delete and passing it 
        to Bean for delete operation. */
        for (Integer id : accountsIdList) {
            if (request.getParameter("delete_" + String.valueOf(id)) != null) {
                boolean deleted = sql.executeDelete(DBConnection, 
                        String.valueOf(id));
                if (deleted) {
                    log.add(session, currentDateTime + " [Delete Account "
                            + "command entered] : Account deleted");
                } else {
                    log.add(session, currentDateTime + " [Delete Account "
                            + "command entered] : Command declined");
                }
                request.getRequestDispatcher("AccountsStructurePage.jsp").forward(request, response);
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
