/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ProveedorException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8693063086565994532L;

	public ProveedorException(Integer proveedorID) {
		super("Cannot find Proveedor " + proveedorID);
	}

	public ProveedorException(String cuit) {
		super("Cannot find Proveedor " + cuit);
	}

	public ProveedorException() {
		super("Cannot find last Proveedor");
	}
}
