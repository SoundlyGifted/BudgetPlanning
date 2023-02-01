-- resources.sql.mainScreen --
-- createTable.plannedVariableParams.sql --
create table PLANNED_VARIABLE_PARAMS
("DATE" date not null,
WEEK varchar(4) not null,
DAY_N int not null,
DAY_C varchar(3) not null,
MONTH_N int not null,
MONTH_C varchar(3) not null,
"YEAR" int not null,
EXPENSE_ID int not null references EXPENSES_STRUCTURE(ID),
EXPENSE_NAME varchar(255) not null,
PLANNED_PCS double not null,
PLANNED_CUR double not null,
ACTUAL_PCS double not null,
ACTUAL_CUR double not null,
DIFFERENCE_PCS double not null,
DIFFERENCE_CUR double not null,
CONSUMPTION_PCS double not null,
CONSUMPTION_CUR double not null,
STOCK_PCS double not null,
STOCK_CUR double not null,
REQUIREMENT_PCS double not null,
REQUIREMENT_CUR double not null,
CURPFL varchar(1) not null,
constraint PK_DATE_EXPENSENAME primary key ("DATE", EXPENSE_NAME)
);