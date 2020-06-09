
package ejb.expensesStructure;

import ejb.DBConnection.QueryProviderLocal;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
import javax.ejb.EJB;

/**
 *
 * @author SoundlyGifted
 */
public abstract class ExpensesStructureSQLAbstract {
    
    @EJB
    private QueryProviderLocal queryProvider;
    
//    @PostConstruct
//    public void initialize() {
//        String className = this.getClass().getSimpleName();
//        System.out.println("*** " + className + ": initialize() called. ***");
//            try {
//                connection = connectionCheck(connectionProvider.connection());
//            } catch (SQLException sqlex) {
//            System.out.println("*** " + className + ": SQL PreparedStatement "
//                    + " connection establishing failure: "
//                    + sqlex.getMessage() + " ***");                
//            }
//        System.out.println("*** " + className + ": initialize() Connection = " + connection.hashCode());
//    }
    

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
    
//    @PreDestroy
//    public void close() {
//        if (connection != null) {          
//            try {
//                connection.close();
//                connection = null;
//            } catch (SQLException ex) {
//                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
//            }
//        }     
//    }
}
