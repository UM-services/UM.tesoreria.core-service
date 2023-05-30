/**
 * 
 */
package um.tesoreria.rest.exception;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class PersonaSuspendidoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2946659981362173003L;

	public PersonaSuspendidoException(BigDecimal personaId, Integer documentoId) {
		super(MessageFormat.format("Cannot find PersonaSuspendido: {0}/{1}", personaId, documentoId));
	}

	public PersonaSuspendidoException(Long personaSuspendidoId) {
		super(MessageFormat.format("Cannot find PersonaSuspendido: {0}", personaSuspendidoId));
	}

}
