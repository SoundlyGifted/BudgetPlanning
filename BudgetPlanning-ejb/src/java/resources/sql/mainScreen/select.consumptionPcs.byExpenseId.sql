select T1.*, T2."TYPE"
from
(select EXPENSE_ID, "DATE", CONSUMPTION_PCS
from PLANNED_VARIABLE_PARAMS
cross join
(select distinct "DATE" as CURRENT_PERIOD_DATE 
    from PLANNED_ACCOUNTS_VALUES where CURPFL = 'Y') as T
where EXPENSE_ID = ? and "DATE" >= CURRENT_PERIOD_DATE) T1
left join
EXPENSES_STRUCTURE T2
on T1.EXPENSE_ID = T2.ID
order by T1."DATE"