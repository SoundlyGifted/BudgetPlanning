
package ejb.accountsStructure;

import ejb.calculation.EntityAccount;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface AccountsStructureSQLLocal {
    
    public boolean executeInsert(Connection connection, String name, 
            String currentRemainder);
    
    public boolean executeUpdate(Connection connection, String idForUpdate, 
            String name, String currentRemainder);
    
    public boolean executeDelete(Connection connection, String id);
    
    public ArrayList<EntityAccount> executeSelectAll(Connection connection);
    
    public EntityAccount executeSelectByName(Connection connection, 
            String name);
    
    public EntityAccount executeSelectById(Connection connection, Integer id);
    
    public HashMap<Integer, HashMap<String, Double>> 
        executeSelectAllValues(Connection connection);
        
    public boolean updateCurrentRemainderById(Connection connection, Integer id, 
            Double newCurrentRemainderCur);    
}
