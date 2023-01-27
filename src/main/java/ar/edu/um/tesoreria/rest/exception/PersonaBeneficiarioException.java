/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class PersonaBeneficiarioException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1311690530439298042L;

	public PersonaBeneficiarioException(Long personaBeneficiarioId) {
		super(MessageFormat.format("Cannot find PersonaBeneficiario {0}", personaBeneficiarioId));
	}

	public PersonaBeneficiarioException(Long personaUniqueId, Boolean persona) {
		super(MessageFormat.format("Cannot find PersonaBeneficiario (Persona) {0}", personaUniqueId));
	}

}
