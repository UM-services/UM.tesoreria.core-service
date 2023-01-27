/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class DebitoAutomaticoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6876853078527889405L;

	public DebitoAutomaticoException(Long debitoautomaticoId) {
		super("Cannot find DebitoAutomatico " + debitoautomaticoId);
	}

	public DebitoAutomaticoException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId,
			Integer alternativaId) {
		super("Cannot find DebitoAutomatico " + facultadId + "/" + tipochequeraId + "/" + chequeraserieId + "/"
				+ alternativaId);
	}

}
