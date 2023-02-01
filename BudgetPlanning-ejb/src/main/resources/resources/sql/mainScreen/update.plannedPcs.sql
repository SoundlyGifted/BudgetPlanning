-- resources.sql.mainScreen --
-- update.plannedPcs.sql --
update PLANNED_VARIABLE_PARAMS
set PLANNED_PCS = ?
where EXPENSE_ID = ? and "DATE" = ?
