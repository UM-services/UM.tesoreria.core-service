/**
 * 
 */
package um.tesoreria.core.exception.view;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class OrdenPagoEntregadoException extends RuntimeException {

	private static final long serialVersionUID = -1182715332167047991L;

	public OrdenPagoEntregadoException(Long ordenPagoId) {
		super(MessageFormat.format("Cannot find OrdenPagoEntregado {0}", ordenPagoId));
	}

}
