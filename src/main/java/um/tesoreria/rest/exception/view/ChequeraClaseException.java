/**
 * 
 */
package um.tesoreria.rest.exception.view;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class ChequeraClaseException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6045173369209103125L;

	public ChequeraClaseException(Integer facultadId, BigDecimal personaId, Integer documentoId,
			Integer clasechequeraId, Integer lectivoId) {
		super("Cannot find ChequeraClase " + facultadId + "/" + personaId + "/" + documentoId + "/" + clasechequeraId
				+ "/" + lectivoId);
	}

	public ChequeraClaseException() {
		super("Cannot find ChequeraClase");
	}

}
