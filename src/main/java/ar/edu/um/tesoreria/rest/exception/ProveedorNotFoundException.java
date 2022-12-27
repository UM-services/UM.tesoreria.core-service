/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ProveedorNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8693063086565994532L;

	public ProveedorNotFoundException(Integer proveedorID) {
		super("Cannot find Proveedor " + proveedorID);
	}

	public ProveedorNotFoundException(String cuit) {
		super("Cannot find Proveedor " + cuit);
	}

	public ProveedorNotFoundException() {
		super("Cannot find last Proveedor");
	}
}
