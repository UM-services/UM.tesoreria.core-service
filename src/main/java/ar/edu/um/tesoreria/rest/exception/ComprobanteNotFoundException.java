/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ComprobanteNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5132262153428523035L;

	public ComprobanteNotFoundException(Integer comprobanteId) {
		super("Cannot find Comprobante " + comprobanteId);
	}

}
