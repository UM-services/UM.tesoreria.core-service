/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class ArticuloNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7584922659043528713L;

	public ArticuloNotFoundException(Long articuloId) {
		super(MessageFormat.format("Cannot find Articulo {0}", articuloId));
	}

}
