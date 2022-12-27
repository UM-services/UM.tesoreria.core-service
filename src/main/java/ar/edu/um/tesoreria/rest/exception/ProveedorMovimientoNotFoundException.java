/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ProveedorMovimientoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1366272985989940191L;

	public ProveedorMovimientoNotFoundException(Long proveedormovimientoID) {
		super("Cannot find ProveedorMovimiento " + proveedormovimientoID);
	}

}
