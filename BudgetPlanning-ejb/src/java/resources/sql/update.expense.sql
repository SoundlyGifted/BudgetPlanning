update EXPENSES_STRUCTURE 
set "NAME" = ?,
    ACCOUNT_LINKED = ?,
    LINKED_TO_COMPLEX_ID = ?,
    TITLE = ?,
    PRICE = ?,
    SAFETY_STOCK = ?,
    ORDER_QTY = ?,
    SHOP_NAME = ?
where "NAME" = ? and TITLE = ?

