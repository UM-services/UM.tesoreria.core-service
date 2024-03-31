/**
 * 
 */
package um.tesoreria.core.exception;

/**
 * @author daniel
 *
 */
public class CoeficienteInflacionException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4503345517558091893L;

	public CoeficienteInflacionException(Integer anho, Integer mes) {
		super("Cannot find CoeficienteInflacion " + anho + "/" + mes);
	}

	public CoeficienteInflacionException(Long coeficienteinflacionID) {
		super("Cannot find CoeficienteInflacion " + coeficienteinflacionID);
	}
}
