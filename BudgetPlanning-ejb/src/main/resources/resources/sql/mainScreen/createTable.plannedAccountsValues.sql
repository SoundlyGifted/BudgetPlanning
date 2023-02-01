-- resources.sql.mainScreen --
-- createTable.plannedAccountsValues.sql --
create table PLANNED_ACCOUNTS_VALUES
("DATE" date not null,
WEEK varchar(4) not null,
DAY_N int not null,
DAY_C varchar(3) not null,
MONTH_N int not null,
MONTH_C varchar(3) not null,
"YEAR" int not null,
ACCOUNT_ID int not null references ACCOUNTS_STRUCTURE(ID),
ACCOUNT_NAME varchar(255) not null,
PLANNED_REMAINDER_CUR double not null,
PLANNED_INCOME_CUR double not null,
CURPFL varchar(1) not null,
constraint PK_DATE_ACCOUNTNAME primary key ("DATE", ACCOUNT_NAME)
);
