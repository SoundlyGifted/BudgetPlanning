
package ejb.common;

import ejb.DBConnection.QueryProviderLocal;
import ejb.expensesStructure.ExpensesTypes;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.ejb.EJB;

/**
 *
 * @author SoundlyGifted
 */
public abstract class SQLAbstract {
    
    @EJB
    private QueryProviderLocal queryProvider;
    
    public PreparedStatement createPreparedStatement(Connection connection,
            String path)
            throws SQLException, IOException {
        String className = this.getClass().getSimpleName();
        System.out.println("*** " + className + ": createPreparedStatement() "
                + "current connection = " + connection.hashCode());
        String query = queryProvider.getQuery(path);
        return connection.prepareStatement(query);
    }
    
    public Integer stringToInt (String stringVal) {
        try {
            return Integer.parseInt(stringVal);          
        } catch (NumberFormatException ex) {
            System.out.println("Value '" + stringVal + "' cannot be "
                    + "converted to Integer");
        }
        return null;
    }  

    public Double stringToDouble (String stringVal) {
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

    public static double round(double value, int dplaces) {
        if (dplaces < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(dplaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public boolean inputCheckType(String type) {
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
    
    public boolean inputCheckNullBlank(String param) {
        return !(param == null || param.trim().isEmpty());
    }
    
    public boolean inputCheckLength(String param) {
        if (param != null) {
            return !(param.length() > 255);
        }
        return true;
    }
    
}
