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
    <sql:query dataSource = "${outputDBConnection}" var = "expensesStructureResultSet">
        select * from EXPENSES_STRUCTURE
        where ID > 0
        order by
        (case
        when LINKED_TO_COMPLEX_ID = 0 then ID
        else LINKED_TO_COMPLEX_ID
        end),
        (case
        when "TYPE" = 'COMPLEX_EXPENSES' then 1
        when "TYPE" = 'SIMPLE_EXPENSES' then 2
        when "TYPE" = 'GOODS' then 3
        else 4
        end),
        NAME
    </sql:query>

    <!-- JSTL sql query to select all records from PLANNED_VARIABLE_PARAMS table -->    
    <sql:query dataSource = "${outputDBConnection}" var = "plannedParamsResultSet">
        select * from PLANNED_VARIABLE_PARAMS cross join
            (select distinct "DATE" as CURRENT_PERIOD_DATE 
                from PLANNED_VARIABLE_PARAMS where CURPFL = 'Y') as T
        where "DATE" >= CURRENT_PERIOD_DATE
        order by DATE
    </sql:query>

    <!-- JSTL sql query to select all records from ACCOUNTS_STRUCTURE table -->
    <sql:query dataSource = "${outputDBConnection}" var = "accountsStructureResultSet">
        select * from ACCOUNTS_STRUCTURE
        where ID > 0
        order by ID
    </sql:query>        
        
    <!-- JSTL sql query to select all records from PLANNED_ACCOUNTS_VALUES table -->    
    <sql:query dataSource = "${outputDBConnection}" var = "plannedAccountsValuesResultSet">
        select * from PLANNED_ACCOUNTS_VALUES cross join
            (select distinct "DATE" as CURRENT_PERIOD_DATE 
                from PLANNED_ACCOUNTS_VALUES where CURPFL = 'Y') as T
        where "DATE" >= CURRENT_PERIOD_DATE
        order by DATE
    </sql:query>
        
    <!-- JSTL sql query to select all distinct timing parameter values from PLANNED_VARIABLE_PARAMS table -->    
    <sql:query dataSource = "${outputDBConnection}" var = "timelineResultSet">
        select distinct DATE, WEEK, DAY_N, DAY_C, MONTH_C, "YEAR", CURPFL 
        from PLANNED_VARIABLE_PARAMS cross join
            (select distinct "DATE" as CURRENT_PERIOD_DATE 
                from PLANNED_VARIABLE_PARAMS where CURPFL = 'Y') as T
        where "DATE" >= CURRENT_PERIOD_DATE
        order by DATE
    </sql:query>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/NavigationBarStyles.css" />
        <link rel="stylesheet" href="css/GeneralStyles.css" />
        <link rel="stylesheet" href="css/PlanningDataTable.css" />
        <title>Budget Planning Application</title>

        <style>
            /***************************************************/
            /***** STYLES FOR COLLAPSIBLE TABLE ELEMENTS *******/
            /***************************************************/
            
            .level1 td:first-child {
                padding-left: 30px;
            }
            .level2 td:first-child {
                padding-left: 45px;
            }

            .collapse .toggle {
                background-image: url(img/toggle-collapse.png);
                background-size: 100%;
                background-repeat: no-repeat;
                cursor: pointer;
            }
            .expand .toggle {
                background-image: url(img/toggle-expand.png);
                background-size: 100%;
                background-repeat: no-repeat;
                cursor: pointer;
            }
            .toggle {
                height: 9px;
                width: 9px;
                display: inline-block;   
            }            
        </style>

        <script src="http://code.jquery.com/jquery-1.9.1.js" 
                type="text/javascript">
        </script>
        
        <script>
            
            // Keeping scroll position after 'input' field is clicked.
            // (preventing browser from scrolling to the top.
            ;(function($){
            $.fn.saveScrollPosition = function () {
                if (sessionStorage) {
                    var tempScrollTop = sessionStorage.getItem("tempScrollTop");
                    if (tempScrollTop) {
                        $(window).scrollTop(tempScrollTop);
                        sessionStorage.removeItem("tempScrollTop");
                    }
                    $(this).click(function(e) {
                        sessionStorage.setItem("tempScrollTop", $(window).scrollTop());
                    });
                    return true;
                }
                return false;
            };

            $(document).ready(function () {
                $('input').saveScrollPosition();
            });

            }(jQuery));
        
            // This code is to keep the same collapse/expand status of each 
            // Complex Expense on the page after the page is refreshed.
            // The statuses are kept in a separate sessionStorage attributes 
            // named based on the Complex Expense DB id value.
            $(document).ready(function () {
                $('.toggle').each(function (ind, val) {
                    // Gets all <tr>'s  of greater depth
                    // below element in the table
                    var findChildren = function (tr) {
                        var depth = tr.data('depth');
                        return tr.nextUntil($('tr').filter(function () {
                            return $(this).data('depth') <= depth;
                        })).not(".uncollapsible");
                    };

                    var el = $(this);
                    var tr = el.closest('tr'); // Get <tr> parent of toggle button
                    var id = tr.attr('id');
                    
                    // Find collapse status value from the sessionStorage 
                    // attributes and assign to the 'collapsibleStatus' variable.
                    var collapsibleStatus;
                    $.each(sessionStorage, function (key, value) {
                        if (key.indexOf("collapsibleStatus_ID") >= 0 && key.replace("collapsibleStatus_ID", "") === id) {
                            collapsibleStatus = value;
                        }
                    });

                    var children = findChildren(tr);

                    // Switch the corresponding Complex Expense <tr> class
                    // (whichever 'collapse' or 'expand' it is) to the status
                    // based on the value of 'collapsibleStatus' variable.
                    if (collapsibleStatus !== null) {
                        if (collapsibleStatus === "collapsed") {
                            tr.removeClass('collapse').addClass('expand');
                            children.hide();
                        } else {
                            tr.removeClass('expand').addClass('collapse');
                            children.show();
                        }
                    }
                });
            });

            $(function () {
                $('#planningTable').on('click', '.toggle', function () {
                    // Gets all <tr>'s  of greater depth
                    // below element in the table
                    var findChildren = function (tr) {
                        var depth = tr.data('depth');
                        return tr.nextUntil($('tr').filter(function () {
                            return $(this).data('depth') <= depth;
                        })).not(".uncollapsible");
                    };

                    var el = $(this); // getting element with ".toggle" class. This will be a Complex Expense <span> element.
                    var tr = el.closest('tr'); // Get <tr> parent of toggle button.
                    var children = findChildren(tr);

                    // Remove already collapsed nodes from children so that we don't
                    // make them visible. 
                    // (Confused? Remove this code and close Item 2, close Item 1 
                    // then open Item 1 again, then you will understand)
                    var subnodes = children.filter('.expand');
                    subnodes.each(function () {
                        var subnode = $(this);
                        var subnodeChildren = findChildren(subnode);
                        children = children.not(subnodeChildren);
                    });

                    // Change icon and hide/show children
                    // Store Complex Expense <tr> status in a separate 
                    // sessionStorage attributes named based on the Complex 
                    // Expense DB id value.
                    if (tr.hasClass('collapse')) {
                        tr.removeClass('collapse').addClass('expand');
                        children.hide();
                        sessionStorage.setItem("collapsibleStatus_ID" + tr.attr('id'), "collapsed");
                    } else {
                        tr.removeClass('expand').addClass('collapse');
                        children.show();
                        sessionStorage.setItem("collapsibleStatus_ID" + tr.attr('id'), "expanded");
                    }
                    return children;
                });
            });  
        </script>
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

            <form action="LogServlet">
                <% String uri = request.getRequestURI();
                   String pageName = uri.substring(uri.lastIndexOf("/")+1);
                %>
                <input type="hidden" name="pageName" value=<%= pageName %>>
                <input type="submit" class="button" value="Refresh" name="refresh"/>
                <input type="submit" class="button" value="Clear Log" name="clearLog"/>
            </form>            
        </div>

        <div class="outputBlock1">
            <form action="MainScreenServlet">
            <table class="planningDataTable" id="planningTable">

                <caption>
                    Planning Data
                </caption>

                <!-- Table Header -->
                <tr><th rowspan="6" colspan="8" class="sticky-header1" style="top: 50px">Timeline</th>
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
                                <c:when test="${row.CURPFL == 'Y'}">
                                    <th class="sticky-header1" style="top: 180px; background-color: #005a87; color: white;">Current</th>
                                </c:when>
                            <c:otherwise><th class="sticky-header1" style="top: 180px; color: #005a87;">Planned</th></c:otherwise>
                            </c:choose>
                        </c:forEach>
                </tr>
                <tr>
                    <th rowspan="2" class="sticky-header2" style="top: 206px; min-width:80px">Expense Name</th>
                    <th rowspan="2" class="sticky-header2" style="top: 206px; min-width:120px">Expense Type</th>
                    <th colspan="4" class="sticky-header2" style="top: 206px; text-align: center;">Parameter</th>
                    <th rowspan="2" class="sticky-header2" style="top: 206px; min-width:135px">Data</th>
                    <th rowspan="2" class="sticky-header2" style="top: 206px; min-width:20px">UM</th>
                    <th rowspan="2" class="sticky-header2" style="top: 206px; min-width:50px"></th>
                        <c:forEach var = "row" items = "${timelineResultSet.rows}">
                        <th rowspan="2" class="sticky-header2" style="top: 206px"></th>
                        </c:forEach>                    
                </tr>
                <tr>
                    <th class="sticky-header2" style="top: 232.5px; min-width:120px">Name</th>
                    <th class="sticky-header2" style="top: 232.5px; min-width:20px">UM</th>
                    <th class="sticky-header2" style="top: 232.5px; min-width:100px">Value</th>                    
                    <th class="sticky-header2" style="top: 232.5px; min-width:50px">Adjust</th>
                </tr>

                <!-- Table Data -->
                <c:forEach var="row" items="${expensesStructureResultSet.rows}">
                    <c:choose>
                        <c:when test="${row.TYPE == 'GOODS'}">
                            <!-- Defining <tr>-level variable. -->
                            <c:choose>
                                <c:when test="${row.LINKED_TO_COMPLEX_ID == 0}">
                                    <c:set var="dataDepth" value="0"/>
                                    <tr data-depth="${dataDepth}" class="collapse level0" style="border-top: 3px solid #00aaff">
                                        <td><c:out value = "${row.NAME}"/></td>
                                        <td><c:out value = "${row.TYPE}"/></td>
                                        <td>Price</td>
                                        <td>CUR</td>
                                        <td></td>
                                        <td><c:out value = "${row.PRICE}"/></td>
                                        <td>Consumption</td>
                                        <td>PCS</td>
                                        <td><input type="submit" class="smallButton" value="Update" name="update_${row.ID}_CONSUMPTION_PCS"/></td>
                                            <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                                <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                                <td><c:out value="${row2.CONSUMPTION_PCS}"/></td>
                                            </c:if>
                                        </c:forEach>                                
                                    </tr>                                
                                </c:when>
                                <c:otherwise>
                                    <c:set var="dataDepth" value="1"/>
                                    <tr data-depth="${dataDepth}" class="collapse level1" style="border-top: 3px solid #e8e8e8">
                                        <td><c:out value = "${row.NAME}"/></td>
                                        <td><c:out value = "${row.TYPE}"/></td>
                                        <td>Price</td>
                                        <td>CUR</td>
                                        <td><c:out value = "${row.PRICE}"/></td>
                                        <td></td>                                        
                                        
                                        <td>Consumption</td>
                                        <td>PCS</td>
                                        <!-- CONSUMPTION_PCS (Consumption PCS) variable parameter that can be changed by user. -->
                                        <c:choose>
                                            <c:when test="${row.ID == requestScope.rowSelectedForConsumptionPcsUpdate}">
                                                <td><input type="submit" class="smallButton" value="Submit" name="submitUpdate_CONSUMPTION_PCS_${row.ID}"/></td>
                                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                                        <td><input type="text" class="inputTextBox" style="font-size: 10px; padding: 2px 1px" value="${row2.CONSUMPTION_PCS}" size="5" name="updateConsumptionPcs_${row2.DATE}" placeholder="..."/></td>
                                                    </c:if>
                                                </c:forEach>                                         
                                            </c:when>
                                            <c:otherwise>
                                                <td><input type="submit" class="smallButton" value="Update" name="update_CONSUMPTION_PCS_${row.ID}"/></td>
                                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                                        <td><c:out value="${row2.CONSUMPTION_PCS}"/></td>
                                                    </c:if>
                                                </c:forEach>                                        
                                            </c:otherwise>
                                        </c:choose>
                                                        
                                    </tr>                                
                                </c:otherwise>
                            </c:choose>

                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td style="border-bottom: 2px solid #e8e8e8;">Consumption</td>
                                <td style="border-bottom: 2px solid #e8e8e8;">CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td style="border-bottom: 2px solid #e8e8e8;"><c:out value="${row2.CONSUMPTION_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                   
                            </tr> 
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                <td>Safety Stock</td>
                                <td>PCS</td>
                                <td><c:out value = "${row.SAFETY_STOCK_PCS}"/></td>
                                <td></td>                                
                                <td>Stock Plan</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.STOCK_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                   
                            </tr> 
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                <td style="border-bottom: 2px solid #e8e8e8;">Safety Stock</td>
                                <td style="border-bottom: 2px solid #e8e8e8;">CUR</td>
                                <td style="border-bottom: 2px solid #e8e8e8;"><c:out value = "${row.SAFETY_STOCK_CUR}"/></td>
                                <td></td>                                
                                <td style="border-bottom: 2px solid #e8e8e8;">Stock Plan</td>
                                <td style="border-bottom: 2px solid #e8e8e8;">CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td style="border-bottom: 2px solid #e8e8e8;"><c:out value="${row2.STOCK_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr>
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                <td>Order Qty</td>
                                <td>PCS</td>
                                <td><c:out value = "${row.ORDER_QTY_PCS}"/></td>
                                <td></td>                                
                                <td>Requirement</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.REQUIREMENT_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                <td style="border-bottom: 2px solid #e8e8e8;">Order Qty</td>
                                <td style="border-bottom: 2px solid #e8e8e8;">CUR</td>
                                <td style="border-bottom: 2px solid #e8e8e8;"><c:out value = "${row.ORDER_QTY_CUR}"/></td>
                                <td></td>                                
                                <td style="border-bottom: 2px solid #e8e8e8;">Requirement</td>
                                <td style="border-bottom: 2px solid #e8e8e8;">CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td style="border-bottom: 2px solid #e8e8e8;"><c:out value="${row2.REQUIREMENT_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                
                                <td>Stock Current</td>
                                <td>PCS</td>
                                <!-- CURRENT_STOCK_PCS (Current Stock PCS) fixed parameter that can be changed by user. -->
                                <c:choose>
                                    <c:when test="${row.ID == requestScope.rowSelectedForCurrentStockUpdate}">
                                        <td><input type="text" class="inputTextBox" style="font-size: 10px; padding: 2px 1px" value="${row.CURRENT_STOCK_PCS}" size="5" name="updateCurrentStock" placeholder="..."/></td>
                                        <td><input type="submit" class="smallButton" value="Submit" name="submitUpdate_CURRENT_STOCK_PCS_${row.ID}"/></td>                                        
                                    </c:when>
                                    <c:otherwise>
                                        <td><c:out value = "${row.CURRENT_STOCK_PCS}"/></td>
                                        <td><input type="submit" class="smallButton" value="Adjust" name="update_CURRENT_STOCK_PCS_${row.ID}"/></td>                                        
                                    </c:otherwise>
                                </c:choose>
                                        
                                <td>Expenses Plan</td>
                                <td>PCS</td>
                                <!-- PLANNED_PCS (Expenses Plan PCS) variable parameter that can be changed by user. -->
                                <c:choose>
                                    <c:when test="${row.ID == requestScope.rowSelectedForExpensesPlanPcsUpdate}">
                                        <td><input type="submit" class="smallButton" value="Submit" name="submitUpdate_PLANNED_PCS_${row.ID}"/></td>
                                        <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                            <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                                <td><input type="text" class="inputTextBox" style="font-size: 10px; padding: 2px 1px" value="${row2.PLANNED_PCS}" size="5" name="updateExpensesPlanPcs_${row2.DATE}" placeholder="..."/></td>
                                            </c:if>
                                        </c:forEach>                                         
                                    </c:when>
                                    <c:otherwise>
                                        <td><input type="submit" class="smallButton" value="Update" name="update_PLANNED_PCS_${row.ID}"/></td>
                                        <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                            <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                                <td><c:out value="${row2.PLANNED_PCS}"/></td>
                                            </c:if>
                                        </c:forEach>                                        
                                    </c:otherwise>
                                </c:choose>
    
                            </tr> 
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                <td style="border-bottom: 2px solid #e8e8e8;">Stock Current</td>
                                <td style="border-bottom: 2px solid #e8e8e8;">CUR</td>
                                <td style="border-bottom: 2px solid #e8e8e8;"><c:out value = "${row.CURRENT_STOCK_CUR}"/></td>
                                <td></td>                                
                                <td style="border-bottom: 2px solid #e8e8e8;">Expenses Plan</td>
                                <td style="border-bottom: 2px solid #e8e8e8;">CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td style="border-bottom: 2px solid #e8e8e8;"><c:out value="${row2.PLANNED_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                <td>Stock Current WSC</td>
                                <td>PCS</td>
                                <td><c:out value = "${row.CURRENT_STOCK_WSC_PCS}"/></td>
                                <td></td>                                
                                <td>Expenses Actual</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.ACTUAL_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                <td style="border-bottom: 2px solid #e8e8e8;">Stock Current WSC</td>
                                <td style="border-bottom: 2px solid #e8e8e8;">CUR</td>
                                <td style="border-bottom: 2px solid #e8e8e8;"><c:out value = "${row.CURRENT_STOCK_WSC_CUR}"/></td>
                                <td></td>                                
                                <td style="border-bottom: 2px solid #e8e8e8;">Expenses Actual</td>
                                <td style="border-bottom: 2px solid #e8e8e8;">CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td style="border-bottom: 2px solid #e8e8e8;"><c:out value="${row2.ACTUAL_CUR}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr>
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
                                <td></td>
                                <td>Linked Account</td>
                                <td>Text</td>
                                <td><c:out value = "${row.ACCOUNT_LINKED}"/></td>
                                <td></td>                                
                                <td>Expenses Diff (Act - Pl)</td>
                                <td>PCS</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.DIFFERENCE_PCS}"/></td>
                                    </c:if>
                                </c:forEach>                                 
                            </tr> 
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
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
                        <c:when test="${row.TYPE == 'SIMPLE_EXPENSES'}">
                            <!-- Defining <tr>-level variable. -->
                            <c:choose>
                                <c:when test="${row.LINKED_TO_COMPLEX_ID == 0}">
                                    <c:set var="dataDepth" value="0"/>
                                    <tr data-depth="${dataDepth}" class="collapse level0" style="border-top: 3px solid #00aaff">
                                        <td><c:out value = "${row.NAME}"/></td>
                                        <td><c:out value = "${row.TYPE}"/></td>
                                        <td>Linked Account</td>
                                        <td>Text</td>
                                        <td><c:out value = "${row.ACCOUNT_LINKED}"/></td>
                                        <td></td>                                        
                                        
                                        <td>Expenses Plan</td>
                                        <td>CUR</td>
                                        <!-- PLANNED_CUR (Expenses Plan CUR) variable parameter that can be changed by user. -->
                                        <c:choose>
                                            <c:when test="${row.ID == requestScope.rowSelectedForExpensesPlanCurUpdate}">
                                                <td><input type="submit" class="smallButton" value="Submit" name="submitUpdate_PLANNED_CUR_${row.ID}"/></td>
                                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                                        <td><input type="text" class="inputTextBox" style="font-size: 10px; padding: 2px 1px" value="${row2.PLANNED_CUR}" size="5" name="updateExpensesPlanCur_${row2.DATE}" placeholder="..."/></td>
                                                    </c:if>
                                                </c:forEach>                                         
                                            </c:when>
                                            <c:otherwise>
                                                <td><input type="submit" class="smallButton" value="Update" name="update_PLANNED_CUR_${row.ID}"/></td>
                                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                                        <td><c:out value="${row2.PLANNED_CUR}"/></td>
                                                    </c:if>
                                                </c:forEach>                                        
                                            </c:otherwise>
                                        </c:choose>
                                                
                                    </tr>                                
                                </c:when>
                                <c:otherwise>
                                    <c:set var="dataDepth" value="1"/>
                                    <tr data-depth="${dataDepth}" class="collapse level1" style="border-top: 3px solid #e8e8e8">
                                        <td><c:out value = "${row.NAME}"/></td>
                                        <td><c:out value = "${row.TYPE}"/></td>
                                        <td>Linked Account</td>
                                        <td>Text</td>
                                        <td><c:out value = "${row.ACCOUNT_LINKED}"/></td>
                                        <td></td>                                        
                                        
                                        <td>Expenses Plan</td>
                                        <td>CUR</td>
                                        <!-- PLANNED_CUR (Expenses Plan CUR) variable parameter that can be changed by user. -->
                                        <c:choose>
                                            <c:when test="${row.ID == requestScope.rowSelectedForExpensesPlanCurUpdate}">
                                                <td><input type="submit" class="smallButton" value="Submit" name="submitUpdate_PLANNED_CUR_${row.ID}"/></td>
                                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                                        <td><input type="text" class="inputTextBox" style="font-size: 10px; padding: 2px 1px" value="${row2.PLANNED_CUR}" size="5" name="updateExpensesPlanCur_${row2.DATE}" placeholder="..."/></td>
                                                    </c:if>
                                                </c:forEach>                                         
                                            </c:when>
                                            <c:otherwise>
                                                <td><input type="submit" class="smallButton" value="Update" name="update_PLANNED_CUR_${row.ID}"/></td>
                                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                                        <td><c:out value="${row2.PLANNED_CUR}"/></td>
                                                    </c:if>
                                                </c:forEach>                                        
                                            </c:otherwise>
                                        </c:choose>                                        
     
                                    </tr>                                
                                </c:otherwise>
                            </c:choose>                            

                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
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
                            <tr data-depth="${dataDepth}" class="collapse">
                                <td></td>
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
                            <!-- Defining <tr>-level variable. -->
                            <tr id="${row.ID}" data-depth="0" class="collapse level0" style="border-top: 3px solid #00aaff">
                                <td><span style="padding: 1px" class="toggle collapse"></span> <c:out value = "${row.NAME}"/></td>                            
                                <td><c:out value = "${row.TYPE}"/></td>
                                <td>Linked Account</td>
                                <td>Text</td>
                                <td><c:out value = "${row.ACCOUNT_LINKED}"/></td>
                                <td></td>                                
                                <td>Expenses Plan</td>
                                <td>CUR</td>
                                <td></td>
                                <c:forEach var="row2" items="${plannedParamsResultSet.rows}">
                                    <c:if test="${row.NAME == row2.EXPENSE_NAME}">
                                        <td><c:out value="${row2.PLANNED_CUR}"/></td>
                                    </c:if>
                                </c:forEach>
                            </tr>
                            <tr class="uncollapsible">
                                <td></td>
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
                            <tr class="uncollapsible">
                                <td></td>
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
                            
            <!-- ACCOUNTS PART -->
            <!-- Blank row to separate two parts of the table -->
            <tr data-depth="0" style="border-top: 3px solid #00aaff">
                <td></td>                            
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>                                
                <td></td>
                <td></td>
                <td></td>
                <c:forEach var="row" items="${timelineResultSet.rows}">
                    <td></td>
                </c:forEach>
            </tr>
            <!-- Blank row to separate two parts of the table -->
            <tr style="background-color: #ddcfff">
                <th colspan="2" style="text-align: left;">Account Name</th>                            
                <th style="text-align: left;">Parameter Name</th>
                <th style="text-align: left;">UM</th>
                <th style="text-align: left;">Value</th>
                <th></th>                               
                <th>Data</th>
                <th>UM</th>
                <th></th>
                <c:forEach var="row" items="${timelineResultSet.rows}">
                    <th></th>
                </c:forEach>
            </tr>
            <c:forEach var="row" items="${accountsStructureResultSet.rows}">
                <tr style="border-top: 3px solid #00aaff">
                    <td colspan="2" style="text-align: left;"><c:out value="${row.NAME}"/></td>                            
                    <td style="text-align: left;">Current Remainder</td>
                    <td style="text-align: left;">CUR</td>
                    <td style="text-align: left;"><c:out value="${row.CURRENT_REMAINDER_CUR}"/></td>
                    <td></td>                                
                    <td>Remainder Plan</td>
                    <td>CUR</td>
                    <td></td>
                    <c:forEach var="row2" items="${plannedAccountsValuesResultSet.rows}">
                        <c:if test="${row.ID == row2.ACCOUNT_ID}">
                            <td><c:out value="${row2.PLANNED_REMAINDER_CUR}"/></td>
                        </c:if>
                    </c:forEach>
                </tr>
                <tr>
                    <td colspan="2"></td>                            
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>                                
                    <td>Income Plan</td>
                    <td>CUR</td>
                    <c:choose>
                        <c:when test="${row.ID == requestScope.rowSelectedForIncomeCurUpdate}">
                            <td><input type="submit" class="smallButton" value="Submit" name="submitUpdate_PLANNED_INCOME_CUR_${row.ID}"/></td>
                            <c:forEach var="row2" items="${plannedAccountsValuesResultSet.rows}">
                                <c:if test="${row.ID == row2.ACCOUNT_ID}">
                                    <td><input type="text" class="inputTextBox" style="font-size: 10px; padding: 2px 1px" value="${row2.PLANNED_INCOME_CUR}" size="5" name="updateIncomeCur_${row2.DATE}" placeholder="..."/></td>
                                </c:if>
                            </c:forEach>                                         
                        </c:when>
                        <c:otherwise>
                            <td><input type="submit" class="smallButton" value="Update" name="update_PLANNED_INCOME_CUR_${row.ID}"/></td>
                            <c:forEach var="row2" items="${plannedAccountsValuesResultSet.rows}">
                                <c:if test="${row.ID == row2.ACCOUNT_ID}">
                                    <td><c:out value="${row2.PLANNED_INCOME_CUR}"/></td>
                                </c:if>
                            </c:forEach>                                        
                        </c:otherwise>
                    </c:choose>   
                </tr>
            </c:forEach>            

            </table>
            </form>
   
            <%--
            <%  String currentEntityExpenseList = (String) request.getAttribute("currentEntityExpenseList");
                    if (currentEntityExpenseList != null && !currentEntityExpenseList.trim().isEmpty()) {
                        out.println(currentEntityExpenseList);
                    }
            %>

            
            <%  String currentEntityAccountList = (String) request.getAttribute("currentEntityAccountList");
                    if (currentEntityAccountList != null && !currentEntityAccountList.trim().isEmpty()) {
                        out.println(currentEntityAccountList);
                    }
            %>            
            --%>            
            
        </div>

    </body>
</html>
