/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ProveedorValorNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -498043410710406519L;

	public ProveedorValorNotFoundException(Long valormovimientoID) {
		super("Cannot find ProveedorValor with ValorMovimiento " + valormovimientoID);
	}

	public ProveedorValorNotFoundException() {
		super("Cannot find ProveedorValor");
	}
}
