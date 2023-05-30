/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class GeograficaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -454146588384710522L;

	public GeograficaException(Integer geograficaID) {
		super("Cannot find Geografica " + geograficaID);
	}

}
