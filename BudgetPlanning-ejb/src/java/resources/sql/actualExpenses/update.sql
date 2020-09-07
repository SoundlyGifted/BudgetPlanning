-- resources.sql.actualExpenses --
-- update.sql --
update ACTUAL_EXPENSES 
set "DATE" = ?,
    WEEK = ?,
    DAY_N = ?,
    DAY_C = ?,
    MONTH_N = ?,
    MONTH_C = ?,
    "YEAR" = ?,
    EXPENSE_ID = ?,
    EXPENSE_NAME = ?,
    EXPENSE_TITLE = ?,
    SHOP_NAME = ?,
    PRICE = ?,
    QTY = ?,
    COST = ?,
    COMMENT = ?
where ID = ?