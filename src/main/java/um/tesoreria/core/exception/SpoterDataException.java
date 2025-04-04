/**
 * 
 */
package um.tesoreria.core.exception;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class SpoterDataException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7608928781546521044L;

	public SpoterDataException(BigDecimal personaId, Integer documentoId, Integer facultadId,
			Integer geograficaId, Integer lectivoId) {
		super(MessageFormat.format("Cannot find SpoterDate {0}/{1}/{2}/{3}/{4}", personaId, documentoId, facultadId,
				geograficaId, lectivoId));
	}

}
