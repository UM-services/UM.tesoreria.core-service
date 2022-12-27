/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class ValorMovimientoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2951741465384684841L;

	public ValorMovimientoNotFoundException(Integer valorId, Long numero) {
		super("Cannot find ValorMovimiento " + valorId + " / " + numero);
	}

	public ValorMovimientoNotFoundException(OffsetDateTime fechacontable, Integer ordencontable) {
		super("Cannot find ValorMovimiento " + fechacontable + " / " + ordencontable);
	}

}
