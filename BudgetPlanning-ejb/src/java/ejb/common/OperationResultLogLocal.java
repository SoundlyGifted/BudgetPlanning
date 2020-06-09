
package ejb.common;

import javax.ejb.Local;
import javax.servlet.http.HttpSession;

/**
 *
 * @author SoundlyGifted
 */
@Local
public interface OperationResultLogLocal {
    public void add(HttpSession session, String resultToAdd);
    public void clear(HttpSession session);
}
