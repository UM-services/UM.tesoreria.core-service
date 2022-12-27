/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class ChequeraCuotaNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4078598506389815822L;

	public ChequeraCuotaNotFoundException(Long chequeraCuotaId) {
		super(MessageFormat.format("Cannot find ChequeraCuota {0}", chequeraCuotaId));
	}

	public ChequeraCuotaNotFoundException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer productoId, Integer alternativaId, Integer cuotaId) {
		super(MessageFormat.format("Cannot find ChequeraCuota {0}/{1}/{2}/{3}/{4}/{5}/{6}", facultadId, tipoChequeraId,
				chequeraSerieId, productoId, alternativaId, cuotaId));
	}

}
