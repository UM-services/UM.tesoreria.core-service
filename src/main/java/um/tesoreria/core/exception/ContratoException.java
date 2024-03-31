/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class ContratoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -600753410140965714L;

	public ContratoException(Long contratoId) {
		super("Cannot find Contrato " + contratoId);
	}

}
