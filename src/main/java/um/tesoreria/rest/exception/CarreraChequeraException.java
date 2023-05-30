/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class CarreraChequeraException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8984551747212652326L;

	public CarreraChequeraException(Integer facultadId, Integer lectivoId, Integer planId, Integer carreraId,
			Integer curso, Integer geograficaId) {
		super("Cannot find CarreraChequera " + facultadId + "/" + lectivoId + "/" + planId + "/" + carreraId + "/"
				+ curso + "/" + geograficaId);
	}

	public CarreraChequeraException(Long carrerachequeraId) {
		super("Cannot find CarreraChequera " + carrerachequeraId);
	}

}
