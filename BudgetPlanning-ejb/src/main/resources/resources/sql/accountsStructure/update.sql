-- resources.sql.accountsStructure --
-- update.sql --
update ACCOUNTS_STRUCTURE
set "NAME" = ?,
    CURRENT_REMAINDER_CUR = ?
where ID = ?