/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class ChequeraPagoReemplazoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4552876178441707486L;

	public ChequeraPagoReemplazoException(Long chequerapagoreemplazoID) {
		super("Cannot find ChequeraPagoReemplazo " + chequerapagoreemplazoID);
	}
}
