/**
 * 
 */
package um.tesoreria.core.exception.view;

/**
 * @author daniel
 *
 */
public class TipoChequeraSedeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1524964774034800849L;

	public TipoChequeraSedeException(Integer tipochequeraId) {
		super("Cannot find TipoChequeraSede " + tipochequeraId);
	}
}
