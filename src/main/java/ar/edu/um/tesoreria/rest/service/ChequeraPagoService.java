/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.ChequeraPago;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ChequeraPagoException;
import ar.edu.um.tesoreria.rest.repository.IChequeraPagoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraPagoService {

	@Autowired
	private IChequeraPagoRepository repository;

	public List<ChequeraPago> findAllByChequera(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipochequeraId,
				chequeraserieId);
	}

	public ChequeraPago findByChequeraPagoId(Long chequeraPagoId) {
		return repository.findByChequeraPagoId(chequeraPagoId)
				.orElseThrow(() -> new ChequeraPagoException(chequeraPagoId));
	}

	@Transactional
	public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipochequeraId,
			Long chequeraserieId) {
		repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipochequeraId,
				chequeraserieId);
	}

	public ChequeraPago add(ChequeraPago chequeraPago) {
		chequeraPago = repository.save(chequeraPago);
		return chequeraPago;
	}

}
