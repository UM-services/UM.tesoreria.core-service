/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class LectivoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8437455926582914496L;

	public LectivoNotFoundException(OffsetDateTime fecha) {
		super("Cannot find Lectivo " + fecha);
	}

	public LectivoNotFoundException(Integer lectivoId) {
		super("Cannot find Lectivo " + lectivoId);
	}

	public LectivoNotFoundException() {
		super("Cannot find Lectivo");
	}

}
