/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class SetupException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5376030077391156839L;

	public SetupException() {
		super("Cannot find Setup");
	}
}
