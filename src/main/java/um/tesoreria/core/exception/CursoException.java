/**
 * 
 */
package um.tesoreria.core.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class CursoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6397067520729900565L;

	public CursoException(Integer claseChequeraId) {
		super(MessageFormat.format("Cannot find Curso (claseChequeraId) -> {0}", claseChequeraId));
	}

}
