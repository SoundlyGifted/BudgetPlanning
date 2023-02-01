
package com.ejb.calculation;

import java.util.ArrayList;

/**
 * EntityExpenseList class is used to hold collection of EntityExpense objects.
 */
public class EntityExpenseList {
    
    private EntityExpenseList(){}
    
    static ArrayList<EntityExpense> expenseList;

    /**
     * Returns current collection of EntityExpense objects that is contained
     * within EntityExpenseList class.
     * 
     * @return collection of EntityExpense objects of the EntityExpenseList 
     * class.
     */
    static ArrayList<EntityExpense> getEntityExpenseList() {
        if (expenseList == null) {
            expenseList = new ArrayList<>();
        }
        return expenseList;
    }

    /**
     * Sets collection of EntityExpense objects of the EntityExpenseList class
     * based on the given collection of EntityExpense objects.
     * 
     * @param expenseList given collection of EntityExpense objects.
     */
    static void setEntityExpenseList(ArrayList<EntityExpense> expenseList) {
        EntityExpenseList.expenseList = expenseList;
    }
    
    /**
     * Releases EntityExpenseList collection and sets link to the collection
     * to null.
     */
    static void removeEntityExpenseList(){
        if (expenseList != null) {
            EntityExpenseList.expenseList.clear();
            EntityExpenseList.expenseList = null;
        }
    }
}
