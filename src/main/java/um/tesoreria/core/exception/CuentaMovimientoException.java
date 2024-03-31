/**
 * 
 */
package um.tesoreria.core.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class CuentaMovimientoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1510162749948993693L;

	public CuentaMovimientoException() {
		super("Cannot find CuentaMovimiento");
	}

	public CuentaMovimientoException(Long cuentaMovimientoId) {
		super(MessageFormat.format("Cannot find CuentaMovimiento {0}", cuentaMovimientoId));
	}
}
