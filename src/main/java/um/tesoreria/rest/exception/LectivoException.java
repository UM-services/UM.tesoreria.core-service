/**
 * 
 */
package um.tesoreria.rest.exception;

import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class LectivoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8437455926582914496L;

	public LectivoException(OffsetDateTime fecha) {
		super("Cannot find Lectivo " + fecha);
	}

	public LectivoException(Integer lectivoId) {
		super("Cannot find Lectivo " + lectivoId);
	}

	public LectivoException() {
		super("Cannot find Lectivo");
	}

}
