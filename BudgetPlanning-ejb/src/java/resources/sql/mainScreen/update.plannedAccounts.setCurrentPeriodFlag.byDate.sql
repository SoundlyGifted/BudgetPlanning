-- resources.sql.mainScreen --
-- update.plannedAccounts.setCurrentPeriodFlag.byDate.sql --
update PLANNED_ACCOUNTS_VALUES set CURPFL = 'Y'
where "DATE" = ?

