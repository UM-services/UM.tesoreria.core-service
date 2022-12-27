/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class PostalNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8262327331585470633L;

	public PostalNotFoundException(Integer codigopostal) {
		super("Cannot find Postal " + codigopostal);
	}

}
