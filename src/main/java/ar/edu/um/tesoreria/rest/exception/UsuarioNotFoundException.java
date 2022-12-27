/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class UsuarioNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6099848254988715350L;

	public UsuarioNotFoundException() {
		super("Cannot find Usuario by Password");
	}

	public UsuarioNotFoundException(String usuarioId) {
		super("Cannot find Usuario " + usuarioId);
	}

}
