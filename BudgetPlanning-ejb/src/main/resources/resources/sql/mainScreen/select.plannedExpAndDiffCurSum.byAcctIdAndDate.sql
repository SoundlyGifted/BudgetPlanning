-- resources.sql.mainScreen --
-- select.plannedExpAndDiffCurSum.byAcctIdAndDate.sql --
select T1."DATE", T2.ACCOUNT_ID, 
    sum(T1.PLANNED_CUR) as PLANNED_CUR, 
        sum(T1.DIFFERENCE_CUR) as DIFFERENCE_CUR
from
(select "DATE", EXPENSE_ID, PLANNED_CUR, DIFFERENCE_CUR 
    from PLANNED_VARIABLE_PARAMS) as T1
left join
(select ID, ACCOUNT_ID from EXPENSES_STRUCTURE) as T2
on T1.EXPENSE_ID = T2.ID
where ACCOUNT_ID = ? and "DATE" = ?
group by ACCOUNT_ID, "DATE"
