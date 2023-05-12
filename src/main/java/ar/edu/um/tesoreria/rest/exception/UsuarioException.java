/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class UsuarioException extends RuntimeException {

	private static final long serialVersionUID = -6099848254988715350L;

	public UsuarioException() {
		super("Cannot find Usuario by Password");
	}

	public UsuarioException(String login) {
		super("Cannot find Usuario " + login);
	}

	public UsuarioException(Long userId) {
		super("Cannot find Usuario + " + userId);
	}

}
