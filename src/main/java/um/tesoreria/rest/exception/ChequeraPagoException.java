/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ChequeraPagoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5586943026419608961L;

	public ChequeraPagoException(Long chequerapagoID) {
		super("Cannot find ChequeraPago " + chequerapagoID);
	}
}
