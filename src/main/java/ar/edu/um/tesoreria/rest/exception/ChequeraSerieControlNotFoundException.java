/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class ChequeraSerieControlNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2398415338213392637L;

	public ChequeraSerieControlNotFoundException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		super("Cannot find ChequeraSerieControl " + facultadId + "/" + tipoChequeraId + "/" + chequeraSerieId);
	}

	public ChequeraSerieControlNotFoundException(Long chequeraSerieControlId) {
		super("Cannot find ChequeraSerieControl " + chequeraSerieControlId);
	}

	public ChequeraSerieControlNotFoundException(Integer facultadId, Integer tipoChequeraId) {
		super(MessageFormat.format("Cannot find ChequeraSerieControl {0}/{1}", facultadId, tipoChequeraId));
	}
}
