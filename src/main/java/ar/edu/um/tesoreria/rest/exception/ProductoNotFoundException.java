/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ProductoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2093160998381916119L;

	public ProductoNotFoundException(Integer productoID) {
		super("Cannot find Producto " + productoID);
	}
}
