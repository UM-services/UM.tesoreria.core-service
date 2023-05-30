/**
 * 
 */
package um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class EntregaException extends RuntimeException {

	private static final long serialVersionUID = 1026853568021260789L;

	public EntregaException(Long entregaId) {
		super(MessageFormat.format("Cannot find Entrega {0}", entregaId));
	}

}
