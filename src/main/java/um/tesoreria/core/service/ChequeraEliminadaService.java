/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraEliminadaException;
import um.tesoreria.core.kotlin.model.ChequeraEliminada;
import um.tesoreria.core.repository.ChequeraEliminadaRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ChequeraEliminadaService {

	private final ChequeraEliminadaRepository repository;

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
