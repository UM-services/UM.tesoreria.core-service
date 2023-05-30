/**
 * 
 */
package um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class DomicilioException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2288561937963883733L;

	public DomicilioException(BigDecimal personaId, Integer documentoId) {
		super("Cannot find Domicilio " + personaId + "/" + documentoId);
	}

	public DomicilioException(Long domicilioId) {
		super("Cannot find Domicilio " + domicilioId);
	}

	public DomicilioException(BigDecimal personaId) {
		super("Cannot find Domicilio " + personaId);
	}

}
