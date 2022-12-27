/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception.view;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class OrdenPagoEntregadoNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1182715332167047991L;

	public OrdenPagoEntregadoNotFoundException(Long ordenPagoId) {
		super(MessageFormat.format("Cannot find OrdenPagoEntregado {0}", ordenPagoId));
	}

}
