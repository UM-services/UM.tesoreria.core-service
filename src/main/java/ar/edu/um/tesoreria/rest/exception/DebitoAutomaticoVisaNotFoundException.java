/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class DebitoAutomaticoVisaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8662107449191025209L;

	public DebitoAutomaticoVisaNotFoundException(Long debitoautomaticoId) {
		super("Cannot find DebitoAutomatico " + debitoautomaticoId);
	}

	public DebitoAutomaticoVisaNotFoundException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId,
			Integer alternativaId) {
		super("Cannot find DebitoAutomatico " + facultadId + "/" + tipochequeraId + "/" + chequeraserieId + "/"
				+ alternativaId);
	}

}
