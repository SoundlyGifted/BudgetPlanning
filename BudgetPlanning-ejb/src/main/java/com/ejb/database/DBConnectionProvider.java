
package com.ejb.database;

import com.ejb.database.exceptions.GenericDBException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

/**
 * EJB DBConnectionProvider is used to create and close database Connection.
 */
@Singleton
@Startup
public class DBConnectionProvider implements DBConnectionProviderLocal {
    
    private static final String CONFIGS = "/resources/config.properties";
    private final Properties configs = new Properties();
    
    private String dbURL;
    private String dbUser;
    private String dbPass;    


    /**
     * {@inheritDoc}
     */    
    @Override
    public Connection getDBConnection() throws GenericDBException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbURL, dbUser, dbPass);
        } catch (SQLException sqlex) {          
            throw new GenericDBException("Could not "
                    + "connect to the database using URL '" + dbURL 
                    + "', user '" + dbUser + "', password '" + dbPass 
                    + "; " + (sqlex.getMessage() == null ? "" : sqlex.getMessage()), 
                    sqlex);
        }
        return connection;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void closeDBConnection(Connection connection) 
            throws GenericDBException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException sqlex) {
            throw new GenericDBException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
    }
    
    
    private void setDBConnectionParameters() {
        dbURL = configs.getProperty("database.driver") + "://"
                + configs.getProperty("database.host") + ":"
                + configs.getProperty("database.port") + "/"
                + configs.getProperty("database.name");
        dbUser = configs.getProperty("database.user");
        dbPass = configs.getProperty("database.password");
    }
    
    
    private void setDefaultDBConfigs() {
        String defaultDatabaseName = "BudgetPlanningAppDB";
        String defaultUser = "app";
        String defaultPass = "app";   
        
        configs.setProperty("database.driver", "jdbc:derby");
        configs.setProperty("database.host", "localhost");
        configs.setProperty("database.port", "1527");
        configs.setProperty("database.name", defaultDatabaseName);
        configs.setProperty("database.name", defaultUser);
        configs.setProperty("database.password", defaultPass);
    }
    
    
    @PostConstruct
    public void postConstruct() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(CONFIGS)) {
            configs.load(stream);
            setDBConnectionParameters();
        } catch (IOException ioex) {
            System.out.println("[DBConnectionHandler]: Database connection "
                    + "Properties file loading failure for the configuration "
                    + "file '" + CONFIGS + "': " + ioex.getMessage());
            System.out.println("[DBConnectionHandler]: Default Apache Derby "
                    + "database configuration will be used instead.");
            
            setDefaultDBConfigs();
            setDBConnectionParameters();
        }
    }
}