
package ejb.DBConnection;

import java.io.IOException;
import javax.ejb.Local;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface QueryProviderLocal {
    public String getQuery(String path) throws IOException;
}
