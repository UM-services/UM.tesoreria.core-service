/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ComprobanteException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5132262153428523035L;

	public ComprobanteException() {
		super("Cannot find Compropbante");
	}

	public ComprobanteException(Integer comprobanteId) {
		super("Cannot find Comprobante " + comprobanteId);
	}

}
