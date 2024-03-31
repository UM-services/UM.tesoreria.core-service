/**
 * 
 */
package um.tesoreria.core.exception;

import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class EjercicioException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1992623447183404173L;

	public EjercicioException(Integer ejercicioId) {
		super ("Cannot find Ejercicio " + ejercicioId);
	}

	public EjercicioException(OffsetDateTime fecha) {
		super ("Cannot find Ejercicio " + fecha);
	}

	public EjercicioException(String string) {
		super ("Cannot find Ejercicio " + string);
	}

}
