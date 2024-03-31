/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class MailSenderException extends RuntimeException {

	private static final long serialVersionUID = 4032623962817489332L;

	public MailSenderException(Byte enabled) {
		super("Cannot find MailSender (enabled) " + enabled);
	}

}
