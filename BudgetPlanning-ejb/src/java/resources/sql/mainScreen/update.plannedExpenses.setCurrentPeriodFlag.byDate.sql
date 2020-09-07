-- resources.sql.mainScreen --
-- update.plannedExpenses.setCurrentPeriodFlag.byDate.sql --
update PLANNED_VARIABLE_PARAMS set CURPFL = 'Y'
where "DATE" = ?