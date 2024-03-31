/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class ChequeraEliminadaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4264963918618776147L;

	public ChequeraEliminadaException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		super("Cannot find ChequeraEliminada " + facultadId + "/" + tipochequeraId + "/" + chequeraserieId);
	}

}
