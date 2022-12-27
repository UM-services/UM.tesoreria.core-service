/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class MatriculaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3227677605225493808L;

	public MatriculaNotFoundException(Long matriculaId) {
		super("Cannot find Matricula " + matriculaId);
	}

	public MatriculaNotFoundException(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId,
			Integer clasechequeraId) {
		super("Cannot find Matricula " + facultadId + "/" + personaId + "/" + documentoId + "/" + lectivoId + "/"
				+ clasechequeraId);
	}

	public MatriculaNotFoundException() {
		super("Cannot find Matricula");
	}

}
