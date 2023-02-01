-- resources.sql.actualExpenses --
-- createTable.actualExpenses.sql --
create table ACTUAL_EXPENSES
(ID int not null primary key generated always as identity (start with 1, increment by 1),
"DATE" date not null,
WEEK varchar(4) not null,
DAY_N int not null,
DAY_C varchar(3) not null,
MONTH_N int not null,
MONTH_C varchar(3) not null,
"YEAR" int not null,
EXPENSE_ID int not null references EXPENSES_STRUCTURE(ID),
EXPENSE_NAME varchar(255) not null,
EXPENSE_TITLE varchar(255),
SHOP_NAME varchar(255),
PRICE double not null,
QTY double not null,
COST double not null,
COMMENT varchar(255),
constraint unique_actual_expense unique ("DATE", EXPENSE_NAME, EXPENSE_TITLE)
);

