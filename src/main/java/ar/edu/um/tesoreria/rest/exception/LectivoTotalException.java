/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class LectivoTotalException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5995752828626929159L;

	public LectivoTotalException(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
			Integer productoId) {
		super("Cannot find LectivoTotal " + facultadId + "/" + lectivoId + "/" + tipoChequeraId + "/" + productoId);
	}

}
