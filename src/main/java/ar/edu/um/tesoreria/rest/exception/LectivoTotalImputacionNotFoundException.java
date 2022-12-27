/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class LectivoTotalImputacionNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -697565205822891525L;

	public LectivoTotalImputacionNotFoundException(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
			Integer productoId) {
		super("Cannot find LectivoTotalImputacion " + facultadId + "/" + lectivoId + "/" + tipoChequeraId + "/"
				+ productoId);
	}

	public LectivoTotalImputacionNotFoundException(Long lectivoTotalImputacionId) {
		super("Cannot find LectivoTotalImputacion " + lectivoTotalImputacionId);
	}

}
