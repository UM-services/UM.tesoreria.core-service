/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class DebitoAutomaticoVisaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8662107449191025209L;

	public DebitoAutomaticoVisaException(Long debitoautomaticoId) {
		super("Cannot find DebitoAutomatico " + debitoautomaticoId);
	}

	public DebitoAutomaticoVisaException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId,
			Integer alternativaId) {
		super("Cannot find DebitoAutomatico " + facultadId + "/" + tipochequeraId + "/" + chequeraserieId + "/"
				+ alternativaId);
	}

}
