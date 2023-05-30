/**
 * 
 */
package um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class ProductoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2093160998381916119L;

	public ProductoException(Integer productoID) {
		super("Cannot find Producto " + productoID);
	}
}
