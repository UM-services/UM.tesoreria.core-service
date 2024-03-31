/**
 * 
 */
package um.tesoreria.core.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class FacultadPagoCuentaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7639436109130937391L;

	public FacultadPagoCuentaException(Integer facultadId, Integer tipoPagoId) {
		super(MessageFormat.format("Cannot find FacultadPagoCuenta {}/{}", facultadId, tipoPagoId));
	}

}
