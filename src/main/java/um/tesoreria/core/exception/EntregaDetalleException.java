/**
 * 
 */
package um.tesoreria.core.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class EntregaDetalleException extends RuntimeException {

	private static final long serialVersionUID = 541393234073801295L;

	public EntregaDetalleException(Long entregaDetalleId, String message) {
		super(MessageFormat.format("Cannot find EntregaDetalle {0} - {1}", message, entregaDetalleId));
	}

}
