select T2."TYPE", T2.ID, T1."YEAR", T1.WEEK, T1.ACTUAL_PCS, T1.ACTUAL_CUR
from
(select EXPENSE_ID, "YEAR", cast(substr(WEEK, 3, 2) as int) as WEEK, 
sum(QTY) as ACTUAL_PCS, sum(COST) as ACTUAL_CUR
from ACTUAL_EXPENSES
where EXPENSE_ID = ?
group by EXPENSE_ID, "YEAR", WEEK) T1
left join
EXPENSES_STRUCTURE T2
on T2.ID = T1.EXPENSE_ID
where T1.WEEK >= ?