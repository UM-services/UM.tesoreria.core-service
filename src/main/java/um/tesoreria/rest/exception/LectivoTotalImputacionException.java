/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class LectivoTotalImputacionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -697565205822891525L;

	public LectivoTotalImputacionException(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
			Integer productoId) {
		super("Cannot find LectivoTotalImputacion " + facultadId + "/" + lectivoId + "/" + tipoChequeraId + "/"
				+ productoId);
	}

	public LectivoTotalImputacionException(Long lectivoTotalImputacionId) {
		super("Cannot find LectivoTotalImputacion " + lectivoTotalImputacionId);
	}

}
