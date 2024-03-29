
package com.ejb.database;

import com.ejb.database.exceptions.GenericDBException;
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
     * @throws com.ejb.database.exceptions.GenericDBException in case if an 
     * error happens while reading SQL query. 
     */
    public String getQuery(String path) throws GenericDBException;
}
