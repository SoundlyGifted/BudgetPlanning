-- resources.sql.actualExpenses --
-- select.calculateActualExpenses.byExpenseId.daily.sql --
select T2."TYPE", T2.ID, T1."DATE", T1.ACTUAL_PCS, T1.ACTUAL_CUR
from
(select EXPENSE_ID, "DATE", QTY as ACTUAL_PCS, COST as ACTUAL_CUR
from ACTUAL_EXPENSES
where EXPENSE_ID = ?) T1
left join
EXPENSES_STRUCTURE T2
on T2.ID = T1.EXPENSE_ID
where "DATE" >= ?
