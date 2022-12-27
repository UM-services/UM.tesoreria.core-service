/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ContratoPeriodoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5382823783472393089L;

	public ContratoPeriodoNotFoundException(Long contratoId, Integer anho, Integer mes) {
		super("Cannot find ContratoPeriodo " + contratoId + "/" + anho + "/" + mes);
	}

	public ContratoPeriodoNotFoundException(Long contratoperiodoId) {
		super("Cannot find ContratoPeriodo " + contratoperiodoId);
	}

}
