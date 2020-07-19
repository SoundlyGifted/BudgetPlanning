
package ejb.common;

import ejb.expensesStructure.ExpensesTypes;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Abstract class to contain common Methods that are used in the EJB module.
 * 
 * @author SoundlyGifted
 */
public abstract class EjbCommonMethods {

    /**
     * Converts String value to Integer value.
     * 
     * @param stringVal given String value to convert to Integer.
     * @return Integer value if conversion of given String value is possible 
     *         and null otherwise.
     */
    public Integer stringToInt(String stringVal) {
        if (!inputCheckNullBlank(stringVal)) {
            return null;
        }
        try {
            return Integer.parseInt(stringVal);
        } catch (NumberFormatException ex) {
            System.out.println("Value '" + stringVal + "' cannot be "
                    + "converted to Integer");
        }
        return null;
    }

    /**
     * Converts String value to Double value.
     * 
     * @param stringVal given String value to convert to Double.
     * @return Double value if conversion of given String value is possible,
     *         zero if given String is null or blank
     *         and null otherwise. 
     */
    public Double stringToDouble(String stringVal) {
        if (!inputCheckNullBlank(stringVal)) {
            return (double) 0;
        }
        stringVal = stringVal.replaceAll(",", ".");
        try {
            Double doubleValue = Double.parseDouble(stringVal);
            return round(doubleValue, 2);
        } catch (NumberFormatException ex) {
            System.out.println("Value '" + stringVal + "' cannot be "
                    + "converted to Double");
        }
        return null;
    }

    /**
     * Rounds double value by given number of decimal places.
     * 
     * @param value value to be rounded.
     * @param dplaces desired number of decimal places.
     * @return Double value rounded by given number of decimal places. Negative
     *         number of decimal places given will perform rounding by zero
     *         decimal places.
     */
    public double round(double value, int dplaces) {
        if (dplaces < 0) {
            dplaces = 0;
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(dplaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Checks whether the given String supposed to represent an Expense Category
     * type is valid and the value is one of the types pre-specified in the
     * application.
     * 
     * @param type String Expense type to be checked.
     * @return true in case if the type is one of the pre-specified types,
     *         false otherwise.
     */
    public final boolean inputCheckType(String type) {
        if (type == null || type.trim().isEmpty() || type.length() > 255) {
            return false;
        } else {
            /* Check if input type is one of the types in the allowed list. */
            boolean typeMatch = false;
            ExpensesTypes.ExpenseType[] expensesTypes = ExpensesTypes.ExpenseType.values();
            for (ExpensesTypes.ExpenseType t : expensesTypes) {
                if (t.getType().equals(type)) {
                    typeMatch = true;
                }
            }
            if (!typeMatch) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the given String is null or blank.
     * 
     * @param param String to be checked.
     * @return true if given String is not null and not blank, false otherwise.
     */
    public boolean inputCheckNullBlank(String param) {
        return !(param == null || param.trim().isEmpty());
    }

    /**
     * Checks whether the given String has length more than 255 symbols.
     * 
     * @param param String to be checked.
     * @return true if the length of the given String does not exceed 255
     *         symbols, false otherwise.
     */
    public boolean inputCheckLength(String param) {
        if (param != null) {
            return !(param.length() > 255);
        }
        return true;
    }
}
