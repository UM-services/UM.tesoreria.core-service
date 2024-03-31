/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class ContratadoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -389902692451940030L;

	public ContratadoException(Long contratadoId) {
		super("Cannot find Contratado " + contratadoId);
	}

	public ContratadoException(Long personaclave, Boolean persona) {
		super("Cannot find Contratado (persona) " + personaclave);
	}

}
