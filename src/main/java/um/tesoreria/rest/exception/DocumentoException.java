/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class DocumentoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1825674222510360721L;

	public DocumentoException(Integer documentoId) {
		super("Cannot find Documento " + documentoId);
	}

}
