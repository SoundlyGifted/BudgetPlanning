
package web.actualExpenses;

import ejb.DBConnection.DBConnectionLocal;
import ejb.actualExpenses.ActualExpensesSQLLocal;
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
@WebServlet(name = "ActualExpensesServlet", urlPatterns = {"/ActualExpensesServlet"})
public class ActualExpensesServlet extends HttpServlet {

    @EJB
    private DBConnectionLocal connector;
    
    @EJB
    private OperationResultLogLocal log;
    
    @EJB
    private ActualExpensesSQLLocal sql;
    
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
        
        ArrayList<Integer> ActualExpensesIdList = getActualExpensesIdList(DBConnection);
                
        /* Processing Add operation. */
        if (request.getParameter("addActualExpense") != null) {
            String inputDate = request.getParameter("inputDate");
            String inputName = request.getParameter("inputName");
            String inputTitle = request.getParameter("inputTitle");
            String inputShop = request.getParameter("inputShop");
            String inputPrice = request.getParameter("inputPrice");
            String inputQty = request.getParameter("inputQty");
            String inputComment = request.getParameter("inputComment");
            boolean added = sql.executeInsert(DBConnection, inputDate, 
                    inputName, inputTitle, inputShop, inputPrice, inputQty, 
                    inputComment);
            if (added) {
                log.add(session, currentDateTime + " [Add Actual Expense "
                        + "command entered] : Actual Expense added");
            } else {
                log.add(session, currentDateTime + " [Add Actual Expense "
                        + "command entered] : Command declined");
            }
            request.getRequestDispatcher("ActualExpensesPage.jsp").forward(request, response);
        }

        /* Processing Update operation. */
        /* Defining ID of row which was selected for update and passing it 
        as request attribute. */
        for (Integer id : ActualExpensesIdList) {
            if (request.getParameter("update_" + String.valueOf(id)) != null) {
                request.setAttribute("rowSelectedForUpdate", id);
                request.getRequestDispatcher("ActualExpensesPage.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
        to Bean for update operation. */
        for (Integer id : ActualExpensesIdList) {
            if (request.getParameter("submitUpdate_" + String.valueOf(id)) != null) {
                String idToUpdate = String.valueOf(id);
                String updateDate = request.getParameter("updateDate");
                String updateName = request.getParameter("updateName");
                String updateTitle = request.getParameter("updateTitle");
                String updateShop = request.getParameter("updateShop");
                String updatePrice = request.getParameter("updatePrice");
                String updateQty = request.getParameter("updateQty");
                String updateComment = request.getParameter("updateComment");
                boolean updated = sql.executeUpdate(DBConnection, idToUpdate,
                        updateDate, updateName, updateTitle, updateShop, 
                        updatePrice, updateQty, updateComment);
                if (updated) {
                    log.add(session, currentDateTime + " [Update Actual Expense "
                            + "command entered] : Actual Expense updated");
                } else {
                    log.add(session, currentDateTime + " [Update Actual Expense "
                            + "command entered] : Command declined");
                }
                request.getRequestDispatcher("ActualExpensesPage.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was cancelled for update and passing it 
        as request attribute. */
        for (Integer id : ActualExpensesIdList) {
            if (request.getParameter("cancelUpdate_" + String.valueOf(id)) != null) {
                request.getRequestDispatcher("ActualExpensesPage.jsp").forward(request, response);
            }
        }
        
        /* Processing Delete operation. */
        /* Defining ID of row which was selected for delete and passing it 
        to Bean for delete operation. */
        for (Integer id : ActualExpensesIdList) {
            if (request.getParameter("delete_" + String.valueOf(id)) != null) {
                boolean deleted = sql.executeDelete(DBConnection, 
                        String.valueOf(id));
                if (deleted) {
                    log.add(session, currentDateTime + " [Delete Actual Expense "
                            + "command entered] : Actual Expense deleted");
                } else {
                    log.add(session, currentDateTime + " [Delete Actual Expense "
                            + "command entered] : Command declined");
                }
                request.getRequestDispatcher("ActualExpensesPage.jsp").forward(request, response);
            }
        }
    }

    /* returns Collection of IDs from ACTUAL_EXPENSES database table. */
    private ArrayList<Integer> getActualExpensesIdList(Connection connection) {

        Statement statement = null;
        String query = "select ID from ACTUAL_EXPENSES";

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("*** ActualExpensesServlet : error while "
                    + "getting Actual Expenses ID list: " + ex.getMessage());
            return null;
        }

        try (ResultSet resultSet = statement.executeQuery(query)) {
            Collection<Integer> IdList = new LinkedList<>();
            while (resultSet.next()) {
                IdList.add(resultSet.getInt("ID"));
            }
            return new ArrayList<>(IdList);
        } catch (SQLException ex) {
            System.out.println("*** ActualExpensesServlet : error while "
                    + "getting Actual Expenses ID list: " + ex.getMessage());
            return null;
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                System.out.println("*** ActualExpensesServlet : error "
                        + "while getting Actual Expenses ID list: "
                        + ex.getMessage());
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
