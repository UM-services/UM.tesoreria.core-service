/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraEliminadaException;
import um.tesoreria.core.kotlin.model.ChequeraEliminada;
import um.tesoreria.core.repository.ChequeraEliminadaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraEliminadaService {

	@Autowired
	private ChequeraEliminadaRepository repository;

	public ChequeraEliminada findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
		return repository
				.findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
				.orElseThrow(() -> new ChequeraEliminadaException(facultadId, tipoChequeraId, chequeraSerieId));
	}

	public ChequeraEliminada add(ChequeraEliminada cheqeliminada) {
		repository.save(cheqeliminada);
		return cheqeliminada;
	}

}
