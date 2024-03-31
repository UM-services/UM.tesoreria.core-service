/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class ArancelPorcentajeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -775102210496137215L;

	public ArancelPorcentajeException(Long arancelporcentajeId) {
		super("Cannot found ArancelPorcentaje " + arancelporcentajeId);
	}

	public ArancelPorcentajeException(Integer aranceltipoID, Integer productoID) {
		super("Cannot found ArancelPorcentaje " + aranceltipoID + "/" + productoID);
	}
}
