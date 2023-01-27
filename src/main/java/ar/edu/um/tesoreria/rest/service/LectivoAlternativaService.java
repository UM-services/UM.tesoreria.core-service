/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.LectivoAlternativaException;
import ar.edu.um.tesoreria.rest.model.LectivoAlternativa;
import ar.edu.um.tesoreria.rest.repository.ILectivoAlternativaRepository;

/**
 * @author daniel
 *
 */
@Service
public class LectivoAlternativaService {

	@Autowired
	private ILectivoAlternativaRepository repository;

	public List<LectivoAlternativa> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
			Integer alternativaId) {
		return repository.findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(facultadId, lectivoId,
				tipoChequeraId, alternativaId);
	}

	public LectivoAlternativa findByFacultadIdAndLectivoIdAndTipochequeraIdAndProductoIdAndAlternativaId(
			Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId) {
		return repository
				.findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaId(facultadId, lectivoId,
						tipoChequeraId, productoId, alternativaId)
				.orElseThrow(() -> new LectivoAlternativaException(facultadId, lectivoId, tipoChequeraId,
						productoId, alternativaId));
	}

}
