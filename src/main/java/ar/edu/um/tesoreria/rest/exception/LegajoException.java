/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class LegajoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -279162028356804223L;

	public LegajoException(Integer facultadId, BigDecimal personaId, Integer documentoId) {
		super("Cannot find Legajo " + facultadId + "/" + personaId + "/" + documentoId);
	}

}
