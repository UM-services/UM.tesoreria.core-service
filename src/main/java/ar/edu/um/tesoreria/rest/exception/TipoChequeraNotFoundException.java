/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class TipoChequeraNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8278048406744498690L;

	public TipoChequeraNotFoundException(Integer tipochequeraId) {
		super("Cannot find TipoChequera " + tipochequeraId);
	}

	public TipoChequeraNotFoundException() {
		super("Cannot find TipoChequera");
	}
}
