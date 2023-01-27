/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class TipoChequeraException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8278048406744498690L;

	public TipoChequeraException(Integer tipochequeraId) {
		super("Cannot find TipoChequera " + tipochequeraId);
	}

	public TipoChequeraException() {
		super("Cannot find TipoChequera");
	}
}
