/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class IngresoAsientoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8775388960008293139L;

	public IngresoAsientoNotFoundException(OffsetDateTime fechaContable, Integer tipoPagoId) {
		super("Cannot find IngresoAsiento: " + fechaContable + "/" + tipoPagoId);
	}

}
