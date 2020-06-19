update EXPENSES_STRUCTURE 
set "NAME" = ?,
    ACCOUNT_LINKED = ?,
    LINKED_TO_COMPLEX_ID = ?,
    PRICE = ?,
    SAFETY_STOCK_PCS = ?,
    SAFETY_STOCK_CUR = ?,
    ORDER_QTY_PCS = ?,
    ORDER_QTY_CUR = ?
where "NAME" = ?

