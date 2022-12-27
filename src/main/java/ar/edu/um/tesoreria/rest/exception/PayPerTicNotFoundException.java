/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class PayPerTicNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7198703391135198016L;

	public PayPerTicNotFoundException(String payperticId) {
		super("Cannot find PayPerTic " + payperticId);
	}
}
