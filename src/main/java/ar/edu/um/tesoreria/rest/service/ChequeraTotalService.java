/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.ChequeraTotal;
import ar.edu.um.tesoreria.rest.repository.IChequeraTotalRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraTotalService {

	@Autowired
	private IChequeraTotalRepository repository;

	public List<ChequeraTotal> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
				chequeraSerieId);
	}

	@Transactional
	public List<ChequeraTotal> saveAll(List<ChequeraTotal> chequeraTotals) {
		chequeraTotals = repository.saveAll(chequeraTotals);
		return chequeraTotals;
	}

	@Transactional
	public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId) {
		repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
				chequeraSerieId);
	}

}
