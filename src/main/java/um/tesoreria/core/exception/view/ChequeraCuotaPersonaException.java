/**
 * 
 */
package um.tesoreria.core.exception.view;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class ChequeraCuotaPersonaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5054712622010726906L;

	public ChequeraCuotaPersonaException(BigDecimal personaId, Integer documentoId, Integer facultadId,
			Integer anho, Integer mes) {
		super("Cannot find ChequeraCuotaPersona " + personaId + "/" + documentoId + "/" + facultadId + "/" + anho + "/"
				+ mes);
	}

}
