/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ProvinciaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3995756238321042389L;

	public ProvinciaNotFoundException(Long unicoId) {
		super("Cannot find Provincia " + unicoId);
	}

	public ProvinciaNotFoundException(Integer facultadId, Integer provinciaId) {
		super("Cannot find Provincia " + facultadId + "/" + provinciaId);
	}

	public ProvinciaNotFoundException(Integer facultadId, String nombre) {
		super("Cannot find Provincia " + facultadId + "/" + nombre);
	}

}
