-- resources.sql.expensesStructure --
-- createTable.expensesStructure.sql --
create table EXPENSES_STRUCTURE (
ID int not null primary key generated always as identity (start with 1, increment by 1),
"TYPE" varchar(255) not null,
"NAME" varchar(255) not null unique,
ACCOUNT_ID int not null,
ACCOUNT_LINKED varchar(255),
LINKED_TO_COMPLEX_ID int not null,
PRICE double not null,
SAFETY_STOCK_PCS double not null,
SAFETY_STOCK_CUR double not null,
ORDER_QTY_PCS double not null,
ORDER_QTY_CUR double not null,
CURRENT_STOCK_PCS double not null,
CURRENT_STOCK_CUR double not null,
CURRENT_STOCK_WSC_PCS double not null,
CURRENT_STOCK_WSC_CUR double not null
);