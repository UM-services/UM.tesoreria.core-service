/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ChequeraEliminadaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4264963918618776147L;

	public ChequeraEliminadaNotFoundException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		super("Cannot find ChequeraEliminada " + facultadId + "/" + tipochequeraId + "/" + chequeraserieId);
	}

}
