/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class ValorException extends RuntimeException {

	private static final long serialVersionUID = 805063401992825406L;

	public ValorException(Integer valorId) {
		super("Cannot find Valor " + valorId);
	}

}
