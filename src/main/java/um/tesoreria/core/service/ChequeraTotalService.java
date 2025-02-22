/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraTotalException;
import um.tesoreria.core.model.ChequeraTotal;
import um.tesoreria.core.repository.ChequeraTotalRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraTotalService {

	private final ChequeraTotalRepository repository;

	public ChequeraTotalService(ChequeraTotalRepository repository) {
		this.repository = repository;
	}

	public List<ChequeraTotal> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
				chequeraSerieId);
	}

	public ChequeraTotal findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId) {
		return repository.findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoId(facultadId, tipoChequeraId,
				chequeraSerieId, productoId).orElseThrow(() -> new ChequeraTotalException(facultadId, tipoChequeraId, chequeraSerieId, productoId));
	}

	@Transactional
	public List<ChequeraTotal> saveAll(List<ChequeraTotal> chequeraTotals) {
		return repository.saveAll(chequeraTotals);
	}

	@Transactional
	public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId) {
		repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
				chequeraSerieId);
	}

    public ChequeraTotal update(ChequeraTotal newChequeraTotal, Long chequeraTotalId) {
		return repository.findByChequeraTotalId(chequeraTotalId).map(chequeraTotal -> {
			chequeraTotal = ChequeraTotal.builder()
					.chequeraTotalId(chequeraTotalId)
					.facultadId(newChequeraTotal.getFacultadId())
					.tipoChequeraId(newChequeraTotal.getTipoChequeraId())
					.chequeraSerieId(newChequeraTotal.getChequeraSerieId())
					.productoId(newChequeraTotal.getProductoId())
					.total(newChequeraTotal.getTotal())
					.pagado(newChequeraTotal.getPagado())
					.build();
			return repository.save(chequeraTotal);
		}).orElseThrow(() -> new ChequeraTotalException(chequeraTotalId));
    }

}
