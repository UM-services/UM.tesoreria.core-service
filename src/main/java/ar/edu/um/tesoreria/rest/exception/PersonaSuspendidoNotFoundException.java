/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class PersonaSuspendidoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2946659981362173003L;

	public PersonaSuspendidoNotFoundException(BigDecimal personaId, Integer documentoId) {
		super(MessageFormat.format("Cannot find PersonaSuspendido: {0}/{1}", personaId, documentoId));
	}

	public PersonaSuspendidoNotFoundException(Long personaSuspendidoId) {
		super(MessageFormat.format("Cannot find PersonaSuspendido: {0}", personaSuspendidoId));
	}

}
