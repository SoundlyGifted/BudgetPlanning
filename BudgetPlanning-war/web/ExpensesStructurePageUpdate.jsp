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
    
<!-- JSTL sql query to select ID and NAME from EXPENSES_STRUCTURE table -->
<sql:query dataSource = "${outputDBConnection}" var = "complexExpenseResultSet">
    SELECT ID, NAME FROM EXPENSES_STRUCTURE
    WHERE TYPE = 'COMPLEX_EXPENSES'
</sql:query>

<!-- JSTL sql query to select ID and NAME from ACCOUNTS_STRUCTURE table -->
<sql:query dataSource = "${outputDBConnection}" var = "accountsStructureResultSet">
    SELECT ID, NAME FROM ACCOUNTS_STRUCTURE
</sql:query>   
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/NavigationBarStyles.css" />
        <link rel="stylesheet" href="css/GeneralStyles.css" />
        <title>Expenses Structure Module : Update</title>
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

            <!-- User form -->
            <form action="ExpensesStructureServletUpdate">
                <input type="submit" class="button" value="Refresh" name="refresh"/>
                <input type="submit" class="button" value="Clear Log" name="clearLog"/>               
                <br>                
                <br>
                <table class="inputTable" cellpadding="10" rules="cols" style="width:65%">
                    <caption>
                        Expense Category to Update
                    </caption>
                    <tr>
                        <th style="width:28%">Expense Attribute Name</th>
                        <th style="width:35%">Change Value</th>
                        <th style="width:35%">Current Value</th>   
                    </tr>
                    <tr valign="top">
                        <td><b>Name</b></td>
                        <td><input type="text" class="inputTextBox" value="${currentName}" size="30" name="updateNewName" placeholder="[not set]" maxlength="255"/></td>
                        <td><b>${currentName}</b></td>                       
                    </tr>
                    <tr valign="top">
                        <td><b>Account Name</b></td>
                        <td>
                            <!--Account selection dropdown list.-->              
                            <select name="accountIDSelected" class="inputTextBox" style="width:340px">
                                <option value="${currentAccountId}" selected disabled hidden>${currentAccount}</option>
                                <c:forEach var="row" items="${accountsStructureResultSet.rows}">
                                    <option value="${row.ID}">
                                        ${row.NAME}
                                    </option>
                                </c:forEach>
                            </select>
                        </td>
                        <td><b>${currentAccount}</b></td>                      
                    </tr>
                    <c:if test="${ExpensesStructure_ExpenseSelectedType != 'COMPLEX_EXPENSES'}">
                        <tr valign="top">
                            <td><b>Linked to Complex Expense Name</b></td>
                            <td>
                                <!--Complex Expense selection dropdown list.-->              
                                <select name="complexExpenseIDSelected" class="inputTextBox" style="width:340px">
                                    <option value="${currentComplexExpenseId}" selected disabled hidden>${currentLinkedToComplExpName}</option>
                                    <option value="0">NOT SET</option>
                                    <c:forEach var="row" items="${complexExpenseResultSet.rows}">
                                        <option value="${row.ID}">
                                            ${row.NAME}
                                        </option>
                                    </c:forEach>
                                </select>
                            </td>
                            <td><b>${currentLinkedToComplExpName}</b></td>                 
                        </tr>                        
                    </c:if>
                    <c:if test="${ExpensesStructure_ExpenseSelectedType == 'GOODS'}">                 
                        <tr valign="top">
                            <td><b>Price</b></td>
                            <td><input type="text" class="inputTextBox" value="" size="30" name="updatePrice" placeholder="${currentPrice}"/></td>
                            <td><b>${currentPrice}</b></td>                      
                        </tr>                    
                        <tr valign="top">
                            <td><b>Safety Stock, pcs</b></td>
                            <td><input type="text" class="inputTextBox" value="" size="30" name="updateSafetyStockPcs" placeholder="${currentSafetyStockPcs}"/></td>
                            <td><b>${currentSafetyStockPcs}</b></td>                       
                        </tr>                    
                        <tr valign="top">
                            <td><b>Order QTY, pcs</b></td>
                            <td><input type="text" class="inputTextBox" value="" size="30" name="updateOrderQtyPcs" placeholder="${currentOrderQtyPcs}"/></td>
                            <td><b>${currentOrderQtyPcs}</b></td>                     
                        </tr>                    
                    </c:if>                    
                </table>
                <br>        
                <input type="submit" class="button" value="Update" name="executeUpdate"/>
                <input type="submit" class="button" value="Return" name="return"/>
            </form>
        </div>                                     
    </body>
</html>
