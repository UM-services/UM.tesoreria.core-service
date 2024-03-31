/**
 * 
 */
package um.tesoreria.core.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class ChequeraSerieException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1977749507964033206L;

	public ChequeraSerieException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		super("Cannot find ChequeraSerie " + facultadId + "/" + tipoChequeraId + "/" + chequeraSerieId);
	}

	public ChequeraSerieException(Long chequeraId) {
		super("Cannot find ChequeraSerie " + chequeraId);
	}

	public ChequeraSerieException(BigDecimal personaId, Integer documentoId, Integer facultadId,
			Integer lectivoId, Integer geograficaId, Integer tipoChequeraId) {
		super("Cannot find ChequeraSerie " + personaId + "/" + documentoId + "/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId + "/" + tipoChequeraId);
	}

	public ChequeraSerieException(BigDecimal personaId, Integer documentoId, Integer facultadId,
			Integer lectivoId, Integer geograficaId) {
		super("Cannot find ChequeraSerie " + personaId + "/" + documentoId + "/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId);
	}

	public ChequeraSerieException(BigDecimal personaId, Integer documentoId, Integer facultadId) {
		super("Cannot find ChequeraSerie - personaId -> " + personaId + " - documentoId -> " + documentoId + " - facultadId -> " + facultadId);
	}

}
