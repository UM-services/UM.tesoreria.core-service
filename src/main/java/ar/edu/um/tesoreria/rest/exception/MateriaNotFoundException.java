/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class MateriaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4831145298090193641L;

	public MateriaNotFoundException(Integer facultadId, Integer planId, String materiaId) {
		super("Cannot find Materia " + facultadId + "/" + planId + "/" + materiaId);
	}

}
