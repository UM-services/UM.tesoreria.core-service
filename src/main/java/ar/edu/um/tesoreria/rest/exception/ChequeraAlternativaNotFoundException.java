/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ChequeraAlternativaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8064240347168513032L;

	public ChequeraAlternativaNotFoundException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId,
			Integer productoId, Integer alternativaId) {
		super("Cannot find ChequeraAlternativa " + facultadId + "/" + tipochequeraId + "/" + chequeraserieId + "/"
				+ productoId + "/" + alternativaId);
	}

}
