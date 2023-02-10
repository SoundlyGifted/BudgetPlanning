
package com.ejb.expstructure;

/**
 * ExpensesTypes interface contains enumeration of possible Expenses types 
 * pre-specified in the application.
 */
public interface ExpensesTypes {
    
    // Expense Types supported in the application.
    public static final String SIMPLE_EXPENSES_SUPPORTED_TYPE 
            = ExpenseType.SIMPLE_EXPENSES.getType();
    public static final String GOODS_SUPPORTED_TYPE 
            = ExpenseType.GOODS.getType();
    public static final String COMPLEX_EXPENSES_SUPPORTED_TYPE 
            = ExpenseType.COMPLEX_EXPENSES.getType();       
    
    /**
     * Enumeration of possible Expenses types pre-specified in the application.
     */
    enum ExpenseType {
        
        SIMPLE_EXPENSES("SIMPLE_EXPENSES"),
        COMPLEX_EXPENSES("COMPLEX_EXPENSES"),
        GOODS("GOODS");
        
        private String type;
        
        private ExpenseType(String type){
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
    }
}
