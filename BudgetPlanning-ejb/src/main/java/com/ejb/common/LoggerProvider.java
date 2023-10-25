package com.ejb.common;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This EJB is used to provide Logger for the application debug purpose.
 */
@Singleton
@Startup
public class LoggerProvider implements LoggerProviderLocal {
    
    private static final String CONFIGS = "/resources/logconfig.properties";
    private final Properties handlerConfigs = new Properties();
    
    /* Default configuration logging handler properties to use in case of
       configuration file loading failure.
    */
    private final String defaultLevel = "ALL";
    private final String defaultFormatter = "java.util.logging.SimpleFormatter";
    private final String defaultLimit = "1000000";
    private final String defaultCount = "5";
    private final String defaultAppend = "true";
    private final String defaultPattern = "BudgetPlanningLog.txt";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getLogger() {
        Logger logger = Logger.getLogger(LoggerProvider.class.getName());
        return logger;
    }

    /**
     * {@inheritDoc}
     */
    @Override    
    public FileHandler getFileHandler() {
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler(
                    handlerConfigs.getProperty("java.util.logging.FileHandler.pattern"),
                    Integer.parseInt(handlerConfigs.getProperty("java.util.logging.FileHandler.limit")),
                    Integer.parseInt(handlerConfigs.getProperty("java.util.logging.FileHandler.count")),
                    Boolean.parseBoolean(handlerConfigs.getProperty("java.util.logging.FileHandler.append"))
            );
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException ioex) {
            System.out.println("[LoggerProvider]: Logging Handler cannot be "
                    + "initialized: " + ioex.getMessage());          
        }
        return fileHandler;
    }
    
    
    @PostConstruct
    public void postConstruct() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(CONFIGS)) {
            handlerConfigs.load(stream);
        } catch (IOException ioex) {
            System.out.println("[LoggerProvider]: Logging Handler configuration "
                    + "file loading failure for the file '" + CONFIGS + "': " 
                    + ioex.getMessage());
            // Loading default configs.
            handlerConfigs.setProperty("java.util.logging.FileHandler.level", 
                    defaultLevel);
            handlerConfigs.setProperty("java.util.logging.FileHandler.formatter", 
                    defaultFormatter);
            handlerConfigs.setProperty("java.util.logging.FileHandler.limit", 
                    defaultLimit);
            handlerConfigs.setProperty("java.util.logging.FileHandler.count", 
                    defaultCount);
            handlerConfigs.setProperty("java.util.logging.FileHandler.append", 
                    defaultAppend);            
            handlerConfigs.setProperty("java.util.logging.FileHandler.pattern", 
                    defaultPattern);

        }
    }
}
