
package com.ejb.common.exceptions;

/**
 * Custom generic database operation exception thrown when a database 
 * operation related exception is thrown.
 */
public class GenericDBOperationException extends Exception {
    
    public GenericDBOperationException() { }
    
    public GenericDBOperationException(String message) {
        super(message);
    }
    public GenericDBOperationException(String message, Throwable cause) {
        super(message, cause);
    }    
}
