/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class LectivoAlternativaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1102626687111313686L;

	public LectivoAlternativaNotFoundException(Integer facultadId, Integer lectivoId, Integer tipochequeraId,
			Integer productoId, Integer alternativaId) {
		super("Cannot find Lectivo " + facultadId + "/" + lectivoId + "/" + tipochequeraId + "/" + productoId + "/"
				+ alternativaId);
	}

}
