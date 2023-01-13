
package web.common;

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
     */
    public ArrayList<Integer> getIdList(Connection connection,
            String tableName) {

        Statement statement = null;
        String query = "select ID from " + tableName;

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("*** WebServletCommonMethods : getIdList() "
                    + "error while creating statement: " + ex.getMessage());
            return null;
        }

        try (ResultSet resultSet = statement.executeQuery(query)) {
            Collection<Integer> IdList = new LinkedList<>();
            while (resultSet.next()) {
                IdList.add(resultSet.getInt("ID"));
            }
            return new ArrayList<>(IdList);
        } catch (SQLException ex) {
            System.out.println("*** WebServletCommonMethods : getIdList() "
                    + "error while executing '" + query + "' query: "
                    + ex.getMessage());
            return null;
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                System.out.println("*** WebServletCommonMethods : getIdList() "
                        + "error while closing statement: " + ex.getMessage());
            }
        }
    }

    /**
     * Returns collection of planning dates from PLANNED_VARIABLE_PARAMS 
     * database table.
     * 
     * @param connection database Connection.
     * @return collection of planning dates from PLANNED_VARIABLE_PARAMS 
     * database table.
     */
    public ArrayList<String> getDatesList(Connection connection) {

        Statement statement = null;
        String query = "select distinct DATE from PLANNED_VARIABLE_PARAMS";

        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println("*** WebServletCommonMethods : getDatesList() "
                    + "error while creating statement: " + ex.getMessage());
            return null;
        }

        try (ResultSet resultSet = statement.executeQuery(query)) {
            Collection<String> DatesList = new LinkedList<>();
            while (resultSet.next()) {
                DatesList.add(resultSet.getString("DATE"));
            }
            return new ArrayList<>(DatesList);
        } catch (SQLException ex) {
            System.out.println("*** WebServletCommonMethods : getDatesList() "
                    + "error while executing '" + query + "' query: "
                    + ex.getMessage());
            return null;
        } finally {
            try {
                statement.close();
            } catch (SQLException ex) {
                System.out.println("*** WebServletCommonMethods : "
                        + "getDatesList() error while closing statement: " 
                        + ex.getMessage());
            }
        }
    }
}
