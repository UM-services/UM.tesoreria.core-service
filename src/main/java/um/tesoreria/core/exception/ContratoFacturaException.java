/**
 * 
 */
package um.tesoreria.core.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class ContratoFacturaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4383675677547462027L;

	public ContratoFacturaException(Long contratofacturaId) {
		super("Cannot find ContratoFactura " + contratofacturaId);
	}

	public ContratoFacturaException(BigDecimal personaId, Integer documentoId) {
		super("Cannot find ContratoFactura " + personaId + "/" + documentoId);
	}

}
