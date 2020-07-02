<%@page import="ejb.entity.EntityExpense"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix = "sql"%>
<!DOCTYPE html>
<html>
    
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
                <table class="inputTable" cellpadding="10" rules="cols">
                    <caption>
                        Expense Category to Update
                    </caption>
                    <tr>
                        <th>Expense Attribute Name</th>
                        <th>Change Value</th>
                        <th>Current Value</th>
                        <th>Clear Attribute</th>   
                    </tr>
                    <tr valign="top">
                        <td><b>Name</b></td>
                        <td><input type="text" class="inputTextBox" value="${currentName}" size="15" name="updateNewName" placeholder="[not set]" maxlength="255"/></td>
                        <td><b>${currentName}</b></td>
                        <td></td>                        
                    </tr>
                    <tr valign="top">
                        <td><b>Account Name</b></td>
                        <td><input type="text" class="inputTextBox" value="${currentAccount}" size="15" name="updateAccountName" placeholder="[not set]" maxlength="255"/></td>
                        <td><b>${currentAccount}</b></td>
                        <td><input type="submit" class="button" value="Clear" name="clearAssignmentToAccount"/></td>                        
                    </tr>                    
                    <tr valign="top">
                        <td><b>Linked to Complex Expense Name</b></td>
                        <td><input type="text" class="inputTextBox" value="${currentLinkedToComplExpName}" size="15" name="updateLinkedComplExpName" placeholder="[not set]"/></td>
                        <td><b>${currentLinkedToComplExpName}</b></td>
                        <td><input type="submit" class="button" value="Clear" name="clearAssignmentToComplExp"/></td>                        
                    </tr>
                    <c:if test="${ExpensesStructure_ExpenseSelectedType == 'GOODS'}">                 
                        <tr valign="top">
                            <td><b>Price</b></td>
                            <td><input type="text" class="inputTextBox" value="" size="15" name="updatePrice" placeholder="${currentPrice}"/></td>
                            <td><b>${currentPrice}</b></td>
                            <td></td>                        
                        </tr>                    
                        <tr valign="top">
                            <td><b>Safety Stock, pcs</b></td>
                            <td><input type="text" class="inputTextBox" value="" size="15" name="updateSafetyStockPcs" placeholder="${currentSafetyStockPcs}"/></td>
                            <td><b>${currentSafetyStockPcs}</b></td>
                            <td></td>                        
                        </tr>                    
                        <tr valign="top">
                            <td><b>Order QTY, pcs</b></td>
                            <td><input type="text" class="inputTextBox" value="" size="15" name="updateOrderQtyPcs" placeholder="${currentOrderQtyPcs}"/></td>
                            <td><b>${currentOrderQtyPcs}</b></td>
                            <td></td>                        
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
