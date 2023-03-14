package com.ejb.common;

import jakarta.ejb.Local;
import java.util.logging.Logger;

/**
 * This Interface describes methods to get Logger for the application debug 
 * purpose.
 */
@Local
public interface LoggerProviderLocal {
    
    /**
     * Returns java.util.logging.Logger object.
     * 
     * @return java.util.logging.Logger object.
     */
    public Logger getLogger();
}
