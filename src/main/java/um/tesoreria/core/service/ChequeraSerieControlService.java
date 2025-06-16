/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraSerieControlException;
import um.tesoreria.core.model.ChequeraSerieControl;
import um.tesoreria.core.repository.ChequeraSerieControlRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraSerieControlService {

	@Autowired
	private ChequeraSerieControlRepository repository;

	public ChequeraSerieControl findLastByTipoChequera(Integer facultadId, Integer tipoChequeraId) {
		return repository.findTopByFacultadIdAndTipoChequeraIdOrderByChequeraSerieIdDesc(facultadId, tipoChequeraId)
				.orElseThrow(() -> new ChequeraSerieControlException(facultadId, tipoChequeraId));
	}

	public ChequeraSerieControl findByUnique(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
		return repository
				.findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipochequeraId, chequeraserieId)
				.orElseThrow(
						() -> new ChequeraSerieControlException(facultadId, tipochequeraId, chequeraserieId));
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
		}).orElseThrow(() -> new ChequeraSerieControlException(chequeraSerieControlId));
	}

	public ChequeraSerieControl add(ChequeraSerieControl chequeraSerieControl) {
		repository.save(chequeraSerieControl);
		return chequeraSerieControl;
	}

}
