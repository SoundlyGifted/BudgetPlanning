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
<sql:query dataSource = "${outputDBConnection}" var = "expensesStructureOutput">
    SELECT T1.ID, T1.TYPE, T1.NAME, T1.ACCOUNT_LINKED, T2.NAME as COMPLEX_EXP_NAME_ASSIGNED, T1.PRICE, T1.SAFETY_STOCK, T1.ORDER_QTY, T1.SHOP_NAME 
    from EXPENSES_STRUCTURE T1
    left join
    EXPENSES_STRUCTURE T2
    on T2.ID = T1.LINKED_TO_COMPLEX_ID
</sql:query>       

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/ExpensesStructurePageStyles.css" />
        <title>Expenses Structure Page</title>
    </head>
    
    <body>
        <h1>Expenses Structure module</h1>
        <div class="inputBlock1">
            <!-- System Message Log text area -->
            <textarea id="systemMessageLog_id" class="systemMessageLogTextArea" rows="2" readonly title="System Message Log">${operationResult}</textarea>
            <!-- Keeping System Message Log scroll at the bottom after form request sent. -->
            <script>
                var textarea = document.getElementById('systemMessageLog_id');
                textarea.scrollTop = textarea.scrollHeight;
            </script>
            
            <!-- User form -->
            <form action="ExpensesStructureServlet">            
                <input type="submit" class="button" value="Refresh" name="refresh"/>
                <input type="submit" class="button" value="Clear Log" name="clearLog"/>
                
                <h5>Add Expense Category</h5>
                <select name="inputType" class="inputTextBox">
                    <option value="" selected disabled hidden>Choose Expense Type</option>
                    <option value="SIMPLE_EXPENSES">SIMPLE_EXPENSES</option>
                    <option value="COMPLEX_EXPENSES">COMPLEX_EXPENSES</option>
                    <option value="GOODS">GOODS</option>
                </select>
                <input type="text" class="inputTextBox" value="" size="15" name="inputName" placeholder="Expense Name" maxlength="255"/>
                <input type="text" class="inputTextBox" value="" size="15" name="inputAccountName" placeholder="Assigned Account" maxlength="255"/>
                <input type="text" class="inputTextBox" value="" size="15" name="inputPrice" placeholder="Price [GOODS only]"/>
                <input type="text" class="inputTextBox" value="" size="15" name="inputSafetyStock" placeholder="Safety Stock [GOODS only]"/>
                <input type="text" class="inputTextBox" value="" size="15" name="inputOrderQty" placeholder="Order QTY [GOODS only]"/>
                <input type="text" class="inputTextBox" value="" size="15" name="inputShopName" placeholder="Shop Name [GOODS only]" maxlength="255"/>
                <input type="submit" class="button" value="Create" name="executeInsert"/>
                
                <h5>Update / Delete Expense Category</h5>
                <!--entered expense selection dropdown list.-->              
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
            <table class="outputTable">
                <caption>
                    Expense Categories List
                </caption>
                <tr>
                    <th>ID</th>
                    <th>Type</th>
                    <th>Expense Name</th>
                    <th>Linked Account</th>
                    <th>Linked To Complex Expense Named</th>            
                    <th>Price</th>
                    <th>Safety Stock</th>
                    <th>Order QTY</th>
                    <th>Shop Name</th>    
                </tr>
                <c:forEach var = "row" items = "${expensesStructureOutput.rows}">
                    <tr>
                        <td> <c:out value = "${row.ID}"/></td>
                        <td> <c:out value = "${row.TYPE}"/></td>
                        <td> <c:out value = "${row.NAME}"/></td>
                        <td> <c:out value = "${row.ACCOUNT_LINKED}"/></td>
                        <td> <c:out value = "${row.COMPLEX_EXP_NAME_ASSIGNED}"/></td>
                        <td> <c:out value = "${row.PRICE}"/></td>
                        <td> <c:out value = "${row.SAFETY_STOCK}"/></td>
                        <td> <c:out value = "${row.ORDER_QTY}"/></td>
                        <td> <c:out value = "${row.SHOP_NAME}"/></td>
                    </tr>
                </c:forEach>
            </table>  

            <%--                
                        <%  String currentEntityList = (String) request.getAttribute("currentEntityList");
                            if (currentEntityList != null && !currentEntityList.trim().isEmpty()) {
                                out.println(currentEntityList);
                            }
                        %>
            --%>            
        </div>
    </body>
</html>