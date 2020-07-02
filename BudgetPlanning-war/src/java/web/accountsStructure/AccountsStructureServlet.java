
package web.accountsStructure;

import ejb.DBConnection.DBConnectionLocal;
import ejb.accountsStructure.AccountsStructureSQLLocal;
import ejb.common.OperationResultLogLocal;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
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
@WebServlet(name = "AccountsStructureServlet", urlPatterns = {"/AccountsStructureServlet"})
public class AccountsStructureServlet extends HttpServlet {

    @EJB
    private DBConnectionLocal connector;
    
    @EJB
    private OperationResultLogLocal log;
    
    @EJB
    private AccountsStructureSQLLocal sql;    
    
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
        
        ArrayList<Integer> AccountIdList = getIdList(DBConnection);
        
        /* Refreshing the page. */
        if (request.getParameter("refresh") != null) {
            log.add(session, currentDateTime + " Awaiting for user command...");       
            request.getRequestDispatcher("AccountsStructurePage.jsp").forward(request, response);
        }
        
        /* Clearing System message log. */
        if (request.getParameter("clearLog") != null) {
            log.clear(session);
            log.add(session, "Awaiting for initial user command..."); 
            request.getRequestDispatcher("AccountsStructurePage.jsp").forward(request, response);
        }        
        
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
        for (Integer id : AccountIdList) {
            if (request.getParameter("update_" + String.valueOf(id)) != null) {
                request.setAttribute("rowSelectedForUpdate", id);
                request.getRequestDispatcher("AccountsStructurePage.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
        to Bean for update operation. */
        for (Integer id : AccountIdList) {
            if (request.getParameter("submitUpdate_" + String.valueOf(id)) != null) {
                String idToUpdate = String.valueOf(id);
                String updateName = request.getParameter("updateName");
                String updateCurrentRemainder = request.getParameter("updateCurrentRemainder");
                boolean updated = sql.executeUpdate(DBConnection, idToUpdate, 
                        updateName, updateCurrentRemainder);
                if (updated) {
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
        for (Integer id : AccountIdList) {
            if (request.getParameter("cancelUpdate_" + String.valueOf(id)) != null) {
                request.getRequestDispatcher("AccountsStructurePage.jsp").forward(request, response);
            }
        }        
        
        /* Processing Delete operation. */
        /* Defining ID of row which was selected for delete and passing it 
        to Bean for delete operation. */
        for (Integer id : AccountIdList) {
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

    /* returns Collection of IDs from ACCOUNTS_STRUCTURE database table. */
    private ArrayList<Integer> getIdList(Connection connection) {

        Statement statement = null;
        String query = "select ID from ACCOUNTS_STRUCTURE";

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            getIdListErrorMsg(ex);
            return null;
        }

        try (ResultSet resultSet = statement.executeQuery(query)) {
            Collection<Integer> IdList = new LinkedList<>();
            while (resultSet.next()) {
                IdList.add(resultSet.getInt("ID"));
            }
            return new ArrayList<>(IdList);
        } catch (SQLException ex) {
            getIdListErrorMsg(ex);
            return null;
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                getIdListErrorMsg(ex);
            }
        }
    }

    private void getIdListErrorMsg (SQLException ex) {
        System.out.println("*** AccountsStructureServlet : error "
                        + "while getting Accounts Structure ID list: "
                        + ex.getMessage());
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
