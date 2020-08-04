
package ejb.actualExpenses;

import java.sql.Connection;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface ActualExpensesSQLLocal {

    public boolean executeInsert(Connection connection, String date,
            String expenseName, String expenseTitle, String shopName,
            String price, String qty, String comment);

    public boolean executeUpdate(Connection connection, String idForUpdate,
            String date, String expenseName, String expenseTitle,
            String shopName, String price, String qty, String comment);

    public boolean executeDelete(Connection connection, String id);

    public TreeMap<String, Double> calculateActualExpenses(Connection connection,
            TreeSet<String> timePeriodDates, String planningPeriodsFrequency,
            Integer expenseId);
    
    public boolean setExpenseToDeleted (Connection connection, String id);
    
    public boolean recoverDeletedExpenseId (Connection connection, 
            Integer expenseId, String expenseName);
}
