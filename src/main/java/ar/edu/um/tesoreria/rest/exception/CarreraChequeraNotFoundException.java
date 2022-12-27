/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class CarreraChequeraNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8984551747212652326L;

	public CarreraChequeraNotFoundException(Integer facultadId, Integer lectivoId, Integer planId, Integer carreraId,
			Integer curso, Integer geograficaId) {
		super("Cannot find CarreraChequera " + facultadId + "/" + lectivoId + "/" + planId + "/" + carreraId + "/"
				+ curso + "/" + geograficaId);
	}

	public CarreraChequeraNotFoundException(Long carrerachequeraId) {
		super("Cannot find CarreraChequera " + carrerachequeraId);
	}

}
