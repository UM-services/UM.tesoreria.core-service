/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ChequeraSerieControlNotFoundException;
import ar.edu.um.tesoreria.rest.model.ChequeraSerieControl;
import ar.edu.um.tesoreria.rest.repository.IChequeraSerieControlRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraSerieControlService {

	@Autowired
	private IChequeraSerieControlRepository repository;

	public ChequeraSerieControl findLastByTipoChequera(Integer facultadId, Integer tipoChequeraId) {
		return repository.findTopByFacultadIdAndTipoChequeraIdOrderByChequeraSerieIdDesc(facultadId, tipoChequeraId)
				.orElseThrow(() -> new ChequeraSerieControlNotFoundException(facultadId, tipoChequeraId));
	}

	public ChequeraSerieControl findByUnique(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		return repository
				.findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipochequeraId, chequeraserieId)
				.orElseThrow(
						() -> new ChequeraSerieControlNotFoundException(facultadId, tipochequeraId, chequeraserieId));
	}

	public ChequeraSerieControl update(ChequeraSerieControl newChequeraSerieControl, Long chequeraSerieControlId) {
		return repository.findByChequeraSerieControlId(chequeraSerieControlId).map(chequeraSerieControl -> {
			chequeraSerieControl = new ChequeraSerieControl(chequeraSerieControlId,
					newChequeraSerieControl.getFacultadId(), newChequeraSerieControl.getTipoChequeraId(),
					newChequeraSerieControl.getChequeraSerieId(), newChequeraSerieControl.getReemplazada(),
					newChequeraSerieControl.getFacultadIdNueva(), newChequeraSerieControl.getTipoChequeraIdNueva(),
					newChequeraSerieControl.getChequeraSerieIdNueva(), newChequeraSerieControl.getEliminada());
			repository.save(chequeraSerieControl);
			return chequeraSerieControl;
		}).orElseThrow(() -> new ChequeraSerieControlNotFoundException(chequeraSerieControlId));
	}

	public ChequeraSerieControl add(ChequeraSerieControl chequeraSerieControl) {
		repository.save(chequeraSerieControl);
		return chequeraSerieControl;
	}

}
