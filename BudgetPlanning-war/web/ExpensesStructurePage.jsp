<%@page import="java.io.*, java.util.*, java.sql.*"%>
<%@page import="ejb.calculation.EntityExpense"%>
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
<sql:query dataSource = "${outputDBConnection}" var = "expensesStructureOutput">
    SELECT T1.ID, T1.TYPE, T1.NAME, T1.ACCOUNT_LINKED, 
    T2.NAME as COMPLEX_EXP_NAME_ASSIGNED, T1.PRICE, 
    T1.SAFETY_STOCK_PCS, T1.SAFETY_STOCK_CUR,
    T1.ORDER_QTY_PCS, T1.ORDER_QTY_CUR
    from EXPENSES_STRUCTURE T1
    left join
    EXPENSES_STRUCTURE T2
    on T2.ID = T1.LINKED_TO_COMPLEX_ID
    where T1.ID > 0
</sql:query>       

<!-- JSTL sql query to select ID and NAME from ACCOUNTS_STRUCTURE table -->
<sql:query dataSource = "${outputDBConnection}" var = "accountsStructureResultSet">
    SELECT ID, NAME FROM ACCOUNTS_STRUCTURE
</sql:query>      
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/NavigationBarStyles.css" />
        <link rel="stylesheet" href="css/GeneralStyles.css" />
        <title>Expenses Structure Module</title>
    </head>
    
    <body>
        
        <!-- Navigation Bar at the top of the screen -->
        <div class="fixed_top_navigation_bar">
            <ul class="menu">
                <li><a href="index.jsp">Main Screen</a></li>
                <li><a href="ExpensesStructurePage.jsp">Expenses Structure</a></li>
                <li><a href="ActualExpensesPage.jsp">Actual Expenses</a></li>
                <li><a href="AccountsStructurePage.jsp">Accounts Structure</a></li>
            </ul>
            <div class="App_Title_Nav_Bar1">Expenses Structure Module</div>
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
            
            <form action="LogServlet">
                <% String uri = request.getRequestURI();
                   String pageName = uri.substring(uri.lastIndexOf("/")+1);
                %>
                <input type="hidden" name="pageName" value=<%= pageName %>>
                <input type="submit" class="button" value="Refresh" name="refresh"/>
                <input type="submit" class="button" value="Clear Log" name="clearLog"/>
            </form>            
            
            <!-- User form -->
            <form action="ExpensesStructureServlet">               
                <h5>Add Expense Category</h5>
                <select name="inputType" class="inputTextBox">
                    <option value="" selected disabled hidden>Choose Expense Type</option>
                    <option value="SIMPLE_EXPENSES">SIMPLE_EXPENSES</option>
                    <option value="COMPLEX_EXPENSES">COMPLEX_EXPENSES</option>
                    <option value="GOODS">GOODS</option>
                </select>
                <input type="text" class="inputTextBox" value="" size="30" name="inputName" placeholder="Expense Name" maxlength="255"/>

                <!--Account selection dropdown list.-->              
                <select name="accountIDSelected" class="inputTextBox" style="width:200px">
                    <option value="" selected disabled hidden>Choose Account</option>
                    <c:forEach var="row" items="${accountsStructureResultSet.rows}">
                        <option value="${row.ID}">
                            ${row.NAME}
                        </option>
                    </c:forEach>
                </select>                

                <input type="text" class="inputTextBox" value="" size="15" name="inputPrice" placeholder="Price [GOODS only]"/>
                <input type="text" class="inputTextBox" value="" size="15" name="inputSafetyStockPcs" placeholder="Safety Stock [GOODS only]"/>
                <input type="text" class="inputTextBox" value="" size="15" name="inputOrderQtyPcs" placeholder="Order QTY [GOODS only]"/>
                <input type="submit" class="button" value="Create" name="executeInsert"/>
                
                <h5>Update / Delete Expense Category</h5>
                <!--Expense selection dropdown list.-->              
                <select name="updateExpenseUserSelected" class="inputTextBox">
                    <option value="" selected disabled hidden>Choose Expense</option>
                    <c:forEach var="row" items="${expensesStructureOutput.rows}">
                        <option value="${row.ID}">
                            ${row.TYPE}: ${row.NAME}
                        </option>
                    </c:forEach>
                </select>
                <input type="submit" class="button" value="Update" name="selectForUpdate"/>
                <input type="submit" class="button" value="Delete" name="delete"/>
            </form>
        </div>

        <!-- Output Block Table showing current DB data -->        
        <div class="outputBlock1">  
            <table class="outputTable1">
                <caption>
                    Expense Categories List
                </caption>
                <tr>
                    <th rowspan="2">ID</th>
                    <th rowspan="2">Type</th>
                    <th rowspan="2">Expense Name</th>
                    <th rowspan="2">Linked Account</th>
                    <th rowspan="2">Linked To Complex Expense Named</th>            
                    <th rowspan="2">Price</th>
                    <th colspan="2" >Safety Stock</th>
                    <th colspan="2">Order QTY</th> 
                </tr>
                <tr>
                    <th>pcs</th>
                    <th>cur</th>
                    <th>pcs</th> 
                    <th>cur</th> 
                </tr>
                <c:forEach var = "row" items = "${expensesStructureOutput.rows}">
                    <tr>
                        <td> <c:out value = "${row.ID}"/></td>
                        <td> <c:out value = "${row.TYPE}"/></td>
                        <td> <c:out value = "${row.NAME}"/></td>
                        <td> <c:out value = "${row.ACCOUNT_LINKED}"/></td>
                        <td> <c:out value = "${row.COMPLEX_EXP_NAME_ASSIGNED}"/></td>
                        <c:choose>
                            <c:when test="${row.TYPE == 'GOODS'}">
                                <td> <c:out value = "${row.PRICE}"/></td>
                                <td> <c:out value = "${row.SAFETY_STOCK_PCS}"/></td>
                                <td> <c:out value = "${row.SAFETY_STOCK_CUR}"/></td>
                                <td> <c:out value = "${row.ORDER_QTY_PCS}"/></td>
                                <td> <c:out value = "${row.ORDER_QTY_CUR}"/></td>                                
                            </c:when>
                            <c:otherwise>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>                                   
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
            </table>          
        </div>
    </body>
</html>