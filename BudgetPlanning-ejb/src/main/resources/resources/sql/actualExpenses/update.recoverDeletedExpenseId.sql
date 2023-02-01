-- resources.sql.actualExpenses --
-- update.recoverDeletedExpenseId.sql --
update ACTUAL_EXPENSES
set EXPENSE_ID = ?
where EXPENSE_ID = -1 and EXPENSE_NAME = ?

