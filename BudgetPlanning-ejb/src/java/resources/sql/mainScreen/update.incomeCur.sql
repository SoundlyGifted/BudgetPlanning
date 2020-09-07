-- resources.sql.mainScreen --
-- update.incomeCur.sql --
update PLANNED_ACCOUNTS_VALUES
set PLANNED_INCOME_CUR = ?
where ACCOUNT_ID = ? and "DATE" = ?