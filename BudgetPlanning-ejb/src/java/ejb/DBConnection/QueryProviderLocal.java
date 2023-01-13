
package ejb.DBConnection;

import java.io.IOException;
import jakarta.ejb.Local;

/**
 * EJB QueryProvider Local interface contains method that reads SQL query 
 * from an sql-file.
 */
@Local
public interface QueryProviderLocal {
    
    /**
     * Reads SQL query from an sql-file.
     * 
     * @param path path to the sql-file
     * @return SQL query (String).
     * @throws IOException 
     */
    public String getQuery(String path) throws IOException;
}
