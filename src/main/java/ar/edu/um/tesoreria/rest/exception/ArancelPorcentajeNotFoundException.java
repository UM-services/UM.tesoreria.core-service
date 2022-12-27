/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ArancelPorcentajeNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -775102210496137215L;

	public ArancelPorcentajeNotFoundException(Long arancelporcentajeId) {
		super("Cannot found ArancelPorcentaje " + arancelporcentajeId);
	}

	public ArancelPorcentajeNotFoundException(Integer aranceltipoID, Integer productoID) {
		super("Cannot found ArancelPorcentaje " + aranceltipoID + "/" + productoID);
	}
}
