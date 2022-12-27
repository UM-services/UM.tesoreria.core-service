/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class GeograficaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -454146588384710522L;

	public GeograficaNotFoundException(Integer geograficaID) {
		super("Cannot find Geografica " + geograficaID);
	}

}
