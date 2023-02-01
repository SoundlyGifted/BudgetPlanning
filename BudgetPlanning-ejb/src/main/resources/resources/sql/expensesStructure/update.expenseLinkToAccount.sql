-- resources.sql.expensesStructure --
-- update.expenseLinkToAccount.sql --
update EXPENSES_STRUCTURE
set ACCOUNT_ID = ?, ACCOUNT_LINKED = ?
where ACCOUNT_ID = ?

