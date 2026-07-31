/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ChequeraPreuniv;
import um.tesoreria.core.repository.view.ChequeraPreunivRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ChequeraPreunivService {

	private final ChequeraPreunivRepository repository;

	public List<ChequeraPreuniv> findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonakeyIn(Integer facultadId,
                                                                                               Integer lectivoId, Integer geograficaId, List<String> keys) {
		return repository.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaKeyIn(facultadId, lectivoId,
				geograficaId, keys);
	}

	public List<ChequeraPreuniv> findAllByFacultadIdAndLectivoId(Integer facultadId, Integer lectivoId) {
		return repository.findAllByFacultadIdAndLectivoId(facultadId, lectivoId);
	}

}
