-- resources.sql.expensesStructure --
-- update.clearComplexExpLink.sql --
update EXPENSES_STRUCTURE 
set LINKED_TO_COMPLEX_ID = 0
where "NAME" = ?

