/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class BajaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7792570689220865772L;

	public BajaException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		super(String.format("Cannot find Baja %d/%d/%d", facultadId, tipochequeraId, chequeraserieId));
	}

	public BajaException(Long bajaId) {
		super(String.format("Cannot find Baja %d", bajaId));
	}

}
