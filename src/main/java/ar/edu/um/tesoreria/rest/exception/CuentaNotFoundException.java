/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class CuentaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5189073715707599040L;

	public CuentaNotFoundException(BigDecimal cuenta) {
		super(MessageFormat.format("Cannot find Cuenta {0}", cuenta));
	}

	public CuentaNotFoundException(Long cuentaContableId) {
		super(MessageFormat.format("Cannot find Cuenta {0}", cuentaContableId));
	}

}
