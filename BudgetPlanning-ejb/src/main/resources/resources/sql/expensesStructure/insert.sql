-- resources.sql.expensesStructure --
-- insert.sql --
insert into EXPENSES_STRUCTURE (
"TYPE", 
"NAME", 
ACCOUNT_ID, 
ACCOUNT_LINKED, 
LINKED_TO_COMPLEX_ID, 
PRICE, 
SAFETY_STOCK_PCS, 
SAFETY_STOCK_CUR,
ORDER_QTY_PCS,
ORDER_QTY_CUR, 
CURRENT_STOCK_PCS, 
CURRENT_STOCK_CUR, 
CURRENT_STOCK_WSC_PCS,
CURRENT_STOCK_WSC_CUR)
values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)