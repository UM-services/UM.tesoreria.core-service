/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class ArancelTipoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1968720650728904283L;

	public ArancelTipoException(Integer aranceltipoId) {
		super("Cannot find ArancelTipo " + aranceltipoId);
	}

}
