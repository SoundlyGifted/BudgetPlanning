
package ejb.expensesStructure;

/**
 *
 * @author SoundlyGifted
 */
public interface ExpensesTypes {
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
