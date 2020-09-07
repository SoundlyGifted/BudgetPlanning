-- resources.sql.actualExpenses --
-- update.setExpenseToDeleted.sql --
update ACTUAL_EXPENSES
set EXPENSE_ID = -1
where EXPENSE_ID = ?

