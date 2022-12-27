/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class PersonaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3837453940342207846L;

	public PersonaNotFoundException(BigDecimal personaId, Integer documentoId) {
		super("Cannot find Persona " + personaId + "/" + documentoId);
	}

	public PersonaNotFoundException(BigDecimal personaId) {
		super("Cannot find Persona " + personaId);
	}

	public PersonaNotFoundException(Long uniqueId) {
		super("Cannot find Persona " + uniqueId);
	}

}
