/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ChequeraPagoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5586943026419608961L;

	public ChequeraPagoNotFoundException(Long chequerapagoID) {
		super("Cannot find ChequeraPago " + chequerapagoID);
	}
}
