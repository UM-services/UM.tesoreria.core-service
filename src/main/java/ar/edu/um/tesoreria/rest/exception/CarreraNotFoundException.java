/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class CarreraNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5071966625881038143L;

	public CarreraNotFoundException(Integer facultadId, Integer planId, Integer carreraId) {
		super("Cannot find Carrera " + facultadId + "/" + planId + "/" + carreraId);
	}

}
