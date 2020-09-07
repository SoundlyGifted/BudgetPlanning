-- resources.sql.mainScreen --
-- update.consumptionPcs.sql --
update PLANNED_VARIABLE_PARAMS
set CONSUMPTION_PCS = ?
where EXPENSE_ID = ? and "DATE" = ?

