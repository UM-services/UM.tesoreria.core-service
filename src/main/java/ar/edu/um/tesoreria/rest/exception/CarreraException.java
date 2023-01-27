/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class CarreraException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5071966625881038143L;

	public CarreraException(Integer facultadId, Integer planId, Integer carreraId) {
		super("Cannot find Carrera " + facultadId + "/" + planId + "/" + carreraId);
	}

}
