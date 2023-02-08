
package com.ejb.database;

import com.ejb.database.exceptions.GenericDBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Singleton;

/**
 * EJB QueryProvider is used to provide SQL queries red from sql-files.
 */
@Singleton
@DependsOn("DBConnectionProvider")
public class QueryProvider implements QueryProviderLocal {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getQuery(String path) throws GenericDBException {
        path = "resources/sql/" + path + ".sql";
        ClassLoader classLoader = this.getClass().getClassLoader();
        String query = "";
        try (InputStream stream = classLoader.getResourceAsStream(path)) {
            try(Reader reader = new InputStreamReader(stream)) {
                try(BufferedReader in = new BufferedReader(reader)) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        builder.append(line).append(System.lineSeparator());
                    }
                    query = builder.toString();
                }
            }
        } catch (IOException ioex) {
            throw new GenericDBException(ioex.getMessage() == null 
                    ? "" : ioex.getMessage(), ioex);
        }
        return query;
    }
}