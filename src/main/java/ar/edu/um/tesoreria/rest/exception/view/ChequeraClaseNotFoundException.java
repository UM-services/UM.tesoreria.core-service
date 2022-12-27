/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception.view;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class ChequeraClaseNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6045173369209103125L;

	public ChequeraClaseNotFoundException(Integer facultadId, BigDecimal personaId, Integer documentoId,
			Integer clasechequeraId, Integer lectivoId) {
		super("Cannot find ChequeraClase " + facultadId + "/" + personaId + "/" + documentoId + "/" + clasechequeraId
				+ "/" + lectivoId);
	}

	public ChequeraClaseNotFoundException() {
		super("Cannot find ChequeraClase");
	}

}
