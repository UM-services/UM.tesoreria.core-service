/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class PostalException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8262327331585470633L;

	public PostalException(Integer codigopostal) {
		super("Cannot find Postal " + codigopostal);
	}

}
