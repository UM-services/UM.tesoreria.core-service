/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class MailSenderNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4032623962817489332L;

	public MailSenderNotFoundException(Byte enabled) {
		super("Cannot find MailSender (enabled) " + enabled);
	}

}
