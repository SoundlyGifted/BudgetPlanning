
package ejb.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;

/**
 *
 * @author SoundlyGifted
 */
@Singleton
@DependsOn("DbConnectionProvider")
public class QueryProvider implements QueryProviderLocal {
    @Override
    public String getQuery(String path) throws IOException {
        path = "resources/sql/" + path + ".sql";
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(path)) {
            try(Reader reader = new InputStreamReader(stream)) {
                try(BufferedReader in = new BufferedReader(reader)) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        builder.append(line).append(System.lineSeparator());
                    }
                    return builder.toString();
                }
            }
        }
    }
}