-- resources.sql.mainScreen --
-- select.accountPlannedVarParams.byid.sql --
select "DATE", PLANNED_REMAINDER_CUR, PLANNED_INCOME_CUR
from PLANNED_ACCOUNTS_VALUES
cross join
(select distinct "DATE" as CURRENT_PERIOD_DATE 
    from PLANNED_ACCOUNTS_VALUES where CURPFL = 'Y') as T
where ACCOUNT_ID = ? and "DATE" >= CURRENT_PERIOD_DATE
order by "DATE"


