/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class ChequeraSerieNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1977749507964033206L;

	public ChequeraSerieNotFoundException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		super("Cannot find ChequeraSerie " + facultadId + "/" + tipochequeraId + "/" + chequeraserieId);
	}

	public ChequeraSerieNotFoundException(Long chequeraId) {
		super("Cannot find ChequeraSerie " + chequeraId);
	}

	public ChequeraSerieNotFoundException(BigDecimal personaId, Integer documentoId, Integer facultadId,
			Integer lectivoId, Integer geograficaId, Integer tipochequeraId) {
		super("Cannot find ChequeraSerie " + personaId + "/" + documentoId + "/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId + "/" + tipochequeraId);
	}

	public ChequeraSerieNotFoundException(BigDecimal personaId, Integer documentoId, Integer facultadId,
			Integer lectivoId, Integer geograficaId) {
		super("Cannot find ChequeraSerie " + personaId + "/" + documentoId + "/" + facultadId + "/" + lectivoId + "/"
				+ geograficaId);
	}

}
