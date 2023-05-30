/**
 * 
 */
package um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class MatriculaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3227677605225493808L;

	public MatriculaException(Long matriculaId) {
		super("Cannot find Matricula " + matriculaId);
	}

	public MatriculaException(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId,
			Integer clasechequeraId) {
		super("Cannot find Matricula " + facultadId + "/" + personaId + "/" + documentoId + "/" + lectivoId + "/"
				+ clasechequeraId);
	}

	public MatriculaException() {
		super("Cannot find Matricula");
	}

}
