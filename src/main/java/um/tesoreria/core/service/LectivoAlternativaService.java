/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.LectivoAlternativaException;
import um.tesoreria.core.kotlin.model.LectivoAlternativa;
import um.tesoreria.core.repository.LectivoAlternativaRepository;

/**
 * @author daniel
 *
 */
@Service
public class LectivoAlternativaService {

	private final LectivoAlternativaRepository repository;

	public LectivoAlternativaService(LectivoAlternativaRepository repository) {
		this.repository = repository;
	}

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
