/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

/**
 * @author daniel
 *
 */
public class ValorNotFoundExcepcion extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 805063401992825406L;

	public ValorNotFoundExcepcion(Integer valorId) {
		super("Cannot find Valor " + valorId);
	}

}
