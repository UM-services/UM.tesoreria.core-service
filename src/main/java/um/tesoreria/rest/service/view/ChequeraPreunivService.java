/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.view.ChequeraPreuniv;
import um.tesoreria.rest.repository.view.IChequeraPreunivRepository;
import um.tesoreria.rest.model.view.ChequeraPreuniv;
import um.tesoreria.rest.repository.view.IChequeraPreunivRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraPreunivService {

	@Autowired
	private IChequeraPreunivRepository repository;

	public List<ChequeraPreuniv> findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonakeyIn(Integer facultadId,
                                                                                               Integer lectivoId, Integer geograficaId, List<String> keys) {
		return repository.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaKeyIn(facultadId, lectivoId,
				geograficaId, keys);
	}

	public List<ChequeraPreuniv> findAllByFacultadIdAndLectivoId(Integer facultadId, Integer lectivoId) {
		return repository.findAllByFacultadIdAndLectivoId(facultadId, lectivoId);
	}

}
