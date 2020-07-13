
package web.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author SoundlyGifted
 */
@Stateless
@LocalBean
public class WebServletCommonMethods {
    
    /* returns Collection of IDs from a database table. */
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
                System.out.println("*** WebServletCommonMethods : error while "
                        + "closing statement: " + ex.getMessage());
            }
        }
    }

}
