
package com.web.mainScreen;

import com.ejb.database.DBConnectionLocal;
import com.ejb.mainscreen.PlannedAccountsValuesSQLLocal;
import com.ejb.mainscreen.PlannedVariableParamsSQLLocal;
import com.ejb.accstructure.AccountsStructureSQLLocal;
import com.ejb.calculation.AccountsHandlerLocal;
import com.ejb.calculation.EntityAccount;
import com.ejb.common.OperationResultLogLocal;
import com.web.common.WebServletCommonMethods;
import com.ejb.calculation.EntityExpense;
import com.ejb.calculation.ExpensesHandlerLocal;
import com.ejb.calculation.TimePeriodsHandlerLocal;
import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.exceptions.GenericDBException;
import com.ejb.expstructure.ExpensesStructureSQLSelectLocal;
import com.ejb.expstructure.ExpensesStructureSQLUpdateLocal;
import com.ejb.expstructure.ExpensesTypes;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;


/**
 * MainScreenServlet Servlet processes commands that come from user form on the 
 * Main Screen page.
 */
@WebServlet(name = "MainScreenServlet", urlPatterns = {"/MainScreenServlet"})
public class MainScreenServlet extends HttpServlet implements ExpensesTypes {

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
    protected void processRequest(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession();
        
        Connection DBConnection = null;
        
        List<Integer> expensesIdList = new ArrayList<>();
        List<Integer> accountsIdList = new ArrayList<>();
        try {
            DBConnection = connector.connection(session,
                    "mainScreenDBConnection");
            expensesIdList = commonMethods.getIdList(DBConnection,
                    "EXPENSES_STRUCTURE");
            accountsIdList = commonMethods.getIdList(DBConnection,
                    "ACCOUNTS_STRUCTURE");
        } catch (GenericDBException | GenericDBOperationException ex) {
            log.add(session, ex.getMessage());
        }

        EntityExpense selectedExpense;
        
        // Processing Shift of Planning Period (one Period Forward).
        if (request.getParameter("shiftPeriodForward") != null) {
            try {
                String nextPeriodDate = timePeriodsHandler
                        .getNextPeriodDate(DBConnection, "W");

                handler.calculateAllCurrentStockPcsForNextPeriod(DBConnection);
                aHandler.calculateAllCurrentRemainderCurForNextPeriod(DBConnection);

                plannedParams.setCurrentPeriodDate(DBConnection, nextPeriodDate);
                plannedAccountsValues.setCurrentPeriodDate(DBConnection,
                        nextPeriodDate);

                HashMap<Integer, String> allTypes = select
                        .executeSelectAllTypes(DBConnection);
                Integer expenseId;
                String type;
                for (Map.Entry<Integer, String> entry : allTypes.entrySet()) {
                    expenseId = entry.getKey();
                    type = entry.getValue();
                    if (type.equals(SIMPLE_EXPENSES_SUPPORTED_TYPE) 
                            || type.equals(GOODS_SUPPORTED_TYPE)) {
                        handler.prepareEntityExpenseById(DBConnection, "W",
                                expenseId);
                    }
                }
                plannedParams.executeUpdateAll(DBConnection, "W");

                HashMap<Integer, HashMap<String, Double>> accountsAllValues
                        = accountsStructureSQL.executeSelectAllValues(DBConnection);
                Integer accountId;
                for (Map.Entry<Integer, HashMap<String, Double>> entry
                        : accountsAllValues.entrySet()) {
                    accountId = entry.getKey();
                    aHandler.prepareEntityAccountById(DBConnection, "W", accountId);
                }
                plannedAccountsValues.executeUpdateAll(DBConnection, "W");
                
                request.setAttribute("currentEntityExpenseList",
                        EntityExpenseListString());
                request.setAttribute("currentEntityAccountList",
                        EntityAccountListString());
                log.add(session, "[Shift one Period forward command entered] : "
                        + "Shifted one Period forward.");                
            } catch (GenericDBOperationException | GenericDBException ex) {
                request.setAttribute("currentEntityExpenseList",
                        EntityExpenseListString());
                request.setAttribute("currentEntityAccountList",
                        EntityAccountListString());                
                log.add(session, "[Shift one Period forward command entered] : "
                        + ex.getMessage());
            }
            request.getRequestDispatcher("index.jsp")
                    .forward(request, response);            
        }

        // Processing Shift of Planning Period (one Period Backward).
        if (request.getParameter("shiftPeriodBack") != null) {
            try {
                String previousPeriodDate = timePeriodsHandler
                        .getPreviousPeriodDate(DBConnection, "W");

                plannedParams.setCurrentPeriodDate(DBConnection,
                        previousPeriodDate);
                plannedAccountsValues.setCurrentPeriodDate(DBConnection,
                        previousPeriodDate);

                handler.calculateAllCurrentStockPcsForPreviousPeriod(DBConnection);
                aHandler.calculateAllCurrentRemainderCurForPreviousPeriod(DBConnection);

                HashMap<Integer, String> allTypes = select
                        .executeSelectAllTypes(DBConnection);
                Integer expenseId;
                String type;
                for (Map.Entry<Integer, String> entry : allTypes.entrySet()) {
                    expenseId = entry.getKey();
                    type = entry.getValue();
                    if (type.equals(SIMPLE_EXPENSES_SUPPORTED_TYPE) 
                            || type.equals(GOODS_SUPPORTED_TYPE)) {
                        handler.prepareEntityExpenseById(DBConnection, "W",
                                expenseId);
                    }
                }
                plannedParams.executeUpdateAll(DBConnection, "W");

                HashMap<Integer, HashMap<String, Double>> accountsAllValues
                        = accountsStructureSQL.executeSelectAllValues(DBConnection);
                Integer accountId;
                for (Map.Entry<Integer, HashMap<String, Double>> entry
                        : accountsAllValues.entrySet()) {
                    accountId = entry.getKey();
                    aHandler.prepareEntityAccountById(DBConnection, "W", accountId);
                }
                plannedAccountsValues.executeUpdateAll(DBConnection, "W");

                request.setAttribute("currentEntityExpenseList",
                        EntityExpenseListString());
                request.setAttribute("currentEntityAccountList",
                        EntityAccountListString());
                log.add(session, "[Shift one Period backward command entered] : "
                        + "Shifted one Period backward.");                
            } catch (GenericDBOperationException | GenericDBException ex) {
                request.setAttribute("currentEntityExpenseList",
                        EntityExpenseListString());
                request.setAttribute("currentEntityAccountList",
                        EntityAccountListString());               
                log.add(session, "[Shift one Period backward command entered] : "
                        + ex.getMessage());
            }
            request.getRequestDispatcher("index.jsp")
                    .forward(request, response);            
        }
           
        // Processing current Stock adjust operation.
        /* Defining ID of row which was selected for update and passing it 
         * as request attribute.
         */
        for (Integer id : expensesIdList) {
            if (request.getParameter("update_CURRENT_STOCK_PCS_" 
                    + String.valueOf(id)) != null) {

                request.setAttribute("currentEntityExpenseList", 
                        EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", 
                        EntityAccountListString());                
                
                request.setAttribute("rowSelectedForCurrentStockUpdate", id);
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
         * to EJB for update operation.
         */
        for (Integer id : expensesIdList) {
            if (request.getParameter("submitUpdate_CURRENT_STOCK_PCS_" 
                    + String.valueOf(id)) != null) {
                try {
                    String idToUpdate = String.valueOf(id);
                    String updateCurrentStock = request
                            .getParameter("updateCurrentStock");

                    selectedExpense = select.executeSelectById(DBConnection,
                            Integer.valueOf(idToUpdate));
                    String name = selectedExpense.getName();
                    String accountId = String.valueOf(selectedExpense
                            .getAccountId());
                    String linkedToComplexId = String.valueOf(selectedExpense
                            .getLinkedToComplexId());
                    String price = String.valueOf(selectedExpense.getPrice());

                    String safetyStockPcs = String.valueOf(selectedExpense
                            .getSafetyStockPcs());
                    String orderQtyPcs = String.valueOf(selectedExpense
                            .getOrderQtyPcs());

                    update.execute(DBConnection, name, name, accountId, 
                            linkedToComplexId, price, updateCurrentStock, 
                            safetyStockPcs, orderQtyPcs);

                    handler.prepareEntityExpenseById(DBConnection, "W", id);
                    plannedParams.executeUpdateAll(DBConnection, "W");

                    aHandler.prepareEntityAccountByExpenseId(DBConnection, "W", 
                            id);
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");

                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());                    
                    log.add(session, "[Adjust Current Stock command entered] : "
                            + "Current Stock adjusted");
                } catch (GenericDBOperationException | GenericDBException ex) {
                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());
                    log.add(session, "[Adjust Current Stock command entered] : "
                            + ex.getMessage());
                }
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }
        
        // Processing Expenses Plan (PCS) update operation.
        /* Defining ID of row which was selected for update and passing it 
         * as request attribute.
         */
        for (Integer id : expensesIdList) {
            if (request.getParameter("update_PLANNED_PCS_" 
                    + String.valueOf(id)) != null) {
                
                request.setAttribute("currentEntityExpenseList", 
                        EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", 
                        EntityAccountListString());
                
                request.setAttribute("rowSelectedForExpensesPlanPcsUpdate", id);
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
         * to EJB for update operation.
         */
        for (Integer id : expensesIdList) {
            if (request.getParameter("submitUpdate_PLANNED_PCS_" 
                    + String.valueOf(id)) != null) {
                try {
                    String idToUpdate = String.valueOf(id);

                    ArrayList<String> dates = commonMethods
                            .getDatesList(DBConnection);
                    Map<String, String> updateExpensesPlanPcsList = new TreeMap<>();
                    for (String date : dates) {
                        updateExpensesPlanPcsList.put(date, request
                                .getParameter("updateExpensesPlanPcs_" + date));
                    }
                    plannedParams.executeUpdate(DBConnection,
                            idToUpdate, "PLANNED_PCS", updateExpensesPlanPcsList);

                    handler.prepareEntityExpenseById(DBConnection, "W", id);
                    plannedParams.executeUpdateAll(DBConnection, "W");

                    aHandler.prepareEntityAccountByExpenseId(DBConnection, "W",
                            id);
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");

                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());
                    log.add(session, "[Update Expenses Plan PCS command entered] : "
                            + "Expenses Plan updated");
                } catch (GenericDBOperationException | GenericDBException ex) {
                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());
                    log.add(session, "[Update Expenses Plan PCS command entered] : "
                            + ex.getMessage());
                }
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }
        
        // Processing Consumption (PCS) update operation.
        /* Defining ID of row which was selected for update and passing it 
         * as request attribute.
         */
        for (Integer id : expensesIdList) {
            if (request.getParameter("update_CONSUMPTION_PCS_" 
                    + String.valueOf(id)) != null) {

                request.setAttribute("currentEntityExpenseList",
                        EntityExpenseListString());
                request.setAttribute("currentEntityAccountList",
                        EntityAccountListString());                
                
                request.setAttribute("rowSelectedForConsumptionPcsUpdate", id);
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
         * to EJB for update operation.
         */
        for (Integer id : expensesIdList) {
            if (request.getParameter("submitUpdate_CONSUMPTION_PCS_" 
                    + String.valueOf(id)) != null) {
                try {
                    String idToUpdate = String.valueOf(id);

                    ArrayList<String> dates = commonMethods
                            .getDatesList(DBConnection);
                    Map<String, String> updateConsumptionPcsList = new TreeMap<>();
                    for (String date : dates) {
                        updateConsumptionPcsList.put(date, request
                                .getParameter("updateConsumptionPcs_" + date));
                    }
                    plannedParams.executeUpdate(DBConnection, idToUpdate,
                            "CONSUMPTION_PCS", updateConsumptionPcsList);

                    handler.prepareEntityExpenseById(DBConnection, "W", id);
                    plannedParams.executeUpdateAll(DBConnection, "W");

                    aHandler.prepareEntityAccountByExpenseId(DBConnection, "W",
                            id);
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");

                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());
                    log.add(session, "[Update Consumption PCS command entered] : "
                            + "Expenses Plan updated");
                } catch (GenericDBOperationException | GenericDBException ex) {
                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());
                    log.add(session, "[Update Consumption PCS command entered] : "
                            + ex.getMessage());
                }
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }        
        
        // Processing Expenses Plan (CUR) update operation.
        /* Defining ID of row which was selected for update and passing it 
         * as request attribute.
         */
        for (Integer id : expensesIdList) {
            if (request.getParameter("update_PLANNED_CUR_" 
                    + String.valueOf(id)) != null) {

                request.setAttribute("currentEntityExpenseList", 
                        EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", 
                        EntityAccountListString());                
                
                request.setAttribute("rowSelectedForExpensesPlanCurUpdate", id);
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
         * to EJB for update operation.
         */
        for (Integer id : expensesIdList) {
            if (request.getParameter("submitUpdate_PLANNED_CUR_" 
                    + String.valueOf(id)) != null) {
                try {
                    String idToUpdate = String.valueOf(id);

                    ArrayList<String> dates = commonMethods
                            .getDatesList(DBConnection);
                    Map<String, String> updateExpensesPlanCurList = new TreeMap<>();
                    for (String date : dates) {
                        updateExpensesPlanCurList.put(date, request
                                .getParameter("updateExpensesPlanCur_" + date));
                    }
                    plannedParams.executeUpdate(DBConnection,
                            idToUpdate, "PLANNED_CUR", updateExpensesPlanCurList);

                    handler.prepareEntityExpenseById(DBConnection, "W", id);
                    plannedParams.executeUpdateAll(DBConnection, "W");

                    aHandler.prepareEntityAccountByExpenseId(DBConnection, "W",
                            id);
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");

                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());
                    log.add(session, "[Update Expenses Plan CUR command entered] : "
                            + "Expenses Plan updated");
                } catch (GenericDBOperationException | GenericDBException ex) {
                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());
                    log.add(session, "[Update Expenses Plan CUR command entered] : "
                            + ex.getMessage());
                }
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }
        
        // Processing Income (CUR) update operation.
        /* Defining ID of row which was selected for update and passing it 
         * as request attribute.
         */
        for (Integer id : accountsIdList) {
            if (request.getParameter("update_PLANNED_INCOME_CUR_" 
                    + String.valueOf(id)) != null) {
            
                request.setAttribute("currentEntityExpenseList", 
                        EntityExpenseListString());
                request.setAttribute("currentEntityAccountList", 
                        EntityAccountListString());
                
                request.setAttribute("rowSelectedForIncomeCurUpdate", id);
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }
        /* Defining ID of row which was submitted for update and passing it 
         * to Bean for update operation.
         */
        for (Integer id : accountsIdList) {
            if (request.getParameter("submitUpdate_PLANNED_INCOME_CUR_" 
                    + String.valueOf(id)) != null) {
                try {
                    String idToUpdate = String.valueOf(id);

                    ArrayList<String> dates = commonMethods
                            .getDatesList(DBConnection);
                    Map<String, String> updateIncomeCurList = new TreeMap<>();
                    for (String date : dates) {
                        updateIncomeCurList.put(date, request
                                .getParameter("updateIncomeCur_" + date));
                    }
                    plannedAccountsValues.executeUpdate(DBConnection, 
                            idToUpdate, "PLANNED_INCOME_CUR", 
                            updateIncomeCurList);
                    aHandler.prepareEntityAccountById(DBConnection, "W", id);
                    plannedAccountsValues.executeUpdateAll(DBConnection, "W");
                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());
                    log.add(session, "[Update Income Plan CUR command entered] : "
                            + "Income Plan updated");
                } catch (GenericDBOperationException | GenericDBException ex) {
                    request.setAttribute("currentEntityExpenseList",
                            EntityExpenseListString());
                    request.setAttribute("currentEntityAccountList",
                            EntityAccountListString());
                    log.add(session, "[Update Income Plan CUR command entered] : "
                            + ex.getMessage());
                }
                request.getRequestDispatcher("index.jsp")
                        .forward(request, response);
            }
        }
    }

    /**
     * Converts collection of EntityExpense objects that is held in 
     * {@link ejb.calculation.EntityExpenseList#expenseList} into String 
     * representing HTML bulleted list.
     * 
     * @return EntityExpense objects converted to String in HTML bulleted list
     * form.
     */
    private String EntityExpenseListString() {
        ArrayList<EntityExpense> list = handler.getEntityExpenseList();
        StringBuilder sb = new StringBuilder();
        for (EntityExpense e : list) {
            sb.append("<li>").append(e.toString()).append("</li>");
        }
        return sb.toString();
    }

    /**
     * Converts collection of EntityAccount objects that is held in 
     * {@link ejb.calculation.EntityAccountList#accountList} into String 
     * representing HTML bulleted list.
     * 
     * @return EntityAccount objects converted to String in HTML bulleted list
     * form.
     */
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
