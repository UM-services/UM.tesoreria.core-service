/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class CuentaMovimientoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1510162749948993693L;

	public CuentaMovimientoNotFoundException() {
		super("Cannot find CuentaMovimiento");
	}

	public CuentaMovimientoNotFoundException(Long cuentaMovimientoId) {
		super(MessageFormat.format("Cannot find CuentaMovimiento {0}", cuentaMovimientoId));
	}
}
