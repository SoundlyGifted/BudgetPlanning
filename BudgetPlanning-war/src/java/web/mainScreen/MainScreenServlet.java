
package web.mainScreen;

import ejb.DBConnection.DBConnectionLocal;
import ejb.MainScreen.PlannedAccountsValuesSQLLocal;
import ejb.MainScreen.PlannedVariableParamsSQLLocal;
import ejb.accountsStructure.AccountsStructureSQLLocal;
import ejb.calculation.AccountsHandlerLocal;
import ejb.calculation.EntityAccount;
import ejb.common.OperationResultLogLocal;
import web.common.WebServletCommonMethods;
import ejb.calculation.EntityExpense;
import ejb.calculation.ExpensesHandlerLocal;
import ejb.calculation.TimePeriodsHandlerLocal;
import ejb.expensesStructure.ExpensesStructureSQLSelectLocal;
import ejb.expensesStructure.ExpensesStructureSQLUpdateLocal;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
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
    private ExpensesHandlerLocal handler;
    
    @EJB
    private AccountsHandlerLocal aHandler;
    
    @EJB
    private ExpensesStructureSQLSelectLocal select;    

    @EJB
    private ExpensesStructureSQLUpdateLocal update;
    
    @EJB
    private AccountsStructureSQLLocal accountsStructureSQL;
    
    @EJB
    private PlannedVariableParamsSQLLocal plannedParams;
    
    @EJB
    private PlannedAccountsValuesSQLLocal plannedAccountsValues;
       
    @EJB
    private WebServletCommonMethods commonMethods;    
    
    @EJB
    private TimePeriodsHandlerLocal timePeriodsHandler;
   
    
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
        ArrayList<Integer> accountsIdList = commonMethods.getIdList(DBConnection, "ACCOUNTS_STRUCTURE");        
        
        EntityExpense selectedExpense = null;
        
        /* Processing Shift of Planning Period (one Period Forward). */
        if (request.getParameter("shiftPeriodForward") != null) {
            
            String nextPeriodDate = timePeriodsHandler.getNextPeriodDate(DBConnection, "W");
            
            handler.calculateAllCurrentStockPcsForNextPeriod(DBConnection);
            aHandler.calculateAllCurrentRemainderCurForNextPeriod(DBConnection);
            
            plannedParams.setCurrentPeriodDate(DBConnection, nextPeriodDate);
            plannedAccountsValues.setCurrentPeriodDate(DBConnection, nextPeriodDate);
            
            HashMap<Integer, String> allTypes = select.executeSelectAllTypes(DBConnection);
            Integer expenseId;
            String type;
            for (Map.Entry<Integer, String> entry : allTypes.entrySet()) {
                expenseId = entry.getKey();
                type = entry.getValue();
                if (type.equals("SIMPLE_EXPENSES") || type.equals("GOODS")) {
                    handler.prepareEntityExpenseById(DBConnection, "W", expenseId);
                }
            }
            plannedParams.executeUpdateAll(DBConnection, "W");

            HashMap<Integer, HashMap<String, Double>> accountsAllValues = 
                    accountsStructureSQL.executeSelectAllValues(DBConnection);
            Integer accountId;
            for (Map.Entry<Integer, HashMap<String, Double>> entry 
                    : accountsAllValues.entrySet()) {
                accountId = entry.getKey();
                aHandler.prepareEntityAccountById(DBConnection, "W", accountId);                  
            }        
            plannedAccountsValues.executeUpdateAll(DBConnection, "W");

            request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
            request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
            request.getRequestDispatcher("index.jsp").forward(request, response);            
        }         

        /* Processing Shift of Planning Period (one Period Back). */
        if (request.getParameter("shiftPeriodBack") != null) {
                
            String previousPeriodDate = timePeriodsHandler.getPreviousPeriodDate(DBConnection, "W");
            
            plannedParams.setCurrentPeriodDate(DBConnection, previousPeriodDate);
            plannedAccountsValues.setCurrentPeriodDate(DBConnection, previousPeriodDate);
            
            handler.calculateAllCurrentStockPcsForPreviousPeriod(DBConnection);
            aHandler.calculateAllCurrentRemainderCurForPreviousPeriod(DBConnection);
            
            HashMap<Integer, String> allTypes = select.executeSelectAllTypes(DBConnection);
            Integer expenseId;
            String type;
            for (Map.Entry<Integer, String> entry : allTypes.entrySet()) {
                expenseId = entry.getKey();
                type = entry.getValue();
                if (type.equals("SIMPLE_EXPENSES") || type.equals("GOODS")) {
                    handler.prepareEntityExpenseById(DBConnection, "W", expenseId);
                }
            }
            plannedParams.executeUpdateAll(DBConnection, "W");
            
            HashMap<Integer, HashMap<String, Double>> accountsAllValues = 
                    accountsStructureSQL.executeSelectAllValues(DBConnection);
            Integer accountId;
            for (Map.Entry<Integer, HashMap<String, Double>> entry 
                    : accountsAllValues.entrySet()) {
                accountId = entry.getKey();
                aHandler.prepareEntityAccountById(DBConnection, "W", accountId);                  
            }        
            plannedAccountsValues.executeUpdateAll(DBConnection, "W");            

            request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
            request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
            request.getRequestDispatcher("index.jsp").forward(request, response);            
        }
           
        /* Processing current Stock adjust operation. */
        /* Defining ID of row which was selected for update and passing it 
        as request attribute. */
        for (Integer id : expensesIdList) {
            if (request.getParameter("update_CURRENT_STOCK_PCS_" + String.valueOf(id)) != null) {

                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
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
                    handler.prepareEntityExpenseById(DBConnection, "W", id);
                    plannedParams.executeUpdateAll(DBConnection, "W");

                    aHandler.prepareEntityAccountByExpenseId(DBConnection, "W", id);
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");                    
 
                    log.add(session, currentDateTime + " [Adjust Current Stock "
                            + "command entered] : Current Stock adjusted");
                } else {
                    log.add(session, currentDateTime + " [Adjust Current Stock "
                            + "command entered] : Command declined");
                }
                
                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
        
        /* Processing Expenses Plan (PCS) update operation. */
        /* Defining ID of row which was selected for update and passing it 
        as request attribute. */
        for (Integer id : expensesIdList) {
            if (request.getParameter("update_PLANNED_PCS_" + String.valueOf(id)) != null) {
                
                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
                request.setAttribute("rowSelectedForExpensesPlanPcsUpdate", id);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
        to Bean for update operation. */
        for (Integer id : expensesIdList) {
            if (request.getParameter("submitUpdate_PLANNED_PCS_" + String.valueOf(id)) != null) {
                String idToUpdate = String.valueOf(id);               
                
                ArrayList<String> dates = commonMethods.getDatesList(DBConnection);
                Map<String, String> updateExpensesPlanPcsList = new TreeMap<>();
                for (String date : dates) {
                    updateExpensesPlanPcsList.put(date, request.getParameter("updateExpensesPlanPcs_" + date));
                }
                
                boolean updated = plannedParams.executeUpdate(DBConnection, 
                        idToUpdate, "PLANNED_PCS", updateExpensesPlanPcsList);
                if (updated) {
                    handler.prepareEntityExpenseById(DBConnection, "W", id);
                    plannedParams.executeUpdateAll(DBConnection, "W");

                    aHandler.prepareEntityAccountByExpenseId(DBConnection, "W", id);                
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");                    
                    
                    log.add(session, currentDateTime + " [Update Expenses Plan "
                            + "PCS command entered] : Expenses Plan "
                            + "updated");
                } else {
                    log.add(session, currentDateTime + " [Update Expenses Plan "
                            + "PCS command entered] : Command declined");
                }
                
                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }        
        
        /* Processing Consumption (PCS) update operation. */
        /* Defining ID of row which was selected for update and passing it 
        as request attribute. */
        for (Integer id : expensesIdList) {
            if (request.getParameter("update_CONSUMPTION_PCS_" + String.valueOf(id)) != null) {

                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
                request.setAttribute("rowSelectedForConsumptionPcsUpdate", id);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
        to Bean for update operation. */
        for (Integer id : expensesIdList) {
            if (request.getParameter("submitUpdate_CONSUMPTION_PCS_" + String.valueOf(id)) != null) {
                String idToUpdate = String.valueOf(id);               
                
                ArrayList<String> dates = commonMethods.getDatesList(DBConnection);
                Map<String, String> updateConsumptionPcsList = new TreeMap<>();
                for (String date : dates) {
                    updateConsumptionPcsList.put(date, request.getParameter("updateConsumptionPcs_" + date));
                }
                
                boolean updated = plannedParams.executeUpdate(DBConnection, 
                        idToUpdate, "CONSUMPTION_PCS", updateConsumptionPcsList);
                if (updated) {
                    handler.prepareEntityExpenseById(DBConnection, "W", id);
                    plannedParams.executeUpdateAll(DBConnection, "W");

                    aHandler.prepareEntityAccountByExpenseId(DBConnection, "W", id);
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");                     

                    log.add(session, currentDateTime + " [Update Consumption "
                            + "PCS command entered] : Expenses Plan "
                            + "updated");
                } else {
                    log.add(session, currentDateTime + " [Update Consumption "
                            + "PCS command entered] : Command declined");
                }
 
                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }        
        
        /* Processing Expenses Plan (CUR) update operation. */
        /* Defining ID of row which was selected for update and passing it 
        as request attribute. */
        for (Integer id : expensesIdList) {
            if (request.getParameter("update_PLANNED_CUR_" + String.valueOf(id)) != null) {

                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
                request.setAttribute("rowSelectedForExpensesPlanCurUpdate", id);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
        to Bean for update operation. */
        for (Integer id : expensesIdList) {
            if (request.getParameter("submitUpdate_PLANNED_CUR_" + String.valueOf(id)) != null) {
                String idToUpdate = String.valueOf(id);               
                
                ArrayList<String> dates = commonMethods.getDatesList(DBConnection);
                Map<String, String> updateExpensesPlanCurList = new TreeMap<>();
                for (String date : dates) {
                    updateExpensesPlanCurList.put(date, request.getParameter("updateExpensesPlanCur_" + date));
                }
                
                boolean updated = plannedParams.executeUpdate(DBConnection, 
                        idToUpdate, "PLANNED_CUR", updateExpensesPlanCurList);
                if (updated) {
                    handler.prepareEntityExpenseById(DBConnection, "W", id);
                    plannedParams.executeUpdateAll(DBConnection, "W");

                    aHandler.prepareEntityAccountByExpenseId(DBConnection, "W", id);
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");                      

                    log.add(session, currentDateTime + " [Update Expenses Plan "
                            + "CUR command entered] : Expenses Plan "
                            + "updated");
                } else {
                    log.add(session, currentDateTime + " [Update Expenses Plan "
                            + "CUR command entered] : Command declined");
                }
    
                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());                
                
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
        
        /* Processing Income (CUR) update operation. */
        /* Defining ID of row which was selected for update and passing it 
        as request attribute. */
        for (Integer id : accountsIdList) {
            if (request.getParameter("update_PLANNED_INCOME_CUR_" + String.valueOf(id)) != null) {
            
                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());
                
                request.setAttribute("rowSelectedForIncomeCurUpdate", id);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
        to Bean for update operation. */
        for (Integer id : accountsIdList) {
            if (request.getParameter("submitUpdate_PLANNED_INCOME_CUR_" + String.valueOf(id)) != null) {
                String idToUpdate = String.valueOf(id);               
                
                ArrayList<String> dates = commonMethods.getDatesList(DBConnection);
                Map<String, String> updateIncomeCurList = new TreeMap<>();
                for (String date : dates) {
                    updateIncomeCurList.put(date, request.getParameter("updateIncomeCur_" + date));
                }
                
                boolean updated = plannedAccountsValues.executeUpdate(DBConnection, 
                        idToUpdate, "PLANNED_INCOME_CUR", updateIncomeCurList);
                if (updated) {
                    aHandler.prepareEntityAccountById(DBConnection, "W", id);
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");                      

                    log.add(session, currentDateTime + " [Update Income Plan "
                            + "CUR command entered] : Income Plan "
                            + "updated");
                } else {
                    log.add(session, currentDateTime + " [Update Income Plan "
                            + "CUR command entered] : Command declined");
                }

                request.setAttribute("currentEntityExpenseList", EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", EntityAccountListString());
                
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
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

    private String EntityAccountListString() {
        ArrayList<EntityAccount> list = aHandler.getEntityAccountList();
        StringBuilder sb = new StringBuilder();
        for (EntityAccount a : list) {
            sb.append("<li>").append(a.toString()).append("</li>");
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
