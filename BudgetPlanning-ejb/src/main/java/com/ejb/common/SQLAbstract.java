
package com.ejb.common;

import com.ejb.common.exceptions.GenericDBOperationException;
import com.ejb.database.QueryProviderLocal;
import com.ejb.database.exceptions.GenericDBException;
import com.ejb.expstructure.ExpensesTypes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.ejb.EJB;
import java.sql.Statement;

/**
 * SQLAbstract class is extended by Session Beans in EJB module that interact 
 * with the database and contain common database operation methods.
 */
public abstract class SQLAbstract 
        extends EjbCommonMethods implements ExpensesTypes {
    
    /**
     * Link to the EJB component that retrieves SQL query from SQL file located 
     * in the given path.
     */
    @EJB
    private QueryProviderLocal queryProvider; 
    
    /**
     * Creates PreparedStatement based on the Connection and Path given.
     * 
     * @param connection database Connection.
     * @param path String path to the SQL file.
     * @return PreparedStatement based on the Connection and Path given.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database access error occurs or this method is called on a closed 
     * connection.
     * @throws com.ejb.database.exceptions.GenericDBException if an error 
     * happens while reading SQL query.
     */
    public PreparedStatement createPreparedStatement(Connection connection,
            String path) throws GenericDBOperationException, GenericDBException {
        PreparedStatement preparedStatement = null;
        String query = queryProvider.getQuery(path);
        try {
            preparedStatement = connection.prepareStatement(query);
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
        return preparedStatement;
    }
    
    /**
     * Clears (closes) the given statement.
     * 
     * @param statement statement to be closed.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if an error
     * happens while closing SQL statement (database access error occurs). 
     */
    public void clear(Statement statement) 
            throws GenericDBOperationException {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
            }
        }
    }
}
