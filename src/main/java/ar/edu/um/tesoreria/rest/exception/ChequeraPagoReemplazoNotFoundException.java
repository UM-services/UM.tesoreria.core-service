/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ChequeraPagoReemplazoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4552876178441707486L;

	public ChequeraPagoReemplazoNotFoundException(Long chequerapagoreemplazoID) {
		super("Cannot find ChequeraPagoReemplazo " + chequerapagoreemplazoID);
	}
}
