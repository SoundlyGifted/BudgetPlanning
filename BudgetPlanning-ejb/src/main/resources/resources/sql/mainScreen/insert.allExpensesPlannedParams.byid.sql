-- resources.sql.mainScreen --
-- insert.allExpensesPlannedParams.byid.sql --
insert into PLANNED_VARIABLE_PARAMS (
"DATE",
WEEK,
DAY_N,
DAY_C,
MONTH_N,
MONTH_C,
"YEAR",
EXPENSE_ID,
EXPENSE_NAME,
PLANNED_PCS,
PLANNED_CUR,
ACTUAL_PCS,
ACTUAL_CUR,
DIFFERENCE_PCS,
DIFFERENCE_CUR,
CONSUMPTION_PCS,
CONSUMPTION_CUR,
STOCK_PCS,
STOCK_CUR,
REQUIREMENT_PCS,
REQUIREMENT_CUR,
CURPFL
)
values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)