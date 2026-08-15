/**
 * 
 */
package um.tesoreria.core.hexagonal.personas.documento.application.exception;

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

	public DocumentoException(String guaraniTipoDocumento) {
		super("Cannot find Documento with guaraniTipoDocumento " + guaraniTipoDocumento);
	}

}
