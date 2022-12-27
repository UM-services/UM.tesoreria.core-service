/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class DocumentoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1825674222510360721L;

	public DocumentoNotFoundException(Integer documentoId) {
		super("Cannot find Documento " + documentoId);
	}

}
