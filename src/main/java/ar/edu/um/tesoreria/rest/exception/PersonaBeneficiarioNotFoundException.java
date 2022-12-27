/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class PersonaBeneficiarioNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1311690530439298042L;

	public PersonaBeneficiarioNotFoundException(Long personaBeneficiarioId) {
		super(MessageFormat.format("Cannot find PersonaBeneficiario {0}", personaBeneficiarioId));
	}

	public PersonaBeneficiarioNotFoundException(Long personaUniqueId, Boolean persona) {
		super(MessageFormat.format("Cannot find PersonaBeneficiario (Persona) {0}", personaUniqueId));
	}

}
