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

	public ProveedorValorException(Long valormovimientoId) {
		super("Cannot find ProveedorValor with ValorMovimiento " + valormovimientoId);
	}

	public ProveedorValorException() {
		super("Cannot find ProveedorValor");
	}
}
