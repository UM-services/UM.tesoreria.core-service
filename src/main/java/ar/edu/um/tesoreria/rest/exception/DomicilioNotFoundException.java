/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class DomicilioNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2288561937963883733L;

	public DomicilioNotFoundException(BigDecimal personaId, Integer documentoId) {
		super("Cannot find Domicilio " + personaId + "/" + documentoId);
	}

	public DomicilioNotFoundException(Long domicilioId) {
		super("Cannot find Domicilio " + domicilioId);
	}

	public DomicilioNotFoundException(BigDecimal personaId) {
		super("Cannot find Domicilio " + personaId);
	}

}
