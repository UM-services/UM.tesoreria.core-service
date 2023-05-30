/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class CursoCargoContratadoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3695486201062424065L;

	public CursoCargoContratadoException(Long cursoId, Integer anho, Integer mes, Long contratadoId) {
		super("Cannot find CursoCargoContratado " + cursoId + "/" + anho + "/" + mes + "/" + contratadoId);
	}

	public CursoCargoContratadoException(Long cursocargocontratadoId) {
		super("Cannot find CursoCargoContratado " + cursocargocontratadoId);
	}

}
