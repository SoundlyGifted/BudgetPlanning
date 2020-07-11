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

    <!-- JSTL sql query to select all records from ACCOUNTS_STRUCTURE table -->
    <sql:query dataSource = "${outputDBConnection}" var = "accountsStructureResultSet">
        SELECT * FROM ACCOUNTS_STRUCTURE
    </sql:query>      

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/NavigationBarStyles.css" />
        <link rel="stylesheet" href="css/GeneralStyles.css" />
        <title>Accounts Structure Module</title>
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
            <div class="App_Title_Nav_Bar1">Accounts Structure Module</div>
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
            <form action="AccountsStructureServlet">            
                <input type="submit" class="button" value="Refresh" name="refresh"/>
                <input type="submit" class="button" value="Clear Log" name="clearLog"/>
                
                <h5>Add Account</h5>
                <input type="text" class="inputTextBox" value="" size="30" name="inputName" placeholder="Account Name" maxlength="255"/>
                <input type="text" class="inputTextBox" value="" size="20" name="inputCurrentRemainder" placeholder="Current Remainder, cur"/>
                <input type="submit" class="button" value="Create" name="addAccount"/>
            </form>
        </div>
            
        <!-- Output Block Table showing current DB data -->
        <div class="outputBlock1">
            <form action="AccountsStructureServlet">
                <table class="outputTable2" style="width:55%">
                    <caption>
                        Accounts List
                    </caption>
                    <tr>
                        <th style="width:5%">ID</th>
                        <th style="width:40%">Account Name</th>
                        <th style="width:25%">Current Remainder, cur</th>
                        <th style="width:15%"></th>
                        <th style="width:15%"></th>
                    </tr>                

                    <!-- Data table rows with Update / Delete, Submit / Cancel buttons. -->
                    <c:forEach var = "row" items = "${accountsStructureResultSet.rows}">
                        <c:if test="${row.ID != 0}"> 
                            <tr>
                                <c:set var="thisRow" value="${row.ID}"/>
                                <c:choose>
                                    <c:when test="${row.ID == requestScope.rowSelectedForUpdate}">
                                        <td><c:out value = "${row.ID}"/></td>
                                        <td><input type="text" class="inputTextBox" value="${row.NAME}" size="30" name="updateName" placeholder="..." maxlength="255"/></td>
                                        <td><input type="text" class="inputTextBox" value="${row.CURRENT_REMAINDER_CUR}" size="15" name="updateCurrentRemainder" placeholder="..."/></td>
                                        <td><input type="submit" class="button" value="Submit" name="submitUpdate_${row.ID}"/></td>
                                        <td><input type="submit" class="button" value="Cancel" name="cancelUpdate_${row.ID}"/></td>                             
                                    </c:when>
                                    <c:otherwise>
                                        <td> <c:out value = "${row.ID}"/></td>
                                        <td> <c:out value = "${row.NAME}"/></td>
                                        <td> <c:out value = "${row.CURRENT_REMAINDER_CUR}"/></td>                        
                                        <td><input type="submit" class="button" value="Update" name="update_${row.ID}"/></td>
                                        <td><input type="submit" class="button" value="Delete" name="delete_${row.ID}"/></td>
                                    </c:otherwise>                          
                                </c:choose>     
                            </tr>
                        </c:if>
                    </c:forEach>
                </table>
            </form>
        </div>            
            
    </body>
    
</html>
