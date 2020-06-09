
package ejb.DBConnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author SoundlyGifted
 */
@Singleton
@Startup
public class DbConnectionProvider implements DbConnectionProviderLocal {
    
    private static final String CONFIGS = "resources/config.properties";
    private final Properties configs = new Properties();
    
    private Connection connection;
    
    @Override
    public Connection connection() {
        return connection;
    }
    
    @PostConstruct
    public void init() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(CONFIGS)) {
            System.out.println("*** DbConnectionProvider : Launched. "
                    + "DB Connection CONFIGS input stream: " + stream);
            configs.load(stream);
        } catch (IOException ioex) {
            System.out.println("*** DbConnectionProvider: Input stream or "
                    + "connection Properties loading failure: "
                    + ioex.getMessage() + " ***");
        }
        System.out.println("*** DbConnectionProvider : configs loaded: " + configs);
        try {
            connection = getConnection();
        } catch (SQLException sqlex) {
            System.out.println("*** DbConnectionProvider: connection "
                    + "establishing failure: "
                    + sqlex.getMessage() + " ***");                
        }
        System.out.println("*** DbConnectionProvider : connection established: " 
                + connection.hashCode());
    }
    
    private String getUrl() {
        return configs.getProperty("database.driver") + "://" +
               configs.getProperty("database.host") + ":" +
               configs.getProperty("database.port") + "/" +
               configs.getProperty("database.name");
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        String username = configs.getProperty("database.user");
        String password = configs.getProperty("database.password");
        String url = getUrl();
        connection = DriverManager.getConnection(url, username, password);
        return connection;
    }
}