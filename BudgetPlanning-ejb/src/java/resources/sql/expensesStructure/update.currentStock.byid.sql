-- resources.sql.expensesStructure --
-- update.currentStock.byid.sql --
update EXPENSES_STRUCTURE
set CURRENT_STOCK_PCS = ?,
CURRENT_STOCK_CUR = ?,
CURRENT_STOCK_WSC_PCS = ?,
CURRENT_STOCK_WSC_CUR = ?
where ID = ?

