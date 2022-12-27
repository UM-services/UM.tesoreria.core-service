/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class CursoCargoContratadoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3695486201062424065L;

	public CursoCargoContratadoNotFoundException(Long cursoId, Integer anho, Integer mes, Long contratadoId) {
		super("Cannot find CursoCargoContratado " + cursoId + "/" + anho + "/" + mes + "/" + contratadoId);
	}

	public CursoCargoContratadoNotFoundException(Long cursocargocontratadoId) {
		super("Cannot find CursoCargoContratado " + cursocargocontratadoId);
	}

}
