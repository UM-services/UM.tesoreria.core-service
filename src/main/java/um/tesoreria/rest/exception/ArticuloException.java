/**
 * 
 */
package um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class ArticuloException extends RuntimeException {

	private static final long serialVersionUID = -7584922659043528713L;

	public ArticuloException(Long articuloId) {
		super(MessageFormat.format("Cannot find Articulo {0}", articuloId));
	}

}
