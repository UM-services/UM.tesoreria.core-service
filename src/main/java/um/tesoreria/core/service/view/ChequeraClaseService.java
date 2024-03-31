/**
 * 
 */
package um.tesoreria.core.service.view;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.view.ChequeraClaseException;
import um.tesoreria.core.model.view.ChequeraClase;
import um.tesoreria.core.repository.view.IChequeraClaseRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraClaseService {

	@Autowired
	private IChequeraClaseRepository repository;

	public ChequeraClase findChequeraGradoUnica(Integer facultadId, BigDecimal personaId, Integer documentoId,
			Integer lectivoId) {
		return repository
				.findTopByFacultadIdAndPersonaIdAndDocumentoIdAndClaseChequeraIdAndLectivoIdLessThanEqualOrderByLectivoIdDesc(
						facultadId, personaId, documentoId, 2, lectivoId)
				.orElseThrow(
						() -> new ChequeraClaseException(facultadId, personaId, documentoId, 2, lectivoId));
	}

	public ChequeraClase findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClaseChequeraIdIn(
			Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId, List<Integer> clases) {
		return repository.findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClaseChequeraIdIn(facultadId,
				personaId, documentoId, lectivoId, clases).orElseThrow(() -> new ChequeraClaseException());
	}

}
