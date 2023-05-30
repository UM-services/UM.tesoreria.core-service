/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ContratoPeriodoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5382823783472393089L;

	public ContratoPeriodoException(Long contratoId, Integer anho, Integer mes) {
		super("Cannot find ContratoPeriodo " + contratoId + "/" + anho + "/" + mes);
	}

	public ContratoPeriodoException(Long contratoperiodoId) {
		super("Cannot find ContratoPeriodo " + contratoperiodoId);
	}

}
