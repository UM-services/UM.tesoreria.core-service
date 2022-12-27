/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception.view;

/**
 * @author daniel
 *
 */
public class TipoChequeraSedeNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1524964774034800849L;

	public TipoChequeraSedeNotFoundException(Integer tipochequeraId) {
		super("Cannot find TipoChequeraSede " + tipochequeraId);
	}
}
