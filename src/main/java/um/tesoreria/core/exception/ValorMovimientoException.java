/**
 * 
 */
package um.tesoreria.core.exception;

import java.text.MessageFormat;
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

	public ValorMovimientoException(Long valorMovimientoId) {
		super("Cannot find ValorMovimiento " + valorMovimientoId);
	}

	public ValorMovimientoException(Integer valorId, Long numero) {
		super("Cannot find ValorMovimiento " + valorId + " / " + numero);
	}

	public ValorMovimientoException(OffsetDateTime fechacontable, Integer ordencontable) {
		super("Cannot find ValorMovimiento " + fechacontable + " / " + ordencontable);
	}

	public ValorMovimientoException(Integer valorId, Long numero, Long bancariaId) {
		super(MessageFormat.format("Cannot find ValorMovimiento {0}/{1}/{2}", valorId, numero, bancariaId));
	}

}
