/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class ProvinciaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3995756238321042389L;

	public ProvinciaException(Long unicoId) {
		super("Cannot find Provincia " + unicoId);
	}

	public ProvinciaException(Integer facultadId, Integer provinciaId) {
		super("Cannot find Provincia " + facultadId + "/" + provinciaId);
	}

	public ProvinciaException(Integer facultadId, String nombre) {
		super("Cannot find Provincia " + facultadId + "/" + nombre);
	}

}
