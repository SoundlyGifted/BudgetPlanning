package com.ejb.common;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * This EJB is used to provide Logger for the application debug purpose.
 */
@Singleton
@Startup
public class LoggerProvider implements LoggerProviderLocal {
    
    private static final String CONFIGS = "/resources/logconfig.properties";

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getLogger() {
        return Logger.getLogger(LoggerProvider.class.getName());
    }

    @PostConstruct
    public void postConstruct() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(CONFIGS)) {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException ioex) {
            System.out.println("[LoggerProvider]: Database connection "
                    + "Properties file loading failure for the configuration "
                    + "file '" + CONFIGS + "': " + ioex.getMessage());
        }
    }
    
    
}
