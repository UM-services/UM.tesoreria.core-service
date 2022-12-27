/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class SpoterDataNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7608928781546521044L;

	public SpoterDataNotFoundException(BigDecimal personaId, Integer documentoId, Integer facultadId,
			Integer geograficaId, Integer lectivoId) {
		super(MessageFormat.format("Cannot find SpoterDate {0}/{1}/{2}/{3}/{4}", personaId, documentoId, facultadId,
				geograficaId, lectivoId));
	}

}
