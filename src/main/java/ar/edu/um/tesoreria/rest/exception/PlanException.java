/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class PlanException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5094855126288410309L;

	public PlanException(Integer facultadId, Integer planId) {
		super("Cannot find Plan " + facultadId + "/" + planId);
	}

}
