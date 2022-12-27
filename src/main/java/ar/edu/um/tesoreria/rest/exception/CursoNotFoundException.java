/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class CursoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6397067520729900565L;

	public CursoNotFoundException(Integer claseChequeraId) {
		super(MessageFormat.format("Cannot find Curso (claseChequeraId) -> {0}", claseChequeraId));
	}

}
