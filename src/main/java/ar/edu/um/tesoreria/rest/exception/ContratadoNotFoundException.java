/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ContratadoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -389902692451940030L;

	public ContratadoNotFoundException(Long contratadoId) {
		super("Cannot find Contratado " + contratadoId);
	}

	public ContratadoNotFoundException(Long personaclave, Boolean persona) {
		super("Cannot find Contratado (persona) " + personaclave);
	}

}
