<%@page import="java.io.*, java.util.*, java.sql.*"%>
<%@page import="ejb.entity.EntityExpense"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix = "sql"%>

<!DOCTYPE html>
<html>

    <!-- JSTL database connection -->
    <sql:setDataSource var = "outputDBConnection" driver = "org.apache.derby.jdbc.ClientDriver"
                       url = "jdbc:derby://localhost:1527/BudgetPlanningAppDB"
                       user = "app"  password = "app"/>    

    <!-- JSTL sql query to select all records from EXPENSES_STRUCTURE table -->
    <sql:query dataSource = "${outputDBConnection}" var = "expensesStructureResultSet">
        SELECT * FROM EXPENSES_STRUCTURE
    </sql:query>

    <!-- JSTL sql query to select all records from PLANNED_VARIABLE_PARAMS table -->    
    <sql:query dataSource = "${outputDBConnection}" var = "plannedParamsResultSet">
        SELECT * FROM PLANNED_VARIABLE_PARAMS
        order by DATE
    </sql:query>

    <!-- JSTL sql query to select all distinct timing parameter values from PLANNED_VARIABLE_PARAMS table -->    
    <sql:query dataSource = "${outputDBConnection}" var = "timelineResultSet">
        SELECT DISTINCT DATE, WEEK, DAY_N, DAY_C, MONTH_C, "YEAR", CURPFL 
        FROM PLANNED_VARIABLE_PARAMS
        order by DATE
    </sql:query>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/NavigationBarStyles.css" />
        <link rel="stylesheet" href="css/GeneralStyles.css" />
        <link rel="stylesheet" href="css/PlanningDataTable.css" />
        <title>Budget Planning Application</title>
    </head>
    <body>

        <!-- Navigation Bar at the top of the screen -->
        <div class="fixed_top_navigation_bar">
            <ul class="menu">
                <li><a href="index.jsp">Main Screen</a></li>
                <li><a href="ExpensesStructurePage.jsp">Expenses Structure</a></li>
                <li><a href="ActualExpensesPage.jsp">Actual Expenses</a></li>
            </ul>
            <div class="App_Title_Nav_Bar1">Main Screen</div>
            <div class="App_Title_Nav_Bar2">Budget Planning Application</div>
        </div>        

        <div class="inputBlock1">        
            <!-- System Message Log text area -->
            <textarea id="systemMessageLog_id" class="systemMessageLogTextArea" rows="2" readonly title="System Message Log">${operationResult}</textarea>
            <!-- Keeping System Message Log scroll at the bottom after form request sent. -->
            <script>
                var textarea = document.getElementById('systemMessageLog_id');
                textarea.scrollTop = textarea.scrollHeight;
            </script>

            <!-- User form -->
            <form action="">            
                <input type="submit" class="button" value="Refresh" name="refresh"/>
                <input type="submit" class="button" value="Clear Log" name="clearLog"/>
            </form>            
        </div>

        <div class="outputBlock1">
            <table class="planningDataTable">

                <caption>
                    Planning Data
                </caption>

                <!-- Table Header -->
                <tr><th rowspan="6" colspan="7" class="sticky-header1" style="top: 50px">Timeline</th>
                    <th class="sticky-header1" style="top: 50px">Year</th>
                        <c:forEach var = "row" items = "${timelineResultSet.rows}">
                        <th class="sticky-header1" style="top: 50px"><c:out value = "${row.YEAR}"/></th>
                        </c:forEach>
                </tr>
                <tr>
                    <th class="sticky-header1" style="top: 76px">Month</th>
                        <c:forEach var = "row" items = "${timelineResultSet.rows}">
                        <th class="sticky-header1" style="top: 76px"><c:out value = "${row.MONTH_C}"/></th>
                        </c:forEach>
                </tr>                
                <tr>
                    <th class="sticky-header1" style="top: 102px">Week</th>
                        <c:forEach var = "row" items = "${timelineResultSet.rows}">
                        <th class="sticky-header1" style="top: 102px"><c:out value = "${row.WEEK}"/></th>
                        </c:forEach>
                </tr>                
                <tr>
                    <th class="sticky-header1" style="top: 128px">Date</th>
                        <c:forEach var = "row" items = "${timelineResultSet.rows}">
                        <th class="sticky-header1" style="top: 128px"><c:out value = "${row.DAY_N}"/></th>
                        </c:forEach>                    
                </tr>
                <tr>
                    <th class="sticky-header1" style="top: 154px">Day</th>
                        <c:forEach var = "row" items = "${timelineResultSet.rows}">
                        <th class="sticky-header1" style="top: 154px"><c:out value = "${row.DAY_C}"/></th>
                        </c:forEach>   
                </tr>
                <tr>
                    <th class="sticky-header1" style="top: 180px">Status</th>
                        <c:forEach var = "row" items = "${timelineResultSet.rows}">
                            <c:choose>
                                <c:when test="${row.CURPFL == 'Y'}"><th class="sticky-header1" style="top: 180px">Current</th></c:when>
                            <c:otherwise><th class="sticky-header1" style="top: 180px">Planned</th></c:otherwise>
                            </c:choose>
                        </c:forEach>
                </tr>
                <tr>
                    <th rowspan="2" class="sticky-header2" style="top: 206px">Expense Name</th>
                    <th rowspan="2" class="sticky-header2" style="top: 206px">Expense Type</th>
                    <th colspan="2" class="sticky-header2" style="top: 206px">Parameter</th>
                    <th rowspan="2" class="sticky-header2" style="top: 206px">Value</th>
                    <th rowspan="2" class="sticky-header2" style="top: 206px">Data</th>
                    <th rowspan="2" class="sticky-header2" style="top: 206px">UM</th>
                    <th rowspan="2" class="sticky-header2" style="top: 206px"></th>
                        <c:forEach var = "row" items = "${timelineResultSet.rows}">
                        <th rowspan="2" class="sticky-header2" style="top: 206px"></th>
                        </c:forEach>                    
                </tr>
                <tr>
                    <th class="sticky-header2" style="top: 232.5px">Name</th>
                    <th class="sticky-header2" style="top: 232.5px">UM</th>
                </tr>

                <!-- Table Data -->
                <c:forEach var="row" items="${expensesStructureResultSet.rows}">
                    <c:choose>
                        <c:when test="${row.TYPE == 'GOODS'}">
                            <tr style="border-top: 3px solid #00aaff">
                                <td><c:out value = "${row.NAME}"/></td>
                                <td><c:out value = "${row.TYPE}"/></td>
                                <td>Price</td>
                                <td>CUR</td>
                                <td><c:out value = "${row.PRICE}"/></td>
                                <td>Consumption</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.CONSUMPTION_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                
                            </tr>                             
                            <tr>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td>Consumption</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.CONSUMPTION_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                   
                            </tr> 
                            <tr>
                                <td></td>
                                <td></td>
                                <td>Safety Stock</td>
                                <td>PCS</td>
                                <td><c:out value = "${row.SAFETY_STOCK_PCS}"/></td>
                                <td>Stock Plan</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.STOCK_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                   
                            </tr> 
                            <tr>
                                <td></td>
                                <td></td>
                                <td>Safety Stock</td>
                                <td>CUR</td>
                                <td><c:out value = "${row.SAFETY_STOCK_CUR}"/></td>
                                <td>Stock Plan</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.STOCK_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr>
                            <tr>
                                <td></td>
                                <td></td>
                                <td>Order Qty</td>
                                <td>PCS</td>
                                <td><c:out value = "${row.ORDER_QTY_PCS}"/></td>
                                <td>Requirement</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.REQUIREMENT_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr>
                                <td></td>
                                <td></td>
                                <td>Order Qty</td>
                                <td>CUR</td>
                                <td><c:out value = "${row.ORDER_QTY_CUR}"/></td>
                                <td>Requirement</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.REQUIREMENT_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr>
                                <td></td>
                                <td></td>
                                <td>Stock Current</td>
                                <td>PCS</td>
                                <td><c:out value = "${row.CURRENT_STOCK_PCS}"/></td>
                                <td>Expenses Plan</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.PLANNED_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                  
                            </tr> 
                            <tr>
                                <td></td>
                                <td></td>
                                <td>Stock Current</td>
                                <td>CUR</td>
                                <td><c:out value = "${row.CURRENT_STOCK_CUR}"/></td>
                                <td>Expenses Plan</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.PLANNED_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr>
                                <td></td>
                                <td></td>
                                <td>Stock Current WSC</td>
                                <td>PCS</td>
                                <td><c:out value = "${row.CURRENT_STOCK_WSC_PCS}"/></td>
                                <td>Expenses Actual</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.ACTUAL_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr>
                                <td></td>
                                <td></td>
                                <td>Stock Current WSC</td>
                                <td>CUR</td>
                                <td><c:out value = "${row.CURRENT_STOCK_WSC_CUR}"/></td>
                                <td>Expenses Actual</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.ACTUAL_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr>
                            <tr>
                                <td></td>
                                <td></td>
                                <td>Linked Account</td>
                                <td>Text</td>
                                <td><c:out value = "${row.ACCOUNT_LINKED}"/></td>
                                <td>Expenses Diff (Act - Pl)</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.DIFFERENCE_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td>Expenses Diff (Act - Pl)</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.DIFFERENCE_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                  
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr style="border-top: 3px solid #00aaff">
                                <td><c:out value = "${row.NAME}"/></td>
                                <td><c:out value = "${row.TYPE}"/></td>
                                <td>Linked Account</td>
                                <td>Text</td>
                                <td><c:out value = "${row.ACCOUNT_LINKED}"/></td>
                                <td>Expenses Plan</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.PLANNED_CUR}"/></td>
                                    </c:if>
                                </c:forEach>
                            </tr>
                            <tr>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td>Expenses Actual</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.ACTUAL_CUR}"/></td>
                                    </c:if>
                                </c:forEach>
                            </tr> 
                            <tr>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td>Expenses Diff (Act - Pl)</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.DIFFERENCE_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                            
            </table>

        </div>

    </body>
</html>
