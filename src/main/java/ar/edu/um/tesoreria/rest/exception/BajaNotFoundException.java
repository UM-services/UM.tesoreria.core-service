/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class BajaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7792570689220865772L;

	public BajaNotFoundException(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		super(String.format("Cannot find Baja %d/%d/%d", facultadId, tipochequeraId, chequeraserieId));
	}

	public BajaNotFoundException(Long bajaId) {
		super(String.format("Cannot find Baja %d", bajaId));
	}

}
