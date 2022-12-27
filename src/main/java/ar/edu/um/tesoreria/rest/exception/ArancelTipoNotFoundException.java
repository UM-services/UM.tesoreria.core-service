/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ArancelTipoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1968720650728904283L;

	public ArancelTipoNotFoundException(Integer aranceltipoId) {
		super("Cannot find ArancelTipo " + aranceltipoId);
	}

}
