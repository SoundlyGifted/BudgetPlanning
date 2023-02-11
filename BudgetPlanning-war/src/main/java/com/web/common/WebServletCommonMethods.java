
package com.web.common;

import com.ejb.common.exceptions.GenericDBOperationException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;

/**
 * EJB WebServletCommonMethods contains common methods used by application 
 * servlets.
 */
@Stateless
@LocalBean
public class WebServletCommonMethods {

    /**
     * Returns collection of IDs from a database table with a given name.
     * 
     * @param connection database Connection.
     * @param tableName name of the database table.
     * @return collection of IDs from a database table with a given name.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public ArrayList<Integer> getIdList(Connection connection,
            String tableName) throws GenericDBOperationException {
        String query = "select ID from " + tableName;
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
            Collection<Integer> IdList = new LinkedList<>();
            while (resultSet.next()) {
                IdList.add(resultSet.getInt("ID"));
            }
            return new ArrayList<>(IdList);
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
    }

    /**
     * Returns collection of planning dates from PLANNED_VARIABLE_PARAMS 
     * database table.
     * 
     * @param connection database Connection.
     * @return collection of planning dates from PLANNED_VARIABLE_PARAMS 
     * database table.
     * @throws com.ejb.common.exceptions.GenericDBOperationException if a 
     * database operation related exception is thrown.
     */
    public ArrayList<String> getDatesList(Connection connection) 
            throws GenericDBOperationException {
        String query = "select distinct DATE from PLANNED_VARIABLE_PARAMS";
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
            Collection<String> DatesList = new LinkedList<>();
            while (resultSet.next()) {
                DatesList.add(resultSet.getString("DATE"));
            }
            return new ArrayList<>(DatesList);
        } catch (SQLException sqlex) {
            throw new GenericDBOperationException(sqlex.getMessage() == null 
                    ? "" : sqlex.getMessage(), sqlex);
        }
    }
}
