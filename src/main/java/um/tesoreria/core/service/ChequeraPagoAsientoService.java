/**
 * 
 */
package um.tesoreria.core.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.ChequeraPagoAsiento;
import um.tesoreria.core.repository.ChequeraPagoAsientoRepository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraPagoAsientoService {

	private final ChequeraPagoAsientoRepository repository;

	public ChequeraPagoAsientoService(ChequeraPagoAsientoRepository repository) {
		this.repository = repository;
	}

	public List<ChequeraPagoAsiento> findAllByTipoPagoIdAndFecha(Integer tipoPagoId, OffsetDateTime fecha) {
		return repository.findAllByTipoPagoIdAndFecha(tipoPagoId, fecha);
	}

	@Transactional
	public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId) {
		repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
				chequeraSerieId);
	}

}
