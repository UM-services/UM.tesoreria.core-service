/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;
import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class EjercicioBloqueadoException extends RuntimeException {

	private static final long serialVersionUID = -3664445128796544585L;

	public EjercicioBloqueadoException(OffsetDateTime fechaContable) {
		super(MessageFormat.format("Ejercicio Bloqueado {}", fechaContable));
	}

}
