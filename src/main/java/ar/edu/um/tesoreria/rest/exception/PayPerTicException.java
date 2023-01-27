/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class PayPerTicException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7198703391135198016L;

	public PayPerTicException(String payperticId) {
		super("Cannot find PayPerTic " + payperticId);
	}
}
