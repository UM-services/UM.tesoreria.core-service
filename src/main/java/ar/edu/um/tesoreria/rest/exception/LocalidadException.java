/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class LocalidadException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7846783809721999045L;

	public LocalidadException(Integer facultadId, Integer provinciaId, String nombre) {
		super("Cannot find Localidad " + facultadId + "/" + provinciaId + "/" + nombre);
	}

	public LocalidadException(Integer facultadId, Integer provinciaId, Integer localidadId) {
		super("Cannot find Localidad " + facultadId + "/" + provinciaId + "/" + localidadId);
	}

}
