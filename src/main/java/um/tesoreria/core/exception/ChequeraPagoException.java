/**
 * 
 */
package um.tesoreria.core.exception;

import java.io.Serial;

/**
 * @author daniel
 *
 */
public class ChequeraPagoException extends RuntimeException {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = -5586943026419608961L;

	public ChequeraPagoException(Long chequerapagoID) {
		super("Cannot find ChequeraPago " + chequerapagoID);
	}

    public ChequeraPagoException(String idMercadoPago) { super("Cannot find ChequeraPago with idMercadoPago " + idMercadoPago); }

}
