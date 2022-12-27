/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ChequeraAlternativaNotFoundException;
import ar.edu.um.tesoreria.rest.model.ChequeraAlternativa;
import ar.edu.um.tesoreria.rest.repository.IChequeraAlternativaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraAlternativaService {

	@Autowired
	private IChequeraAlternativaRepository repository;

	public List<ChequeraAlternativa> findAllByChequera(Integer facultadId, Integer tipochequeraId, Long chequeraserieId,
			Integer alternativaId) {
		return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId,
				tipochequeraId, chequeraserieId, alternativaId);
	}

	public ChequeraAlternativa findByUnique(Integer facultadId, Integer tipochequeraId, Long chequeraserieId,
			Integer productoId, Integer alternativaId) {
		return repository
				.findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaId(facultadId,
						tipochequeraId, chequeraserieId, productoId, alternativaId)
				.orElseThrow(() -> new ChequeraAlternativaNotFoundException(facultadId, tipochequeraId, chequeraserieId,
						productoId, alternativaId));
	}

	@Transactional
	public List<ChequeraAlternativa> saveAll(List<ChequeraAlternativa> chequeraAlternativas) {
		chequeraAlternativas = repository.saveAll(chequeraAlternativas);
		return chequeraAlternativas;
	}

	@Transactional
	public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipochequeraId,
			Long chequeraserieId) {
		repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipochequeraId,
				chequeraserieId);
	}

}
