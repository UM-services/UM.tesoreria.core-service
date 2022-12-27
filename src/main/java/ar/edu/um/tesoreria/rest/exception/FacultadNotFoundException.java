/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class FacultadNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6624791596889663312L;

	public FacultadNotFoundException(Integer facultadID) {
		super("Cannot find Facultad " + facultadID);
	}

}
