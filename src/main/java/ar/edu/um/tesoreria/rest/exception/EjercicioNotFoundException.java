/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.time.OffsetDateTime;

/**
 * @author daniel
 *
 */
public class EjercicioNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1992623447183404173L;

	public EjercicioNotFoundException(Integer ejercicioId) {
		super ("Cannot find Ejercicio " + ejercicioId);
	}

	public EjercicioNotFoundException(OffsetDateTime fecha) {
		super ("Cannot find Ejercicio " + fecha);
	}

	public EjercicioNotFoundException(String string) {
		super ("Cannot find Ejercicio " + string);
	}

}
