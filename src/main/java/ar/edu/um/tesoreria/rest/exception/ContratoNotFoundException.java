/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ContratoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -600753410140965714L;

	public ContratoNotFoundException(Long contratoId) {
		super("Cannot find Contrato " + contratoId);
	}

}
