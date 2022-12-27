/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class SetupNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5376030077391156839L;

	public SetupNotFoundException() {
		super("Cannot find Setup");
	}
}
