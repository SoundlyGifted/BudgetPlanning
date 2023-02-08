
package com.ejb.database.exceptions;

/**
 * Custom generic database exception thrown when a database connection operation 
 * or an sql-file reading operation throws an exception.
 */
public class GenericDBException extends Exception {
    
    public GenericDBException() { }
    
    public GenericDBException(String message) {
        super(message);
    }
    
    public GenericDBException(String message, Throwable cause) {
        super(message, cause);
    }
}
