-- resources.sql.accountsStructure --
-- createTable.accountsStructure.sql --
create table ACCOUNTS_STRUCTURE (
ID int not null primary key generated always as identity (start with 0, increment by 1),
"NAME" varchar(255) not null unique,
CURRENT_REMAINDER_CUR double not null
);

alter table EXPENSES_STRUCTURE add 
constraint fk_account_id_idx foreign key(ACCOUNT_ID) references ACCOUNTS_STRUCTURE(ID);
create unique index fk_account_id_idx on EXPENSES_STRUCTURE(ACCOUNT_ID);

-- prespecified default value --
-- first record in the table will get ID = 0 (prespecified ID for "NOT SET" account) --
insert into ACCOUNTS_STRUCTURE ("NAME", CURRENT_REMAINDER_CUR)
values
('NOT SET', 0); 