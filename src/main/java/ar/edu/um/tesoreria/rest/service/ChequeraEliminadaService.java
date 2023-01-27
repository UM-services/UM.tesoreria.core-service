/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ChequeraEliminadaException;
import ar.edu.um.tesoreria.rest.model.ChequeraEliminada;
import ar.edu.um.tesoreria.rest.repository.IChequeraEliminadaRepository;

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
