/**
 * 
 */
package ar.edu.um.tesoreria.rest.exception;

import java.text.MessageFormat;

/**
 * @author daniel
 *
 */
public class DebitoNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6431676956189668956L;

	public DebitoNotFoundException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId,
			Integer alternativaId, Integer cuotaId, Integer debitoTipoId) {
		super(MessageFormat.format("Cannot find Debito (cuota) {0}/{1}/{2}/{3}/{4}/{5}/{6}", facultadId, tipoChequeraId,
				chequeraSerieId, productoId, alternativaId, cuotaId, debitoTipoId));
	}

	public DebitoNotFoundException(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
			Integer alternativaId) {
		super(MessageFormat.format("Cannot find Debito (alternativa) {0}/{1}/{2}/{3}", facultadId, tipoChequeraId,
				chequeraSerieId, alternativaId));
	}

	public DebitoNotFoundException(Long debitoId) {
		super(MessageFormat.format("Cannot find Debito {0}", debitoId));
	}

	public DebitoNotFoundException(String cbu1, String cbu2) {
		super(MessageFormat.format("Cannot find Debito (cbu) {0} {1}", cbu1, cbu2));
	}

}
