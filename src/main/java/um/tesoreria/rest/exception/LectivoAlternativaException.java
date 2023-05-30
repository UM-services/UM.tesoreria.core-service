/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class LectivoAlternativaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1102626687111313686L;

	public LectivoAlternativaException(Integer facultadId, Integer lectivoId, Integer tipochequeraId,
			Integer productoId, Integer alternativaId) {
		super("Cannot find Lectivo " + facultadId + "/" + lectivoId + "/" + tipochequeraId + "/" + productoId + "/"
				+ alternativaId);
	}

}
