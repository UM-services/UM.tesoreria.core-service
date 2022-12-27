/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class DebitoAutomaticoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6876853078527889405L;

	public DebitoAutomaticoNotFoundException(Long debitoautomaticoId) {
		super("Cannot find DebitoAutomatico " + debitoautomaticoId);
	}

	public DebitoAutomaticoNotFoundException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId,
			Integer alternativaId) {
		super("Cannot find DebitoAutomatico " + facultadId + "/" + tipochequeraId + "/" + chequeraserieId + "/"
				+ alternativaId);
	}

}
