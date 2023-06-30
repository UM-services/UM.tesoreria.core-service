/**
 * 
 */
package um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class ProveedorMovimientoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1366272985989940191L;

	public ProveedorMovimientoException(Long proveedorMovimientoId) {
		super("Cannot find ProveedorMovimiento " + proveedorMovimientoId);
	}

	public ProveedorMovimientoException(Integer prefijo, Long numeroComprobante) {
		super(MessageFormat.format("Cannot find ProveedorMovimiento - OrdenPago {0}/{1}", prefijo, numeroComprobante));
	}

    public ProveedorMovimientoException(Integer prefijo) {
		super("Cannot find ProveedorMovimiento prefijo=" + prefijo);
    }
}
