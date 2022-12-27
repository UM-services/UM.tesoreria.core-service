/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class InfoLdapNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3294420427153958998L;

	public InfoLdapNotFoundException(BigDecimal personaId) {
		super("Cannot find InfoLdap " + personaId);
	}

}
