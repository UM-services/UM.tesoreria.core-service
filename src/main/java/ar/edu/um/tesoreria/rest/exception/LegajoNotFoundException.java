/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class LegajoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -279162028356804223L;

	public LegajoNotFoundException(Integer facultadId, BigDecimal personaId, Integer documentoId) {
		super("Cannot find Legajo " + facultadId + "/" + personaId + "/" + documentoId);
	}

}
