-- resources.sql.actualExpenses --
-- select.calculateActualExpenses.byExpenseId.monthly.sql --
select T2."TYPE", T2.ID, T1."YEAR", T1.MONTH_N, T1.ACTUAL_PCS, T1.ACTUAL_CUR
from
(select EXPENSE_ID, "YEAR", MONTH_N, sum(QTY) as ACTUAL_PCS, sum(COST) as ACTUAL_CUR
from ACTUAL_EXPENSES
where EXPENSE_ID = ? and MONTH_N >= ?
group by EXPENSE_ID, "YEAR", MONTH_N) T1
left join
EXPENSES_STRUCTURE T2
on T2.ID = T1.EXPENSE_ID
