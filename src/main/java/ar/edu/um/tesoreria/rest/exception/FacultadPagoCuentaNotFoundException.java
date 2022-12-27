/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class FacultadPagoCuentaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7639436109130937391L;

	public FacultadPagoCuentaNotFoundException(Integer facultadId, Integer tipoPagoId) {
		super(MessageFormat.format("Cannot find FacultadPagoCuenta {}/{}", facultadId, tipoPagoId));
	}

}
