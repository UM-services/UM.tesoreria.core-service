/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.repository.view.ChequeraSerieAltaRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ChequeraSerieAltaService {

	private final ChequeraSerieAltaRepository repository;

	public List<ChequeraSerieAlta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
																				  Integer geograficaId) {
		return repository.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
	}

}
