/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class EntregaDetalleNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 541393234073801295L;

	public EntregaDetalleNotFoundException(Long entregaDetalleId) {
		super(MessageFormat.format("Cannot find EntregaDetalle {0}", entregaDetalleId));
	}

}
