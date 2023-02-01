-- resources.sql.expensesStructure --
-- update.clearAccount.sql --
update EXPENSES_STRUCTURE 
set ACCOUNT_ID = 0, ACCOUNT_LINKED = 'NOT SET'
where "NAME" = ?

