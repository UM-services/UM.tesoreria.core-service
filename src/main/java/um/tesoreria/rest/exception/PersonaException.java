/**
 * 
 */
package um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class PersonaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3837453940342207846L;

	public PersonaException(BigDecimal personaId, Integer documentoId) {
		super("Cannot find Persona " + personaId + "/" + documentoId);
	}

	public PersonaException(BigDecimal personaId) {
		super("Cannot find Persona " + personaId);
	}

	public PersonaException(Long uniqueId) {
		super("Cannot find Persona " + uniqueId);
	}

}
