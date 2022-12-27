/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class PlanNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5094855126288410309L;

	public PlanNotFoundException(Integer facultadId, Integer planId) {
		super("Cannot find Plan " + facultadId + "/" + planId);
	}

}
