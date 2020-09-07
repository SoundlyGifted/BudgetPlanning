-- resources.sql.mainScreen --
-- update.plannedCur.sql --
update PLANNED_VARIABLE_PARAMS
set PLANNED_CUR = ?
where EXPENSE_ID = ? and "DATE" = ?

