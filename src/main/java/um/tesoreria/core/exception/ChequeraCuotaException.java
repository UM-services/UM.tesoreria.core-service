/**
 * 
 */
package um.tesoreria.core.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class ChequeraCuotaException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4078598506389815822L;

	public ChequeraCuotaException(Long chequeraCuotaId) {
		super(MessageFormat.format("Cannot find ChequeraCuota {0}", chequeraCuotaId));
	}

	public ChequeraCuotaException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer productoId, Integer alternativaId, Integer cuotaId) {
		super(MessageFormat.format("Cannot find ChequeraCuota {0}/{1}/{2}/{3}/{4}/{5}/{6}", facultadId, tipoChequeraId,
				chequeraSerieId, productoId, alternativaId, cuotaId));
	}

    public ChequeraCuotaException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
		super(MessageFormat.format("Cannot find ChequeraCuota {0}/{1}/{2}/{3}", facultadId, tipoChequeraId,
				chequeraSerieId, alternativaId));
    }
}
