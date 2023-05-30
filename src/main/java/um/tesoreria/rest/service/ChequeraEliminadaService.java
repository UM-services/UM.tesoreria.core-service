/**
 * 
 */
package um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.ChequeraEliminadaException;
import um.tesoreria.rest.kotlin.model.ChequeraEliminada;
import um.tesoreria.rest.repository.IChequeraEliminadaRepository;
import um.tesoreria.rest.exception.ChequeraEliminadaException;
import um.tesoreria.rest.repository.IChequeraEliminadaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraEliminadaService {

	@Autowired
	private IChequeraEliminadaRepository repository;

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
