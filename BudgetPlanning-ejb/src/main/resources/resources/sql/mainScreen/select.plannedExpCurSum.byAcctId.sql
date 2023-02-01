-- resources.sql.mainScreen --
-- select.plannedExpCurSum.byAcctId.sql --
select T3."DATE", sum(T3.PLANNED_CUR) as PLANNED_CUR
from
(select T1.* from
    (select "DATE", EXPENSE_ID, PLANNED_CUR 
        from PLANNED_VARIABLE_PARAMS) as T1
    cross join
    (select distinct "DATE" as "CURRENT_PERIOD_DATE" 
        from PLANNED_VARIABLE_PARAMS
            where CURPFL = 'Y') as T2
    where T1."DATE" >= "CURRENT_PERIOD_DATE") as T3
left join
(select ID, ACCOUNT_ID from EXPENSES_STRUCTURE) as T4
on T3.EXPENSE_ID = T4.ID
where ACCOUNT_ID = ?
group by "DATE"
order by "DATE"
