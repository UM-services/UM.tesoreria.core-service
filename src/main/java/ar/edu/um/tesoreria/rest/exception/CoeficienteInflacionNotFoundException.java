/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

/**
 * @author daniel
 *
 */
public class CoeficienteInflacionNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4503345517558091893L;

	public CoeficienteInflacionNotFoundException(Integer anho, Integer mes) {
		super("Cannot find CoeficienteInflacion " + anho + "/" + mes);
	}

	public CoeficienteInflacionNotFoundException(Long coeficienteinflacionID) {
		super("Cannot find CoeficienteInflacion " + coeficienteinflacionID);
	}
}
