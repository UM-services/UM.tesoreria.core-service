/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class MateriaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4831145298090193641L;

	public MateriaException(Integer facultadId, Integer planId, String materiaId) {
		super("Cannot find Materia " + facultadId + "/" + planId + "/" + materiaId);
	}

}
