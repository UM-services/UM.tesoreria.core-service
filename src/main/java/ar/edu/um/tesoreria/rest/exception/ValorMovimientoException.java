/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class ValorMovimientoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2951741465384684841L;

	public ValorMovimientoException(Integer valorId, Long numero) {
		super("Cannot find ValorMovimiento " + valorId + " / " + numero);
	}

	public ValorMovimientoException(OffsetDateTime fechacontable, Integer ordencontable) {
		super("Cannot find ValorMovimiento " + fechacontable + " / " + ordencontable);
	}

}
