/**
 * 
 */
package um.tesoreria.core.exception;

import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class IngresoAsientoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8775388960008293139L;

	public IngresoAsientoException(OffsetDateTime fechaContable, Integer tipoPagoId) {
		super("Cannot find IngresoAsiento: " + fechaContable + "/" + tipoPagoId);
	}

}
