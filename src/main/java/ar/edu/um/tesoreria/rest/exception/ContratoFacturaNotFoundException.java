/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.math.BigDecimal;

/**
 * @author daniel
 *
 */
public class ContratoFacturaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4383675677547462027L;

	public ContratoFacturaNotFoundException(Long contratofacturaId) {
		super("Cannot find ContratoFactura " + contratofacturaId);
	}

	public ContratoFacturaNotFoundException(BigDecimal personaId, Integer documentoId) {
		super("Cannot find ContratoFactura " + personaId + "/" + documentoId);
	}

}
