/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class UsuarioLdapException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3733466295667032382L;

	public UsuarioLdapException (BigDecimal documento) {
		super("Cannot find Usuario " + documento);
	}
}
