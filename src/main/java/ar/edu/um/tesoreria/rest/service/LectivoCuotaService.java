/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.LectivoCuota;
import ar.edu.um.tesoreria.rest.repository.ILectivoCuotaRepository;

/**
 * @author daniel
 *
 */
@Service
public class LectivoCuotaService {

	@Autowired
	private ILectivoCuotaRepository repository;

	public List<LectivoCuota> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
			Integer alternativaId) {
		return repository.findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(facultadId, lectivoId,
				tipoChequeraId, alternativaId);
	}

}
