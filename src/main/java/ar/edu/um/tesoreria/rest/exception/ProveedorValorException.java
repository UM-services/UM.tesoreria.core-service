/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ProveedorValorException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -498043410710406519L;

	public ProveedorValorException(Long valormovimientoID) {
		super("Cannot find ProveedorValor with ValorMovimiento " + valormovimientoID);
	}

	public ProveedorValorException() {
		super("Cannot find ProveedorValor");
	}
}
