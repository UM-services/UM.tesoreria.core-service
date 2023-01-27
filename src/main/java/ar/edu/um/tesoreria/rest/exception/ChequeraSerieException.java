/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

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

	public ChequeraSerieException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		super("Cannot find ChequeraSerie " + facultadId + "/" + tipochequeraId + "/" + chequeraserieId);
	}

	public ChequeraSerieException(Long chequeraId) {
		super("Cannot find ChequeraSerie " + chequeraId);
	}

	public ChequeraSerieException(BigDecimal personaId, Integer documentoId, Integer facultadId,
			Integer lectivoId, Integer geograficaId, Integer tipochequeraId) {
		super("Cannot find ChequeraSerie " + personaId + "/" + documentoId + "/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId + "/" + tipochequeraId);
	}

	public ChequeraSerieException(BigDecimal personaId, Integer documentoId, Integer facultadId,
			Integer lectivoId, Integer geograficaId) {
		super("Cannot find ChequeraSerie " + personaId + "/" + documentoId + "/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId);
	}

}
