
package com.ejb.expstructure;

/**
 * ExpensesTypes interface contains enumeration of possible Expenses types 
 * pre-specified in the application.
 */
public interface ExpensesTypes {
    
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
