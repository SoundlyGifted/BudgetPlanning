<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.*, java.util.*"%>
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

    <!-- JSTL sql query to select all records from ACTUAL_EXPENSES table -->
    <sql:query dataSource = "${outputDBConnection}" var = "actualExpensesResultSet">
        SELECT * FROM ACTUAL_EXPENSES ORDER BY DATE DESC, ID DESC
    </sql:query>

    <!-- JSTL sql query to select all records from EXPENSES_STRUCTURE table -->
    <sql:query dataSource = "${outputDBConnection}" var = "expensesStructureResultSet">
        SELECT * FROM EXPENSES_STRUCTURE
    </sql:query>        

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/NavigationBarStyles.css" />
        <link rel="stylesheet" href="css/GeneralStyles.css" />
        <title>Actual Expenses Module</title>
    </head>    

        <!-- Navigation Bar at the top of the screen -->
        <div class="fixed_top_navigation_bar">
            <ul class="menu">
                <li><a href="index.jsp">Main Screen</a></li>
                <li><a href="ExpensesStructurePage.jsp">Expenses Structure</a></li>
                <li><a href="ActualExpensesPage.jsp">Actual Expenses</a></li>
            </ul>
            <div class="App_Title_Nav_Bar1">Actual Expenses Module</div>
            <div class="App_Title_Nav_Bar2">Budget Planning Application</div>
        </div>
    
    <body>

        <div class="inputBlock1">
            <!-- System Message Log text area -->
            <textarea id="systemMessageLog_id" class="systemMessageLogTextArea" rows="2" readonly title="System Message Log">${operationResult}</textarea>
            <!-- Keeping System Message Log scroll at the bottom after form request sent. -->
            <script>
                var textarea = document.getElementById('systemMessageLog_id');
                textarea.scrollTop = textarea.scrollHeight;
            </script>
            <form action="ActualExpensesServlet">
                <input type="submit" class="button" value="Refresh" name="refresh"/>
                <input type="submit" class="button" value="Clear Log" name="clearLog"/>
            </form>
        </div>

        <!-- Output Block Table showing current DB data -->
        <div class="outputBlock1">
            <form action="ActualExpensesServlet">
                <table class="outputTable2">
                    <caption>
                        Actual Expenses Log
                    </caption>
                    <tr>
                        <th>Date</th>
                        <th>Week</th>
                        <th>Week Day</th>                    
                        <th>Month</th>            
                        <th>Year</th>
                        <th>Expense Name</th>
                        <th>Expense Title</th>
                        <th>Shop Name</th>
                        <th>Price</th>
                        <th>Quantity</th>
                        <th>Cost</th>
                        <th>Comment</th>
                        <th></th>
                        <th></th>
                    </tr>

                    <!-- Input table row with Add button. -->
                    <tr>
                        <%  SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                            request.setAttribute("currentDate", fmt.format(new Date()));
                        %>                        
                        <td><input <c:if test="${requestScope.rowSelectedForUpdate != null}">disabled</c:if> type="date" class="inputTextBox" value="${currentDate}" size="15" name="inputDate"/></td>
                            <td></td>
                            <td></td>                        
                            <td></td>
                            <td></td>
                            <td>
                                <!--entered expense selection dropdown list.-->              
                                <select <c:if test="${requestScope.rowSelectedForUpdate != null}">disabled</c:if> name="inputName" class="inputTextBox">
                                    <option value="" selected disabled hidden>Choose Expense</option>
                                <c:forEach var="row2" items="${expensesStructureResultSet.rows}">
                                    <option value="${row2.NAME}">${row2.NAME}</option>
                                </c:forEach>
                            </select>                            
                        </td>
                        <td><input <c:if test="${requestScope.rowSelectedForUpdate != null}">disabled</c:if> type="text" class="inputTextBox" value="" size="10" name="inputTitle" placeholder="..." maxlength="255"/></td>
                        <td><input <c:if test="${requestScope.rowSelectedForUpdate != null}">disabled</c:if> type="text" class="inputTextBox" value="" size="10" name="inputShop" placeholder="..." maxlength="255"/></td>
                        <td><input <c:if test="${requestScope.rowSelectedForUpdate != null}">disabled</c:if> type="text" class="inputTextBox" value="" size="10" name="inputPrice" placeholder="..."/></td>
                        <td><input <c:if test="${requestScope.rowSelectedForUpdate != null}">disabled</c:if> type="text" class="inputTextBox" value="" size="10" name="inputQty" placeholder="..."/></td>
                            <td></td>
                            <td><input <c:if test="${requestScope.rowSelectedForUpdate != null}">disabled</c:if> type="text" class="inputTextBox" value="" size="15" name="inputComment" placeholder="..."/></td>
                        <td><c:if test="${requestScope.rowSelectedForUpdate == null}"><input type="submit" class="button" value="Add" name="addActualExpense"/></c:if></td>
                            <td></td>
                        </tr>                 

                        <!-- Data table rows with Update / Delete, Submit / Cancel buttons. -->
                    <c:forEach var = "row" items = "${actualExpensesResultSet.rows}">
                        <tr>
                            <c:set var="thisRow" value="${row.ID}"/>
                            <c:choose>
                                <c:when test="${row.ID == requestScope.rowSelectedForUpdate}">
                                    <td><input type="date" class="inputTextBox" value="${row.DATE}" size="15" name="updateDate"/></td>
                                    <td><c:out value = "${row.WEEK}"/></td>
                                    <td><c:out value = "${row.DAY_C}"/></td>                        
                                    <td><c:out value = "${row.MONTH_C}"/></td>
                                    <td><c:out value = "${row.YEAR}"/></td>
                                    <td>
                                        <!--entered expense selection dropdown list.-->              
                                        <select name="updateName" class="inputTextBox">
                                            <option value="${row.EXPENSE_NAME}" selected hidden>${row.EXPENSE_NAME}</option>                                            
                                            <c:forEach var="row2" items="${expensesStructureResultSet.rows}">
                                                <option value="${row2.NAME}">${row2.NAME}</option>
                                            </c:forEach>
                                        </select>                                          
                                    </td>
                                    <td><input type="text" class="inputTextBox" value="${row.EXPENSE_TITLE}" size="10" name="updateTitle" placeholder="..." maxlength="255"/></td>
                                    <td><input type="text" class="inputTextBox" value="${row.SHOP_NAME}" size="10" name="updateShop" placeholder="..." maxlength="255"/></td>
                                    <td><input type="text" class="inputTextBox" value="${row.PRICE}" size="10" name="updatePrice" placeholder="..."/></td>
                                    <td><input type="text" class="inputTextBox" value="${row.QTY}" size="10" name="updateQty" placeholder="..."/></td>
                                    <td><c:out value = "${row.COST}"/></td>
                                    <td><input type="text" class="inputTextBox" value="${row.COMMENT}" size="15" name="updateComment" placeholder="..."/></td>
                                    <td><input type="submit" class="button" value="Submit" name="submitUpdate_${row.ID}"/></td>
                                    <td><input type="submit" class="button" value="Cancel" name="cancelUpdate_${row.ID}"/></td>                             
                                </c:when>
                                <c:otherwise>
                                    <td> <c:out value = "${row.DATE}"/></td>
                                    <td> <c:out value = "${row.WEEK}"/></td>
                                    <td> <c:out value = "${row.DAY_C}"/></td>                        
                                    <td> <c:out value = "${row.MONTH_C}"/></td>
                                    <td> <c:out value = "${row.YEAR}"/></td>
                                    <td> <c:out value = "${row.EXPENSE_NAME}"/></td>
                                    <td> <c:out value = "${row.EXPENSE_TITLE}"/></td>
                                    <td> <c:out value = "${row.SHOP_NAME}"/></td>
                                    <td> <c:out value = "${row.PRICE}"/></td>
                                    <td> <c:out value = "${row.QTY}"/></td>
                                    <td> <c:out value = "${row.COST}"/></td>
                                    <td> <c:out value = "${row.COMMENT}"/></td>
                                    <td><input type="submit" class="button" value="Update" name="update_${row.ID}"/></td>
                                    <td><input type="submit" class="button" value="Delete" name="delete_${row.ID}"/></td>
                                </c:otherwise>                            
                            </c:choose>     
                        </tr>
                    </c:forEach>
                </table>
            </form>
        </div>

    </body>
</html>
